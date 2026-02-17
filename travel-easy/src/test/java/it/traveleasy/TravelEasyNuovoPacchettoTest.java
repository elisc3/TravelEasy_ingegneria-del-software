package it.traveleasy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

import it.traveleasy.testsupport.BaseTravelEasyTest;
import it.traveleasy.testsupport.TestDbSupport;

class TravelEasyNuovoPacchettoTest extends BaseTravelEasyTest {

    @Test
    void validazioneNuovoPacchetto_conDatiValidi_restituiscePrezzo() {
        float result = te.validazioneDatiNuovoPacchetto(
            "Titolo", "Milano", "Italia", "Descrizione", "999.99",
            "SkyItalia", "Hotel Laguna", "20-03-2026", "25-03-2026");

        assertEquals(999.99f, result, 0.001f);
    }

    @Test
    void validazioneNuovoPacchetto_conCampiVuoti_restituisceMenoUno() {
        float result = te.validazioneDatiNuovoPacchetto(
            "", "Milano", "Italia", "Descrizione", "999.99",
            "SkyItalia", "Hotel Laguna", "20-03-2026", "25-03-2026");

        assertEquals(-1.0f, result, 0.001f);
    }

    @Test
    void coerenzaDate_conPartenzaDopoArrivo_restituisceFalse() {
        boolean result = te.coerenzaDate("25-03-2026", "20-03-2026");
        assertTrue(!result);
    }

    @Test
    void nuovoPacchetto_conDatiValidi_inserisceNuovaRiga() throws Exception {
        int before = TestDbSupport.countRows(conn, "PacchettiViaggio");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String dataPartenza = LocalDate.now().plusDays(20).format(fmt);
        String dataRitorno = LocalDate.now().plusDays(25).format(fmt);

        boolean created = te.nuovoPacchetto(
            conn,
            "PKG9001",
            "Londra Sprint",
            "Londra",
            "Regno Unito",
            "City break",
            799.0f,
            2.0f,
            1,
            "SkyItalia",
            "Hotel Laguna",
            dataPartenza,
            dataRitorno
        );

        assertTrue(created);
        assertEquals(before + 1, TestDbSupport.countRows(conn, "PacchettiViaggio"));

        try (PreparedStatement ps = conn.prepareStatement(
            "SELECT COUNT(*) FROM PacchettiViaggio WHERE Codice = ?")) {
            ps.setString(1, "PKG9001");
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(1, rs.getInt(1));
            }
        }
    }
}

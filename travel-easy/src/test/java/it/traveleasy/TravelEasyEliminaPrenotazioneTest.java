package it.traveleasy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

import it.traveleasy.testsupport.BaseTravelEasyTest;

class TravelEasyEliminaPrenotazioneTest extends BaseTravelEasyTest {

    private PacchettoViaggio pacchettoConPartenzaTra(int giorni) {
        String partenza = LocalDate.now().plusDays(giorni).format(DateTimeFormatter.ofPattern("dd-MM-uuuu"));
        String ritorno = LocalDate.now().plusDays(giorni + 3).format(DateTimeFormatter.ofPattern("dd-MM-uuuu"));
        return new PacchettoViaggio(
            1, "TMP1", "Tmp", "Roma", "Italia",
            partenza, ritorno, "tmp", 300.0f, 0.0f, 1, 1, 1, conn
        );
    }

    @Test
    void eliminaPrenotazione_conPiudiSetteGiorni_restituisceSuccessoERimuoveRecord() throws Exception {
        Prenotazione p = te.getPrenotazioneById(1);
        assertNotNull(p);
        p.setPacchetto(pacchettoConPartenzaTra(10));
        float rimborso = te.getRimborsoEliminazionePrenotazione(p);

        int esito = te.eliminaPrenotazione(p, rimborso);
        assertEquals(0, esito);

        try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM Prenotazioni WHERE id = ?")) {
            ps.setInt(1, 1);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(0, rs.getInt(1));
            }
        }

        try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM Viaggiatore WHERE Prenotazione = ?")) {
            ps.setInt(1, 1);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(0, rs.getInt(1));
            }
        }
    }

    @Test
    void eliminaPrenotazione_conPartenzaEntroDueGiorni_restituisceNonEliminabile() throws Exception {
        Prenotazione p = te.getPrenotazioneById(1);
        assertNotNull(p);
        p.setPacchetto(pacchettoConPartenzaTra(1));
        float rimborso = te.getRimborsoEliminazionePrenotazione(p);

        int esito = te.eliminaPrenotazione(p, rimborso);
        assertEquals(-3, esito);
    }
}

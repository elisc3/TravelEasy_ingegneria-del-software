package it.traveleasy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

import it.traveleasy.testsupport.BaseTravelEasyTest;

class TravelEasyCheckInTest extends BaseTravelEasyTest {

    private PacchettoViaggio pacchettoConPartenzaTra(int giorni) {
        String partenza = LocalDate.now().plusDays(giorni).format(DateTimeFormatter.ofPattern("dd-MM-uuuu"));
        String ritorno = LocalDate.now().plusDays(giorni + 4).format(DateTimeFormatter.ofPattern("dd-MM-uuuu"));
        return new PacchettoViaggio(
            1, "CHK1", "CheckIn", "Roma", "Italia",
            partenza, ritorno, "tmp", 200.0f, 0.0f, 1, 1, 1, conn
        );
    }

    @Test
    void checkIn_conPartenzaEntroDueGiorni_riesceEAggiornaDb() throws Exception {
        Cliente cliente = te.getAccountToHomeView("cliente@example.com").getCliente();
        Prenotazione p = new Prenotazione(1, cliente, pacchettoConPartenzaTra(1), "", 0.0f, 0.0f, 0.0f, false);

        boolean ok = te.effettuaCheckIn(p) == 0;
        assertTrue(ok);
        assertTrue(p.isCheckedIn());

        try (PreparedStatement ps = conn.prepareStatement("SELECT CheckIn FROM Prenotazioni WHERE id = 1")) {
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(1, rs.getInt("CheckIn"));
            }
        }
    }

    @Test
    void checkIn_conPartenzaPiuLontanaFallisce() {
        Cliente cliente = te.getAccountToHomeView("cliente@example.com").getCliente();
        Prenotazione p = new Prenotazione(1, cliente, pacchettoConPartenzaTra(6), "", 0.0f, 0.0f, 0.0f, false);

        boolean ok = te.effettuaCheckIn(p) == 0;
        assertFalse(ok);
    }

    @Test
    void checkIn_giaEffettuato_nonPermetteSecondoTentativo() {
        Cliente cliente = te.getAccountToHomeView("cliente@example.com").getCliente();
        Prenotazione p = new Prenotazione(1, cliente, pacchettoConPartenzaTra(1), "", 0.0f, 0.0f, 0.0f, false);

        assertTrue(te.effettuaCheckIn(p) == 0);
        assertFalse(te.effettuaCheckIn(p) == 0);
    }
}

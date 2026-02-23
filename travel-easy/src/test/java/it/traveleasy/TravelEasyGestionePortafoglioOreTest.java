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

class TravelEasyGestionePortafoglioOreTest extends BaseTravelEasyTest {

    @Test
    void incrementaOre_superaSogliaMultiplaAggiornaSconto() throws Exception {
        Cliente cliente = te.getAccountToHomeView("cliente@example.com").getCliente();
        PortafoglioOre po = cliente.getPo();

        boolean ok = po.incrementaOre(conn, 5.0f); // 15 -> 20, sconto 6
        assertTrue(ok);
        assertEquals(0.0f, po.getOre(), 0.001f);
        assertEquals(6.0f, po.getSconto(), 0.001f);

        try (PreparedStatement ps = conn.prepareStatement(
            "SELECT Ore, Sconto FROM PortafoglioOre WHERE id = ?")) {
            ps.setInt(1, po.getId());
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(0.0f, rs.getFloat("Ore"), 0.001f);
                assertEquals(6.0f, rs.getFloat("Sconto"), 0.001f);
            }
        }
    }

    @Test
    void incrementaOre_sottoNuovaSogliaMantieneScontoCorrente() {
        Cliente cliente = te.getAccountToHomeView("cliente@example.com").getCliente();
        PortafoglioOre po = cliente.getPo();

        boolean ok = po.incrementaOre(conn, 2.0f); // 15 -> 17, resta 3
        assertTrue(ok);
        assertEquals(7.0f, po.getOre(), 0.001f);
        assertEquals(3.0f, po.getSconto(), 0.001f);
    }

    @Test
    void aggiornaSconto_conMenoDiDieciOre_nonModificaSconto() {
        PortafoglioOre po = new PortafoglioOre(99, 1, 8.5f, 0.0f);
        boolean ok = po.aggiornaSconto();
        assertTrue(ok);
        assertEquals(0.0f, po.getSconto(), 0.001f);
    }

    @Test
    void applicaScontoDb_conValoriEspliciti_persistaCorrettaRiga() throws Exception {
        Cliente cliente = te.getAccountToHomeView("cliente@example.com").getCliente();
        PortafoglioOre po = cliente.getPo();
        po.setOre(31.0f);
        po.setSconto(9.0f);

        boolean ok = po.applicaScontoDB(conn);
        assertTrue(ok);

        try (PreparedStatement ps = conn.prepareStatement(
            "SELECT Ore, Sconto FROM PortafoglioOre WHERE id = ?")) {
            ps.setInt(1, po.getId());
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(31.0f, rs.getFloat("Ore"), 0.001f);
                assertEquals(9.0f, rs.getFloat("Sconto"), 0.001f);
            }
        }
    }

    @Test
    void decrementaOre_conEliminazionePrenotazione_aggiornaPortafoglioOre() throws Exception {
        Cliente cliente = te.getAccountToHomeView("cliente@example.com").getCliente();
        PortafoglioOre po = cliente.getPo();
        Prenotazione prenotazione = te.getPrenotazioneById(1);
        assertNotNull(prenotazione);

        String partenza = LocalDate.now().plusDays(10).format(DateTimeFormatter.ofPattern("dd-MM-uuuu"));
        String ritorno = LocalDate.now().plusDays(14).format(DateTimeFormatter.ofPattern("dd-MM-uuuu"));
        PacchettoViaggio pacchetto = new PacchettoViaggio(
            1, "TMP-DEL", "Tmp", "Roma", "Italia", partenza, ritorno, "tmp", 300.0f, 3.5f, 1, 1, 1, conn);
        prenotazione.setPacchetto(pacchetto);

        int esito = te.eliminaPrenotazione(prenotazione, 50.0f);
        assertEquals(0, esito);
        assertEquals(1.5f, po.getOre(), 0.001f);
        assertEquals(6.0f, po.getSconto(), 0.001f);

        try (PreparedStatement ps = conn.prepareStatement(
            "SELECT Ore, Sconto FROM PortafoglioOre WHERE id = ?")) {
            ps.setInt(1, po.getId());
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(1.5f, rs.getFloat("Ore"), 0.001f);
                assertEquals(6.0f, rs.getFloat("Sconto"), 0.001f);
            }
        }
    }

    //AGGIUNGERE DECREMENTO ORE NEL CASO DI ELIMINAZIONE PRENOTAZIONE
}

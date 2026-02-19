package it.traveleasy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import org.junit.jupiter.api.Test;

import it.traveleasy.testsupport.BaseTravelEasyTest;

class TravelEasyModificaPrenotazioneTest extends BaseTravelEasyTest {

    @Test
    void modificaViaggiatori_sostituisceInteramenteIRecordDellaPrenotazione() throws Exception {
        Prenotazione p = te.getPrenotazioneById(1);

        Viaggiatore v1 = new Viaggiatore("Anna", "Nuova", "10-10-1992", "Passaporto", "PP1234567");
        v1.setSediaRotelle(true);
        Viaggiatore v2 = new Viaggiatore("Luca", "Nuovo", "11-11-1991", "Patente di guida", "PT12345678");
        v2.setCecita(true);

        boolean ok = te.modificaViaggiatori(p, List.of(v1, v2));
        assertTrue(ok);

        try (PreparedStatement countPs = conn.prepareStatement(
            "SELECT COUNT(*) FROM Viaggiatore WHERE Prenotazione = ?")) {
            countPs.setInt(1, p.getId());
            try (ResultSet rs = countPs.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(2, rs.getInt(1));
            }
        }

        try (PreparedStatement rowPs = conn.prepareStatement(
            "SELECT Nome, Cognome, SediaRotelle, \"Cecità\" FROM Viaggiatore WHERE Prenotazione = ? ORDER BY id")) {
            rowPs.setInt(1, p.getId());
            try (ResultSet rs = rowPs.executeQuery()) {
                assertTrue(rs.next());
                assertEquals("Anna", rs.getString("Nome"));
                assertEquals("Nuova", rs.getString("Cognome"));
                assertEquals(1, rs.getInt("SediaRotelle"));

                assertTrue(rs.next());
                assertEquals("Luca", rs.getString("Nome"));
                assertEquals("Nuovo", rs.getString("Cognome"));
                assertEquals(1, rs.getInt("Cecità"));
            }
        }
    }

    @Test
    void annullaPrenotazioneBozza_rimuovePrenotazioneEViaggiatori() throws Exception {
        Cliente cliente = te.getAccountToHomeView("cliente@example.com").getCliente();
        PacchettoViaggio pacchetto = te.getElencoPacchetti().get(2);

        int idBozza = te.createPrenotazione(pacchetto, cliente.getId());
        assertTrue(idBozza > 0);
        assertTrue(te.createViaggiatore(idBozza, "Bozza", "Uno", "01-01-1990", "Passaporto", "BZ1234567"));

        boolean annullata = te.annullaPrenotazioneBozza(idBozza);
        assertTrue(annullata);
        assertNull(te.getPrenotazioneById(idBozza));

        try (PreparedStatement pPs = conn.prepareStatement("SELECT COUNT(*) FROM Prenotazioni WHERE id = ?")) {
            pPs.setInt(1, idBozza);
            try (ResultSet rs = pPs.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(0, rs.getInt(1));
            }
        }

        try (PreparedStatement vPs = conn.prepareStatement("SELECT COUNT(*) FROM Viaggiatore WHERE Prenotazione = ?")) {
            vPs.setInt(1, idBozza);
            try (ResultSet rs = vPs.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(0, rs.getInt(1));
            }
        }
    }
}

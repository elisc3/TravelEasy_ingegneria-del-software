package it.traveleasy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.junit.jupiter.api.Test;

import it.traveleasy.testsupport.BaseTravelEasyTest;

class TravelEasyModificaPrenotazioneTest extends BaseTravelEasyTest {

    @Test
    void modificaPacchettoPrenotazione_aggiornaPacchettoETotale() throws Exception {
        Prenotazione p = te.getPrenotazioneById(1);
        assertNotNull(p);

        PacchettoViaggio nuovoPacchetto = te.getElencoPacchetti().get(2);
        assertNotNull(nuovoPacchetto);

        boolean ok = te.modificaPacchettoPrenotazione(p, nuovoPacchetto);
        assertTrue(ok);
        assertEquals(nuovoPacchetto.getId(), p.getPacchetto().getId());

        float expectedTotale = te.getTotalePrenotazione(p.getCliente(), nuovoPacchetto, p.getElencoViaggiatori());
        assertEquals(expectedTotale, p.getPrezzoTotale(), 0.001f);

        try (PreparedStatement ps = conn.prepareStatement(
            "SELECT Pacchetto, PrezzoTotale FROM Prenotazioni WHERE id = ?")) {
            ps.setInt(1, p.getId());
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(nuovoPacchetto.getId(), rs.getInt("Pacchetto"));
                assertEquals(expectedTotale, rs.getFloat("PrezzoTotale"), 0.001f);
            }
        }
    }

    @Test
    void modificaViaggiatori_sostituisceInteramenteIRecordDellaPrenotazione() throws Exception {
        Prenotazione p = te.getPrenotazioneById(1);
        assertNotNull(p);

        Viaggiatore v1 = new Viaggiatore("Anna", "Nuova", "10-10-1992", "Passaporto", "PP1234567");
        v1.setSediaRotelle(true);
        Viaggiatore v2 = new Viaggiatore("Luca", "Nuovo", "11-11-1991", "Patente di guida", "PT12345678");
        v2.setCecita(true);

        int ok1 = te.modificaViaggiatori(
            p,
            v1.getNome(),
            v1.getCognome(),
            v1.getDataNascita(),
            v1.getTipoDocumento(),
            v1.getCodiceDocumento(),
            v1.isCecita(),
            v1.isSediaRotelle(),
            0
        );
        int ok2 = te.modificaViaggiatori(
            p,
            v2.getNome(),
            v2.getCognome(),
            v2.getDataNascita(),
            v2.getTipoDocumento(),
            v2.getCodiceDocumento(),
            v2.isCecita(),
            v2.isSediaRotelle(),
            1
        );
        assertEquals(0, ok1);
        assertEquals(0, ok2);
        assertTrue(p.replaceViaggiatoriDB(conn));

        try (PreparedStatement countPs = conn.prepareStatement(
            "SELECT COUNT(*) FROM Viaggiatore WHERE Prenotazione = ?")) {
            countPs.setInt(1, p.getId());
            try (ResultSet rs = countPs.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(2, rs.getInt(1));
            }
        }

        try (PreparedStatement rowPs = conn.prepareStatement(
            "SELECT * FROM Viaggiatore WHERE Prenotazione = ? ORDER BY id")) {
            rowPs.setInt(1, p.getId());
            try (ResultSet rs = rowPs.executeQuery()) {
                assertTrue(rs.next());
                assertEquals("Anna", rs.getString("Nome"));
                assertEquals("Nuova", rs.getString("Cognome"));
                assertEquals(1, rs.getInt(8));

                assertTrue(rs.next());
                assertEquals("Luca", rs.getString("Nome"));
                assertEquals("Nuovo", rs.getString("Cognome"));
                assertEquals(1, rs.getInt(9));
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

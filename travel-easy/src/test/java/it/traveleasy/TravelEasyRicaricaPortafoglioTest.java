package it.traveleasy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.jupiter.api.Test;

import it.traveleasy.testsupport.BaseTravelEasyTest;

class TravelEasyRicaricaPortafoglioTest extends BaseTravelEasyTest {

    @Test
    void validazioneRicarica_conDatiValidi_restituisceImporto() {
        Cliente cliente = te.getAccountToHomeView("cliente@example.com").getCliente();
        float result = te.validazioneDatiNuovaRicarica(
            "4111111111111111", "12/29", "123", "150.50", cliente);
        assertEquals(150.50f, result, 0.001f);
    }

    @Test
    void validazioneRicarica_conCampiVuoti_restituisceMenoUno() {
        Cliente cliente = te.getAccountToHomeView("cliente@example.com").getCliente();
        float result = te.validazioneDatiNuovaRicarica("", "", "", "", cliente);
        assertEquals(-1.0f, result, 0.001f);
    }

    @Test
    void validazioneRicarica_conCvvNonNumerico_restituisceMenoTre() {
        Cliente cliente = te.getAccountToHomeView("cliente@example.com").getCliente();
        float result = te.validazioneDatiNuovaRicarica(
            "4111111111111111", "12/29", "abc", "10", cliente);
        assertEquals(-3.0f, result, 0.001f);
    }

    @Test
    void validazioneRicarica_conScadenzaInvalida_restituisceMenoQuattro() {
        Cliente cliente = te.getAccountToHomeView("cliente@example.com").getCliente();
        float result = te.validazioneDatiNuovaRicarica(
            "4111111111111111", "2029-12", "123", "10", cliente);
        assertEquals(-4.0f, result, 0.001f);
    }

    @Test
    void insertCartaCredito_conDatiValidi_aggiornaTabellaCartaCredito() throws Exception {
        Cliente cliente = te.getAccountToHomeView("cliente@example.com").getCliente();
        boolean updated = te.insertCartaCredito(cliente, "5555555555554444", "10/30", "222", "Mastercard");
        assertTrue(updated);

        try (PreparedStatement ps = conn.prepareStatement(
            "SELECT NumeroCarta, Scadenza, cvv, Circuito FROM CartaCredito WHERE Utente = 1");
             ResultSet rs = ps.executeQuery()) {
            assertTrue(rs.next());
            assertEquals("5555555555554444", rs.getString("NumeroCarta"));
            assertEquals("10/30", rs.getString("Scadenza"));
            assertEquals("222", rs.getString("cvv"));
            assertEquals("Mastercard", rs.getString("Circuito"));
        }
    }

    @Test
    void validazioneRicarica_conCvvErrato_restituisceMenoSette() {
        Cliente cliente = te.getAccountToHomeView("cliente@example.com").getCliente();
        float result = te.validazioneDatiNuovaRicarica(
            "4111111111111111", "12/29", "999", "10", cliente);
        assertEquals(-7.0f, result, 0.001f);
    }

    @Test
    void insertCartaCredito_conCartaGiaPresente_aggiornaSenzaDuplicareRecord() throws Exception {
        Cliente cliente = te.getAccountToHomeView("cliente@example.com").getCliente();

        int before;
        try (PreparedStatement ps = conn.prepareStatement(
            "SELECT COUNT(*) FROM CartaCredito WHERE Utente = ?")) {
            ps.setInt(1, cliente.getId());
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                before = rs.getInt(1);
            }
        }

        assertTrue(te.insertCartaCredito(cliente, "4000000000000002", "11/30", "321", "VISA"));
        assertTrue(te.insertCartaCredito(cliente, "378282246310005", "09/31", "456", "AMEX"));

        int after;
        try (PreparedStatement ps = conn.prepareStatement(
            "SELECT COUNT(*) FROM CartaCredito WHERE Utente = ?")) {
            ps.setInt(1, cliente.getId());
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                after = rs.getInt(1);
            }
        }
        assertEquals(before, after);

        try (PreparedStatement ps = conn.prepareStatement(
            "SELECT NumeroCarta, Scadenza, cvv, Circuito FROM CartaCredito WHERE Utente = ?")) {
            ps.setInt(1, cliente.getId());
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals("378282246310005", rs.getString("NumeroCarta"));
                assertEquals("09/31", rs.getString("Scadenza"));
                assertEquals("456", rs.getString("cvv"));
                assertEquals("AMEX", rs.getString("Circuito"));
            }
        }
    }

    //testare cvv errato
    //testare il caso in cui la carta è già inserita
}

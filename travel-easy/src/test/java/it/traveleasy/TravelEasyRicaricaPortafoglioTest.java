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
        float result = te.validazioneDatiNuovaRicarica(
            "4111111111111111", "12/29", "123", "150.50");
        assertEquals(150.50f, result, 0.001f);
    }

    @Test
    void validazioneRicarica_conCampiVuoti_restituisceMenoUno() {
        float result = te.validazioneDatiNuovaRicarica("", "", "", "");
        assertEquals(-1.0f, result, 0.001f);
    }

    @Test
    void validazioneRicarica_conCvvNonNumerico_restituisceMenoTre() {
        float result = te.validazioneDatiNuovaRicarica(
            "4111111111111111", "12/29", "abc", "10");
        assertEquals(-3.0f, result, 0.001f);
    }

    @Test
    void validazioneRicarica_conScadenzaInvalida_restituisceMenoQuattro() {
        float result = te.validazioneDatiNuovaRicarica(
            "4111111111111111", "2029-12", "123", "10");
        assertEquals(-4.0f, result, 0.001f);
    }

    @Test
    void insertCartaCredito_conDatiValidi_aggiornaTabellaCartaCredito() throws Exception {
        boolean updated = te.insertCartaCredito(1, "5555555555554444", "10/30", "222", "Mastercard");
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
}
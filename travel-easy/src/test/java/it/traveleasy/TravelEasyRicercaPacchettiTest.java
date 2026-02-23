package it.traveleasy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import it.traveleasy.testsupport.BaseTravelEasyTest;

class TravelEasyRicercaPacchettiTest extends BaseTravelEasyTest {

    @Test
    void ricercaPacchetti_conFiltriValidi_restituiscePacchettoAtteso() {
        List<PacchettoViaggio> results = te.ricercaPacchetti("Amsterdam", "17-04-2026", "20-04-2026", 1000.0f);

        assertEquals(1, results.size());
        assertEquals("PKG1001", results.get(0).getCodice());
    }

    @Test
    void ricercaPacchetti_conPrezzoMassimoTroppoBasso_nonRestituisceRisultati() {
        List<PacchettoViaggio> results = te.ricercaPacchetti("Amsterdam", "17-04-2026", "20-04-2026", 800.0f);

        assertTrue(results.isEmpty());
    }

    @Test
    void ricercaPacchetti_nonMostraPacchettiNonVisibili() {
        List<PacchettoViaggio> results = te.ricercaPacchetti("Tokyo", "01-06-2026", "10-06-2026", 3000.0f);

        assertTrue(results.isEmpty());
    }

    @Test
    void coerenzaDate_conDateCorrette_restituisceTrue() {
        assertTrue(te.coerenzaDate("20-03-2026", "21-03-2026"));
    }
}

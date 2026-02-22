package it.traveleasy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import it.traveleasy.testsupport.BaseTravelEasyTest;

class TravelEasyAssistenzaSpecialeTest extends BaseTravelEasyTest {

    @Test
    void gestioneAssistenzaSpeciale_calcolaPrezzoTotaleCorretto() {
        Prenotazione p = te.getPrenotazioneById(1);
        List<Viaggiatore> viaggiatori = p.getElencoViaggiatori();

        Viaggiatore primo = viaggiatori.get(0);
        Viaggiatore secondo = viaggiatori.get(1);

        te.onAssistenzaChanged(p, primo, "SediaRotelle", true);
        te.onAssistenzaChanged(p, secondo, "Cecità", true);
        te.confermaAssistenzaSpeciale(p);

        assertTrue(primo.isSediaRotelle());
        assertTrue(secondo.isCecita());
        assertEquals(60.0f, p.getPrezzoAssistenzaSpeciale(), 0.001f);
    }
}

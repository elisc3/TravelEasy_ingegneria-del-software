package it.traveleasy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.Test;

import it.traveleasy.testsupport.BaseTravelEasyTest;

class TravelEasyVisualizzaPrenotazioniOperatoreTest extends BaseTravelEasyTest {

    @Test
    void getPrenotazioni_allAvvio_restituiscePrenotazioniDaDatabase() {
        List<Prenotazione> prenotazioni = te.getPrenotazioni();
        assertEquals(1, prenotazioni.size());
        assertEquals("16-02-2026", prenotazioni.get(0).getDataPrenotazione());
    }

    @Test
    void getPrenotazioni_contieneDettagliClienteEPacchetto() {
        Prenotazione p = te.getPrenotazioni().get(0);
        assertNotNull(p.getCliente());
        assertNotNull(p.getPacchetto());
        assertEquals("Mario", p.getCliente().getNome());
        assertEquals("PKG1001", p.getPacchetto().getCodice());
    }

    @Test
    void getPrenotazioni_contieneElencoViaggiatoriCaricato() {
        Prenotazione p = te.getPrenotazioni().get(0);
        assertNotNull(p.getElencoViaggiatori());
        assertEquals(2, p.getElencoViaggiatori().size());
    }

    @Test
    void getPrenotazioni_dopoNuovaPrenotazione_contieneNuovoElemento() {
        Cliente cliente = te.getAccountToHomeView("cliente@example.com").getCliente();
        PacchettoViaggio pacchetto = te.getElencoPacchetti().get(2);
        List<Viaggiatore> viaggiatori = List.of(
            new Viaggiatore("Sara", "Test", "08-08-1991", "Passaporto", "MN1234567")
        );
        boolean ok = te.registrazionePrenotazione(cliente, pacchetto, viaggiatori, 0.0f, 480.0f, 0.0f);
        assertTrue(ok);

        List<Prenotazione> prenotazioni = te.getPrenotazioni();
        assertEquals(2, prenotazioni.size());
        assertEquals(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-uuuu")),
            prenotazioni.get(1).getDataPrenotazione());
    }
}

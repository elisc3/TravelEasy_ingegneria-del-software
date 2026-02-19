package it.traveleasy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import it.traveleasy.testsupport.BaseTravelEasyTest;

class TravelEasyVisualizzaPrenotazioniOperatoreTest extends BaseTravelEasyTest {

    private Prenotazione primaPrenotazioneOperatore() {
        return te.getPrenotazioni().values().stream().findFirst().orElse(null);
    }

    @Test
    void getPrenotazioni_allAvvio_restituiscePrenotazioniDaDatabase() {
        Map<Integer, Prenotazione> prenotazioni = te.getPrenotazioni();
        assertEquals(1, prenotazioni.size());

        Prenotazione p = primaPrenotazioneOperatore();
        assertNotNull(p);
        assertEquals("16-02-2026", p.getDataPrenotazione());
    }

    @Test
    void getPrenotazioni_contieneDettagliClienteEPacchetto() {
        Prenotazione p = primaPrenotazioneOperatore();
        assertNotNull(p);
        assertNotNull(p.getCliente());
        assertNotNull(p.getPacchetto());
        assertEquals("Mario", p.getCliente().getNome());
        assertEquals("PKG1001", p.getPacchetto().getCodice());
    }

    @Test
    void getPrenotazioni_contieneElencoViaggiatoriCaricato() {
        Prenotazione p = primaPrenotazioneOperatore();
        assertNotNull(p);
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

        int newIdPrenotazione = te.createPrenotazione(pacchetto, cliente.getId());
        assertTrue(newIdPrenotazione > 0);
        for (Viaggiatore v : viaggiatori) {
            assertTrue(te.createViaggiatore(newIdPrenotazione, v.getNome(), v.getCognome(), v.getDataNascita(), v.getTipoDocumento(), v.getCodiceDocumento()));
        }

        boolean ok = te.registrazionePrenotazione(newIdPrenotazione, 0.0f, 480.0f, 0.0f);
        assertTrue(ok);

        Map<Integer, Prenotazione> prenotazioni = te.getPrenotazioni();
        assertEquals(2, prenotazioni.size());
        assertEquals(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-uuuu")),
            te.getPrenotazioneById(newIdPrenotazione).getDataPrenotazione());
    }
}

package it.traveleasy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import it.traveleasy.testsupport.BaseTravelEasyTest;

class TravelEasyVisualizzaPrenotazioniClienteTest extends BaseTravelEasyTest {

    private Prenotazione primaPrenotazioneCliente(Cliente cliente) {
        return cliente.getElencoPrenotazioniEffettuate().values().stream().findFirst().orElse(null);
    }

    @Test
    void elencoPrenotazioniCliente_allAvvio_contienePrenotazioniEffettuate() {
        Cliente cliente = te.getAccountToHomeView("cliente@example.com").getCliente();
        assertEquals(1, cliente.getElencoPrenotazioniEffettuate().size());
    }

    @Test
    void elencoPrenotazioniCliente_contienePrenotazioneConDettagliValorizzati() {
        Cliente cliente = te.getAccountToHomeView("cliente@example.com").getCliente();
        Prenotazione p = primaPrenotazioneCliente(cliente);
        assertNotNull(p);
        assertEquals("PKG1001", p.getPacchetto().getCodice());
        assertEquals("16-02-2026", p.getDataPrenotazione());
        assertEquals(900.0f, p.getPrezzoTotale(), 0.001f);
    }

    @Test
    void elencoPrenotazioniCliente_dopoNuovaPrenotazione_vieneAggiornato() {
        Cliente cliente = te.getAccountToHomeView("cliente@example.com").getCliente();
        int before = cliente.getElencoPrenotazioniEffettuate().size();

        PacchettoViaggio pacchetto = te.getElencoPacchetti().get(2);
        List<Viaggiatore> viaggiatori = List.of(
            new Viaggiatore("Bruno", "Test", "03-09-1989", "Carta d'identita", "AZ1234567")
        );

        int newIdPrenotazione = te.createPrenotazione(pacchetto, cliente.getId());
        assertTrue(newIdPrenotazione > 0);
        for (Viaggiatore v : viaggiatori) {
            assertTrue(te.createViaggiatore(newIdPrenotazione, v.getNome(), v.getCognome(), v.getDataNascita(), v.getTipoDocumento(), v.getCodiceDocumento()));
        }

        boolean ok = te.registrazionePrenotazione(newIdPrenotazione, 0.0f, 480.0f, 0.0f);
        assertTrue(ok);

        assertEquals(before + 1, cliente.getElencoPrenotazioniEffettuate().size());
    }

    @Test
    void elencoPrenotazioniCliente_ogniPrenotazioneHaListaViaggiatori() {
        Cliente cliente = te.getAccountToHomeView("cliente@example.com").getCliente();
        Prenotazione p = primaPrenotazioneCliente(cliente);
        assertNotNull(p);
        assertNotNull(p.getElencoViaggiatori());
        assertEquals(2, p.getElencoViaggiatori().size());
    }
}

package it.traveleasy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import it.traveleasy.testsupport.BaseTravelEasyTest;
import it.traveleasy.testsupport.TestDbSupport;

class TravelEasyRecensioniTest extends BaseTravelEasyTest {

    @Test
    void inserisciRecensione_creaRecordEAggiornaMappaRecensioni() throws Exception {
        Cliente cliente = te.getAccountToHomeView("cliente@example.com").getCliente();
        Prenotazione p = te.getPrenotazioneById(1);

        boolean ok = te.inserisciRecensione(cliente, p, "Ottima esperienza", 5, "Agenzia");
        assertTrue(ok);
        assertEquals(1, te.getRecensioni().size());

        try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM Recensione")) {
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(1, rs.getInt(1));
            }
        }
    }

    @Test
    void visualizzaRecensioneLatoOperatore_clienteRecuperaTernaPerPrenotazione() {
        Cliente cliente = te.getAccountToHomeView("cliente@example.com").getCliente();
        Prenotazione p = te.getPrenotazioneById(1);

        assertTrue(te.inserisciRecensione(cliente, p, "Agenzia top", 5, "Agenzia"));
        assertTrue(te.inserisciRecensione(cliente, p, "Trasporto ok", 4, "Trasporto"));
        assertTrue(te.inserisciRecensione(cliente, p, "Alloggio buono", 4, "Alloggio"));

        Recensione[] recensioni = cliente.getRecensioneByPrenotazione(p.getId());
        assertNotNull(recensioni);
        assertEquals(3, recensioni.length);

        
        Set<String> riferimenti = Arrays.stream(recensioni)
            .filter(r -> r != null)
            .map(r -> {
                if (r instanceof RecensioneAgenzia) return "Agenzia";
                if (r instanceof RecensioneTrasporto) return "Trasporto";
                if (r instanceof RecensioneAlloggio) return "Alloggio";
                return "Sconosciuto";
            })
            .collect(Collectors.toSet());

        assertEquals(Set.of("Agenzia", "Trasporto", "Alloggio"), riferimenti);
        assertEquals(1, te.getNTotaleRecensioni());
    }

    @Test
    void validazioneNuovaRecensione_conCampiVuoti_fallisceSenzaInserireRecord() throws Exception {
        int before = TestDbSupport.countRows(conn, "Recensione");
        boolean valida = te.validaDatiNuovaRecensione("", "Trasporto ok", "Alloggio ok");

        assertFalse(valida);
        assertEquals(before, TestDbSupport.countRows(conn, "Recensione"));
        assertEquals(0, te.getRecensioni().size());
    }

    //testare caso in cui la validazione dati fallisce
}

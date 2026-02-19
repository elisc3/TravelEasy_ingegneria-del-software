package it.traveleasy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.Test;

import it.traveleasy.testsupport.BaseTravelEasyTest;
import it.traveleasy.testsupport.TestDbSupport;

class TravelEasyNuovaPrenotazioneTest extends BaseTravelEasyTest {

    private int creaBozzaConViaggiatori(Cliente cliente, PacchettoViaggio pacchetto, List<Viaggiatore> viaggiatori) {
        int idPrenotazione = te.createPrenotazione(pacchetto, cliente.getId());
        assertTrue(idPrenotazione > 0);

        for (Viaggiatore v : viaggiatori) {
            assertTrue(te.createViaggiatore(
                idPrenotazione,
                v.getNome(),
                v.getCognome(),
                v.getDataNascita(),
                v.getTipoDocumento(),
                v.getCodiceDocumento()
            ));
        }
        return idPrenotazione;
    }

    @Test
    void registrazionePrenotazione_conDatiValidi_inseriscePrenotazioneEViaggiatori() throws Exception {
        Cliente cliente = te.getAccountToHomeView("cliente@example.com").getCliente();
        PacchettoViaggio pacchetto = te.getElencoPacchetti().get(2);
        List<Viaggiatore> viaggiatori = List.of(
            new Viaggiatore("Paolo", "Neri", "10-01-1995", "Passaporto", "ZX1234567"),
            new Viaggiatore("Marta", "Blu", "22-06-1998", "Carta d'identita", "AB1234567")
        );

        int beforePrenotazioni = TestDbSupport.countRows(conn, "Prenotazioni");
        int beforeViaggiatori = TestDbSupport.countRows(conn, "Viaggiatore");
        int beforeClienteList = cliente.getElencoPrenotazioniEffettuate().size();
        int beforeOperatoreList = te.getPrenotazioni().size();

        int newIdPrenotazione = creaBozzaConViaggiatori(cliente, pacchetto, viaggiatori);
        boolean ok = te.registrazionePrenotazione(newIdPrenotazione, 3.0f, 930.0f, 0.0f);

        assertTrue(ok);
        assertEquals(beforePrenotazioni + 1, TestDbSupport.countRows(conn, "Prenotazioni"));
        assertEquals(beforeViaggiatori + 2, TestDbSupport.countRows(conn, "Viaggiatore"));
        assertEquals(beforeClienteList + 1, cliente.getElencoPrenotazioniEffettuate().size());
        assertEquals(beforeOperatoreList + 1, te.getPrenotazioni().size());
    }

    @Test
    void registrazionePrenotazione_conScontoFedelta_aggiornaPortafoglioOre() throws Exception {
        Cliente cliente = te.getAccountToHomeView("cliente@example.com").getCliente();
        PacchettoViaggio pacchetto = te.getElencoPacchetti().get(2); // OreViaggio = 1.5
        List<Viaggiatore> viaggiatori = List.of(
            new Viaggiatore("Laura", "Rosa", "05-05-1992", "Passaporto", "QQ1234567")
        );

        int newIdPrenotazione = creaBozzaConViaggiatori(cliente, pacchetto, viaggiatori);
        boolean ok = te.registrazionePrenotazione(newIdPrenotazione, 3.0f, 465.0f, 0.0f);
        assertTrue(ok);

        try (PreparedStatement ps = conn.prepareStatement(
            "SELECT Ore, Sconto FROM PortafoglioOre WHERE proprietario = ?")) {
            ps.setInt(1, cliente.getId());
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(6.5f, rs.getFloat("Ore"), 0.001f);
                assertEquals(3.0f, rs.getFloat("Sconto"), 0.001f);
            }
        }
    }

    @Test
    void registrazionePrenotazione_conOffertaAttiva_diminuisceDisponibilita() throws Exception {
        Cliente cliente = te.getAccountToHomeView("cliente@example.com").getCliente();
        PacchettoViaggio pacchettoConOfferta = te.getElencoPacchetti().get(1);
        List<Viaggiatore> viaggiatori = List.of(
            new Viaggiatore("Elena", "Viola", "12-03-1994", "Patente di guida", "CD12345678")
        );

        int disponibilitaBefore;
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM OffertaSpeciale WHERE id = 1")) {
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                disponibilitaBefore = rs.getInt(6);
            }
        }

        int newIdPrenotazione = creaBozzaConViaggiatori(cliente, pacchettoConOfferta, viaggiatori);
        boolean ok = te.registrazionePrenotazione(newIdPrenotazione, 0.0f, 900.0f, 25.0f);
        assertTrue(ok);

        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM OffertaSpeciale WHERE id = 1")) {
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(disponibilitaBefore - 1, rs.getInt(6));
            }
        }
    }

    @Test
    void registrazionePrenotazione_impostaDataPrenotazioneOdierna() {
        Cliente cliente = te.getAccountToHomeView("cliente@example.com").getCliente();
        PacchettoViaggio pacchetto = te.getElencoPacchetti().get(2);
        List<Viaggiatore> viaggiatori = List.of(
            new Viaggiatore("Ivo", "Test", "01-02-1990", "Passaporto", "TT1234567")
        );

        int newIdPrenotazione = creaBozzaConViaggiatori(cliente, pacchetto, viaggiatori);
        boolean ok = te.registrazionePrenotazione(newIdPrenotazione, 0.0f, 480.0f, 0.0f);
        assertTrue(ok);

        String expectedToday = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-uuuu"));
        Prenotazione last = te.getPrenotazioneById(newIdPrenotazione);
        assertEquals(expectedToday, last.getDataPrenotazione());
    }
}

package it.traveleasy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.jupiter.api.Test;

import it.traveleasy.testsupport.BaseTravelEasyTest;
import it.traveleasy.testsupport.TestDbSupport;

class TravelEasyRegistrazioneTest extends BaseTravelEasyTest {

    @Test
    void registrazione_conDatiValidi_creaAccountEUtente() throws Exception {
        int accountsBefore = TestDbSupport.countRows(conn, "Account");
        String email = "nuovo.utente@example.com";

        String result = te.registrazione("Nuovo", "Utente", email, "pwd123", "pwd123", "333444555");

        assertEquals(email, result);
        assertEquals(accountsBefore + 1, TestDbSupport.countRows(conn, "Account"));
        assertNotNull(te.getAccountToHomeView(email));
    }

    @Test
    void registrazione_conEmailDuplicata_restituisceErrore() throws Exception {
        int accountsBefore = TestDbSupport.countRows(conn, "Account");

        String result = te.registrazione(
            "Mario", "Rossi", "cliente@example.com", "pwd123", "pwd123", "333111222");

        assertEquals("errore", result);
        assertEquals(accountsBefore, TestDbSupport.countRows(conn, "Account"));
    }

    @Test
    void registrazione_conEmailNonValida_restituisceErrore() {
        String result = te.registrazione(
            "Nome", "Cognome", "email-non-valida", "pwd123", "pwd123", "333111000");
        assertEquals("errore", result);
    }

    @Test
    void registrazione_conPasswordDiverse_restituisceErrore() {
        String result = te.registrazione(
            "Nome", "Cognome", "u2@example.com", "pwd123", "pwd456", "333111000");
        assertEquals("errore", result);
    }

    @Test
    void registrazione_conDatiValidi_creaPortafoglioVirtuale() throws Exception {
        String email = "wallet.test@example.com";
        String result = te.registrazione("Wallet", "Test", email, "pwd123", "pwd123", "333765432");
        assertEquals(email, result);

        try (PreparedStatement ps = conn.prepareStatement(
            "SELECT u.id FROM Utenti u JOIN Account a ON u.Account = a.id WHERE a.Email = ?")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                int utenteId = rs.getInt(1);

                try (PreparedStatement psPv = conn.prepareStatement(
                    "SELECT COUNT(*) FROM PortafoglioVirtuale WHERE Utente = ?")) {
                    psPv.setInt(1, utenteId);
                    try (ResultSet rsPv = psPv.executeQuery()) {
                        assertTrue(rsPv.next());
                        assertEquals(1, rsPv.getInt(1));
                    }
                }
            }
        }
    }

    @Test
    void registrazione_conDatiValidi_creaMetodiPagamentoCompleti() throws Exception {
        String email = "metodi.pagamento@example.com";
        String result = te.registrazione("Metodi", "Pagamento", email, "pwd123", "pwd123", "333999111");
        assertEquals(email, result);

        try (PreparedStatement ps = conn.prepareStatement(
            "SELECT u.id FROM Utenti u JOIN Account a ON u.Account = a.id WHERE a.Email = ?")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                int utenteId = rs.getInt(1);

                try (PreparedStatement psCc = conn.prepareStatement(
                    "SELECT NumeroCarta, Scadenza, cvv, Circuito FROM CartaCredito WHERE Utente = ?")) {
                    psCc.setInt(1, utenteId);
                    try (ResultSet rsCc = psCc.executeQuery()) {
                        assertTrue(rsCc.next());
                        assertEquals("", rsCc.getString("NumeroCarta"));
                        assertEquals("", rsCc.getString("Scadenza"));
                        assertEquals("", rsCc.getString("cvv"));
                        assertEquals("", rsCc.getString("Circuito"));
                    }
                }

                try (PreparedStatement psPo = conn.prepareStatement(
                    "SELECT Ore, Sconto FROM PortafoglioOre WHERE proprietario = ?")) {
                    psPo.setInt(1, utenteId);
                    try (ResultSet rsPo = psPo.executeQuery()) {
                        assertTrue(rsPo.next());
                        assertEquals(0.0f, rsPo.getFloat("Ore"), 0.001f);
                        assertEquals(0.0f, rsPo.getFloat("Sconto"), 0.001f);
                    }
                }
            }
        }
    }

    //testare creazione di metodi di pagamento;
}

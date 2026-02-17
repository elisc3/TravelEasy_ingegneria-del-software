package it.traveleasy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.jupiter.api.Test;

import it.traveleasy.testsupport.BaseTravelEasyTest;
import it.traveleasy.testsupport.TestDbSupport;

class TravelEasyEliminaAccountTest extends BaseTravelEasyTest {

    @Test
    void eliminaAccount_conCredenzialiValide_rimuoveAccountEUtente() throws Exception {
        int beforeAccount = TestDbSupport.countRows(conn, "Account");
        int beforeUtenti = TestDbSupport.countRows(conn, "Utenti");

        boolean deleted = te.eliminaAccount(conn, "cliente@example.com", "pwd123");

        assertTrue(deleted);
        assertEquals(beforeAccount - 1, TestDbSupport.countRows(conn, "Account"));
        assertEquals(beforeUtenti - 1, TestDbSupport.countRows(conn, "Utenti"));
        assertNull(te.getAccountToHomeView("cliente@example.com"));
    }

    @Test
    void eliminaAccount_conPasswordErrata_nonRimuoveNulla() throws Exception {
        int beforeAccount = TestDbSupport.countRows(conn, "Account");
        int beforeUtenti = TestDbSupport.countRows(conn, "Utenti");

        boolean deleted = te.eliminaAccount(conn, "cliente@example.com", "wrong");

        assertFalse(deleted);
        assertEquals(beforeAccount, TestDbSupport.countRows(conn, "Account"));
        assertEquals(beforeUtenti, TestDbSupport.countRows(conn, "Utenti"));
    }

    @Test
    void eliminaAccount_rimuoveMetodiPagamentoAssociati() throws Exception {
        boolean deleted = te.eliminaAccount(conn, "cliente@example.com", "pwd123");
        assertTrue(deleted);

        try (PreparedStatement ps = conn.prepareStatement(
            "SELECT COUNT(*) FROM PortafoglioVirtuale WHERE Utente = 1");
             ResultSet rs = ps.executeQuery()) {
            assertTrue(rs.next());
            assertEquals(0, rs.getInt(1));
        }

        try (PreparedStatement ps = conn.prepareStatement(
            "SELECT COUNT(*) FROM CartaCredito WHERE Utente = 1");
             ResultSet rs = ps.executeQuery()) {
            assertTrue(rs.next());
            assertEquals(0, rs.getInt(1));
        }
    }

    @Test
    void eliminaAccount_rimuovePortafoglioOreAssociato() throws Exception {
        boolean deleted = te.eliminaAccount(conn, "cliente@example.com", "pwd123");
        assertTrue(deleted);

        try (PreparedStatement ps = conn.prepareStatement(
            "SELECT COUNT(*) FROM PortafoglioOre WHERE proprietario = 1 OR Utente = 1");
             ResultSet rs = ps.executeQuery()) {
            assertTrue(rs.next());
            assertEquals(0, rs.getInt(1));
        }
    }
}

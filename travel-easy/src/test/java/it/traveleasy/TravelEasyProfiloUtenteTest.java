package it.traveleasy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import it.traveleasy.testsupport.BaseTravelEasyTest;

class TravelEasyProfiloUtenteTest extends BaseTravelEasyTest {

    @Test
    void getAccountToHomeView_conEmailCliente_restituisceAccountCliente() {
        Account account = te.getAccountToHomeView("cliente@example.com");
        assertNotNull(account);
        assertEquals("Cliente", account.getRuolo());
        assertNotNull(account.getCliente());
    }

    @Test
    void profiloCliente_contieneDatiAnagraficiAttesi() {
        Cliente cliente = te.getAccountToHomeView("cliente@example.com").getCliente();
        assertEquals("Mario", cliente.getNome());
        assertEquals("Rossi", cliente.getCognome());
        assertEquals("333111222", cliente.getTelefono());
    }

    @Test
    void profiloCliente_contieneSaldoPortafoglioAtteso() {
        Cliente cliente = te.getAccountToHomeView("cliente@example.com").getCliente();
        assertEquals(120.0, cliente.getPv().getSaldo(), 0.001);
    }

    @Test
    void profiloCliente_contienePortafoglioOreAtteso() {
        Cliente cliente = te.getAccountToHomeView("cliente@example.com").getCliente();
        assertEquals(15.0f, cliente.getPo().getOre(), 0.001f);
        assertEquals(3.0f, cliente.getPo().getSconto(), 0.001f);
    }
}

package it.traveleasy;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import it.traveleasy.testsupport.BaseTravelEasyTest;

class TravelEasyLoginTest extends BaseTravelEasyTest {

    @Test
    void login_conCredenzialiClienteValide_restituisceEmailERuolo() {
        String[] result = te.login(conn, "cliente@example.com", "pwd123");
        assertArrayEquals(new String[] {"cliente@example.com", "Cliente"}, result);
    }

    @Test
    void login_conCredenzialiOperatoreValide_restituisceEmailERuolo() {
        String[] result = te.login(conn, "operatore@example.com", "admin123");
        assertArrayEquals(new String[] {"operatore@example.com", "Operatore"}, result);
    }

    @Test
    void login_conEmailInesistente_restituisceErrore() {
        String[] result = te.login(conn, "missing@example.com", "any");
        assertEquals("errore", result[0]);
    }

    @Test
    void login_conPasswordErrata_restituisceErrore() {
        String[] result = te.login(conn, "cliente@example.com", "wrong");
        assertEquals("errore", result[0]);
    }

    @Test
    void login_conCampiVuoti_restituisceErrore() {
        String[] result = te.login(conn, "", "");
        assertEquals("errore", result[0]);
    }
}

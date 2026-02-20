package it.traveleasy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

import it.traveleasy.testsupport.BaseTravelEasyTest;
import it.traveleasy.testsupport.TestDbSupport;

class TravelEasyNuovaOffertaSpecialeTest extends BaseTravelEasyTest {

    @Test
    void validazionePercentualeOfferta_conInputValido_restituisceValore() {
        PacchettoViaggio p = te.getElencoPacchetti().get(1);
        float result = p.validazionePercentunaleNuovaOfferta("15");
        assertEquals(15.0f, result, 0.001f);
    }

    @Test
    void validazionePercentualeOfferta_conInputNonNumerico_restituisceMenoTre() {
        PacchettoViaggio p = te.getElencoPacchetti().get(1);
        float result = p.validazionePercentunaleNuovaOfferta("abc");
        assertEquals(-3.0f, result, 0.001f);
    }

    @Test
    void validazioneDataOfferta_conDataFineValida_restituisceTrue() {
        PacchettoViaggio p = te.getElencoPacchetti().get(2);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String fine = LocalDate.now().plusDays(10).format(fmt);
        assertTrue(p.validazioneDataInserimentoOfferta(fine, p.getDataPartenza()));
    }

    @Test
    void nuovaOfferta_conDatiValidi_inserisceRigaInOffertaSpeciale() throws Exception {
        PacchettoViaggio p = te.getElencoPacchetti().get(2);
        int before = TestDbSupport.countRows(conn, "OffertaSpeciale");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String fine = LocalDate.now().plusDays(5).format(fmt);

        OffertaSpeciale offerta = p.createNuovaOfferta(20.0f, fine, 10);

        assertNotNull(offerta);
        assertEquals(before + 1, TestDbSupport.countRows(conn, "OffertaSpeciale"));
        assertEquals(20.0f, offerta.getScontoPercentuale(), 0.001f);
    }

    @Test
    void getOffertaByPack_conPacchettoConOfferta_restituisceOfferta() {
        PacchettoViaggio p = te.getElencoPacchetti().get(1);
        OffertaSpeciale offerta = te.getOffertaByPack(p);
        assertNotNull(offerta);
    }
}

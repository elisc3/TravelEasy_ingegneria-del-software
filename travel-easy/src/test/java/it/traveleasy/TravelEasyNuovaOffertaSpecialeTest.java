package it.traveleasy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

import it.traveleasy.testsupport.BaseTravelEasyTest;
import it.traveleasy.testsupport.TestDbSupport;

class TravelEasyNuovaOffertaSpecialeTest extends BaseTravelEasyTest {

    @Test
    void validazionePercentualeOfferta_conInputValido_restituisceValore() {
        PacchettoViaggio p = te.getElencoPacchetti().get(1);
        float result = te.validazioneDatiNuovaOfferta(p, "15", p.getDataPartenza(), "10-04-2026", "5");
        assertEquals(15.0f, result, 0.001f);
    }

    @Test
    void validazionePercentualeOfferta_conInputNonNumerico_restituisceMenoTre() {
        PacchettoViaggio p = te.getElencoPacchetti().get(1);
        float result = te.validazioneDatiNuovaOfferta(p, "abc", p.getDataPartenza(), "10-04-2026", "5");
        assertEquals(-3.0f, result, 0.001f);
    }

    @Test
    void validazioneDataOfferta_conDataFineValida_restituisceTrue() {
        PacchettoViaggio p = te.getElencoPacchetti().get(2);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-uuuu");
        String fine = LocalDate.parse(p.getDataPartenza(), fmt).minusDays(1).format(fmt);
        float esito = te.validazioneDatiNuovaOfferta(p, "20", p.getDataPartenza(), fine, "10");
        assertEquals(20.0f, esito, 0.001f);
    }

    @Test
    void nuovaOfferta_conDatiValidi_inserisceRigaInOffertaSpeciale() throws Exception {
        PacchettoViaggio p = te.getElencoPacchetti().get(2);
        int before = TestDbSupport.countRows(conn, "OffertaSpeciale");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-uuuu");
        String fine = LocalDate.parse(p.getDataPartenza(), fmt).minusDays(2).format(fmt);

        boolean created = te.createNuovaOfferta(20.0f, fine, 10, p);

        assertTrue(created);
        assertEquals(before + 1, TestDbSupport.countRows(conn, "OffertaSpeciale"));
        OffertaSpeciale offerta = te.getOffertaByPack(p);
        assertEquals(20.0f, offerta.getScontoPercentuale(), 0.001f);
    }

    @Test
    void getOffertaByPack_conPacchettoConOfferta_restituisceOfferta() {
        PacchettoViaggio p = te.getElencoPacchetti().get(1);
        OffertaSpeciale offerta = te.getOffertaByPack(p);
        assertNotNull(offerta);
    }

    @Test
    void validazioneNuovaOfferta_conPercentualeVuota_restituisceMenoUno() {
        PacchettoViaggio p = te.getElencoPacchetti().get(2);
        String partenza = LocalDate.now().plusDays(30).format(DateTimeFormatter.ofPattern("dd-MM-uuuu"));
        String fine = LocalDate.now().plusDays(20).format(DateTimeFormatter.ofPattern("dd-MM-uuuu"));

        float esito = te.validazioneDatiNuovaOfferta(p, "", partenza, fine, "3");
        assertEquals(-1.0f, esito, 0.001f);
    }

    @Test
    void validazioneNuovaOfferta_conDataFineDopoPartenza_restituisceMenoCinque() {
        PacchettoViaggio p = te.getElencoPacchetti().get(2);
        String partenza = LocalDate.now().plusDays(30).format(DateTimeFormatter.ofPattern("dd-MM-uuuu"));
        String fine = LocalDate.now().plusDays(31).format(DateTimeFormatter.ofPattern("dd-MM-uuuu"));

        float esito = te.validazioneDatiNuovaOfferta(p, "10", partenza, fine, "3");
        assertEquals(-5.0f, esito, 0.001f);
    }

    @Test
    void validazioneNuovaOfferta_conNumeroPacchettiNonNumerico_restituisceMenoOtto() {
        PacchettoViaggio p = te.getElencoPacchetti().get(2);
        String partenza = LocalDate.now().plusDays(30).format(DateTimeFormatter.ofPattern("dd-MM-uuuu"));
        String fine = LocalDate.now().plusDays(20).format(DateTimeFormatter.ofPattern("dd-MM-uuuu"));

        float esito = te.validazioneDatiNuovaOfferta(p, "10", partenza, fine, "abc");
        assertEquals(-8.0f, esito, 0.001f);
    }

    //TESTARE DATI NON VALIDI
}

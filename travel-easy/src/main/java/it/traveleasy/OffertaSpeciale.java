package it.traveleasy;

import java.sql.Connection;

public class OffertaSpeciale {
    private int id;
    private PacchettoViaggio pacchetto;
    private float scontoPercentuale;
    private float prezzoScontato;
    private String dataFine;
    private int disponibilita;

    public OffertaSpeciale(int id, PacchettoViaggio pacchetto, float scontoPercentuale, float prezzoScontato, String dataFine, int disponibilita) {
        this.id = id;
        this.pacchetto = pacchetto;
        this.scontoPercentuale = scontoPercentuale;
        this.prezzoScontato = prezzoScontato;
        this.dataFine = dataFine;
        this.disponibilita = disponibilita;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PacchettoViaggio getPacchetto() {
        return pacchetto;
    }

    public void setPacchetto(PacchettoViaggio pacchetto) {
        this.pacchetto = pacchetto;
    }

    public float getScontoPercentuale() {
        return scontoPercentuale;
    }

    public void setScontoPercentuale(float scontoPercentuale) {
        this.scontoPercentuale = scontoPercentuale;
    }

    public float getPrezzoScontato() {
        return prezzoScontato;
    }

    public void setPrezzoScontato(float prezzoScontato) {
        this.prezzoScontato = prezzoScontato;
    }

    public String getDataFine() {
        return dataFine;
    }

    public void setDataFine(String dataFine) {
        this.dataFine = dataFine;
    }

    public int getDisponibilità() {
        return disponibilita;
    }

    public void setDisponibilità(int disponibilita) {
        this.disponibilita = disponibilita;
    }

    private boolean diminuisciDisponibilitàDB(Connection conn) {
        return OffertaSpecialeDao.INSTANCE.decrementDisponibilita(conn, id);
    }

    public boolean diminuisciDisponibilità(Connection conn) {
        if (this.diminuisciDisponibilitàDB(conn)) {
            disponibilita--;
            return true;
        }
        return false;
    }
}

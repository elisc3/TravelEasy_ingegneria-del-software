package it.traveleasy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

public class PacchettoViaggio {
    private int id;
    private String codice;
    private String città;
    private String titolo;
    private String nazione;
    private String dataPartenza;
    private String dataRitorno;
    private String descrizione;
    private float prezzo;
    private int visibilità;
    private int idCompagniaTrasporto;
    private int idAlloggio;
    private Connection conn;

    public PacchettoViaggio(int id, String codice, String titolo,  String città, String nazione, String dataPartenza, String dataRitorno, String descrizione, float prezzo, int visibilità, int idCompagniaTrasporto, int idAlloggio, Connection conn){
        this.id= id;
        this.codice = codice;
        this.titolo = titolo;
        this.città = città;
        this.nazione = nazione;
        this.dataPartenza = dataPartenza;
        this.dataRitorno = dataRitorno;
        this.descrizione = descrizione;
        this.prezzo = prezzo;
        this.visibilità = visibilità;
        this.idCompagniaTrasporto = idCompagniaTrasporto;
        this.idAlloggio = idAlloggio;
        this.conn = conn;
    }

    public int getId() {
        return id;
    }

    public String getTitolo(){
        return titolo;
    }

    public String getCittà() {
        return città;
    }

    public String getNazione() {
        return nazione;
    }

    public String getDataPartenza() {
        return dataPartenza;
    }

    public String getDataRitorno() {
        return dataRitorno;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public float getPrezzo() {
        return prezzo;
    }

    public int isVisibilità() {
        return visibilità;
    }

    public int getIdCompagniaTrasporto() {
        return idCompagniaTrasporto;
    }

    public int getIdAlloggio(){
        return idAlloggio;
    }

    public String getCodice(){
        return this.codice;
    }

       


}
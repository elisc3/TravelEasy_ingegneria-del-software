package it.traveleasy;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.sql.Connection;

public class Viaggiatore {
    private String nome;
    private String cognome;
    private String dataNascita;
    private String tipoDocumento;
    private String codiceDocumento;
    private boolean sediaRotelle;
    private boolean cecita;
   

    public Viaggiatore(String nome, String cognome, String dataNascita, String tipoDocumento, String codiceDocumento){
        this.nome = nome;
        this.cognome = cognome;
        this.dataNascita = dataNascita;
        this.tipoDocumento = tipoDocumento;
        this.codiceDocumento = codiceDocumento;
        this.sediaRotelle = false;
        this.cecita = false;
    }

    public String getNome(){
        return nome;
    }

    public String getCognome(){
        return cognome;
    }

    public String getDataNascita(){
        return dataNascita;
    }

    public String getTipoDocumento(){
        return tipoDocumento;
    }

    public String getCodiceDocumento(){
        return codiceDocumento;
    }
    
    public boolean isSediaRotelle() { 
        return sediaRotelle; 
    }

    public void setSediaRotelle(boolean sediaRotelle) { 
        this.sediaRotelle = sediaRotelle; 
    }

    public boolean isCecita() { 
        return cecita; 
    }

    public void setCecita(boolean cecita) { 
        this.cecita = cecita; 
    }

    public void setCecitaDB(Connection conn, boolean cecita){
        String query = "UPDATE Viaggiatore SET Cecità = ?;";
    }
}

package it.traveleasy;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

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

    public int validazioneDatiPrenotazione(Viaggiatore v){
        
            String nome = v.getNome();
            String cognome = v.getCognome();
            String dataNascita = v.getDataNascita();
            String tipoDocumento = v.getTipoDocumento();
            String codiceDocumento = v.getCodiceDocumento();

            if (nome.isBlank() || cognome.isBlank() || dataNascita.isBlank() ||tipoDocumento.isBlank() || codiceDocumento.isBlank())
                return -1;
            
            DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);

            LocalDate d = LocalDate.parse(dataNascita, FMT);

            if (d.isAfter(LocalDate.now()))
                return -2;

            if (tipoDocumento.equals("Carta d'identità") && codiceDocumento.length() != 9){
                return -3;
            } else if (tipoDocumento.equals("Patente di guida") && codiceDocumento.length() != 10){
                return -4;
            } else if (tipoDocumento.equals("Passaporto") && codiceDocumento.length() != 9){
                return -5;
            } 
        return 0;
    }
}

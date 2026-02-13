package it.traveleasy;

public class Viaggiatore {
    private String nome;
    private String cognome;
    private String dataNascita;
    private String tipoDocumento;
    private String codiceDocumento;
    

    public Viaggiatore(String nome, String cognome, String dataNascita, String tipoDocumento, String codiceDocumento){
        this.nome = nome;
        this.cognome = cognome;
        this.dataNascita = dataNascita;
        this.tipoDocumento = tipoDocumento;
        this.codiceDocumento = codiceDocumento;
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
}

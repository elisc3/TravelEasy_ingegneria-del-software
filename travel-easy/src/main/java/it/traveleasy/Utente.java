package it.traveleasy;


public class Utente {
    private int id;
    private String nome;
    private String cognome;
    private String Telefono;
    private String ruolo;
    private int Account;

    public Utente(int id, String nome, String cognome, String Telefono, String ruolo, int Account) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.Telefono = Telefono;
        this.ruolo = ruolo;
        this.Account = Account;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getTelefono() {
        return Telefono;
    }

    public void setTelefono(String Telefono) {
        this.Telefono = Telefono;
    }

    public String getRuolo() {
        return ruolo;
    }

    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }

    public int getAccount() {
        return Account;
    }

    public void setAccount(int Account) {
        this.Account = Account;
    }
}


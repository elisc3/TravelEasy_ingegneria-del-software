package it.traveleasy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Account {
    private int id;
    private String email;
    private String password;
    private String ruolo;
    private Connection conn;
    private Utente utente;
    private TravelEasy te = null;

    public Account(Connection conn, int id, String email, String password, String ruolo, Utente utente) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.ruolo = ruolo;
        this.conn = conn;
        this.utente = utente;
        this.id = id;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRuolo() {
        return ruolo;
    }

    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }

    public Utente getUtente() {
        return utente;
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }

    
}

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

    public int createClient(Connection conn, String nome, String cognome, String telefono) {

        String query = "INSERT INTO Utenti (Nome, Cognome, Telefono, Ruolo, Account) values (?, ?, ?, ?, ?);";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, nome);
            pstmt.setString(2, cognome);
            pstmt.setString(3, telefono);
            pstmt.setString(4, "Cliente");
            pstmt.setInt(5, this.id);
            pstmt.executeUpdate();
        } catch (SQLException e){
            System.out.println("Errore registrazioneCliente: "+e);
            return 0;
        }

        int idUtente = 0;
        query = "Select id FROM Utenti where Account = ?;";

        try (PreparedStatement pstmt = conn.prepareStatement(query)){
            pstmt.setInt(1, this.id);

            ResultSet rs = pstmt.executeQuery();

            if(!rs.next())
                return 0;

            idUtente = rs.getInt("id");

        } catch (SQLException e) {
            System.out.println("Errore Utente non esiste:"+e);
            return 0;
        }

        Cliente c = new Cliente(idUtente, nome, cognome, telefono, "Cliente", this.id, null, null);
        this.utente = c;

        if(!c.metodiPagamento(conn))
            return 0;
        
        return idUtente;
    }

    public PortafoglioVirtuale getPortafoglioVirtuale() {
        if (utente instanceof Cliente) {
            return ((Cliente) utente).getPv();
        }
        return null;
    }

    public CartaCredito getCartaCredito() {
        if (utente instanceof Cliente) {
            return ((Cliente) utente).getCc();
        }
        return null;
    }

    public boolean validazioneCredenziali(String email, String password) {
        if (email.equals("") || password.equals(""))
            return false;

        return this.email.equals(email) && this.password.equals(password);
    }


    public Cliente getCliente(){
        if (this.utente instanceof Cliente)
            return ((Cliente) utente);
        return null;
    }
}

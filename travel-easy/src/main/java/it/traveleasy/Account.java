package it.traveleasy;
import java.sql.Connection;

public class Account {
    private int id;
    private String email;
    private String password;
    private String ruolo;
    private Utente utente;

    public Account(int id, String email, String password, String ruolo, Utente utente) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.ruolo = ruolo;
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

    //*CREAZIONE CLIENTE
    public int createClient(Connection conn, String nome, String cognome, String telefono) {
        if (!UtenteDao.INSTANCE.insertCliente(conn, nome, cognome, telefono, this.id)) {
            return 0;
        }

        int idUtente = UtenteDao.INSTANCE.findIdByAccount(conn, this.id);
        if (idUtente == 0) {
            return 0;
        }

        Cliente c = new Cliente(idUtente, nome, cognome, telefono, "Cliente", this.id, null, null, null);
        this.utente = c;

        if(!c.metodiPagamento(conn))
            return 0;

        
        
        return idUtente;
    }

    //*ELIMINAZIONE CLIENTE
    public boolean eliminaCliente(Connection conn){
        Cliente cliente = this.getCliente();
        if (cliente == null)
            return false;

        if(!cliente.eliminaMetodiPagamento(conn))
            return false;

        return UtenteDao.INSTANCE.deleteById(conn, this.utente.getId());
    }

    //*VARIE
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

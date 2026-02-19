package it.traveleasy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter; 
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Prenotazione {
    private int id;
    private Cliente cliente;
    private PacchettoViaggio pacchetto;
    private String dataPrenotazione;
    private List<Viaggiatore> elencoViaggiatori;
    private float prezzoTotale;
    private float prezzoAssistenzaSpeciale;
    private float scontoApplicato;
    private float percentualeOfferta;
    private boolean checkedIn;
    

    public Prenotazione(Cliente cliente, PacchettoViaggio pacchetto, String dataPrenotazione, float prezzoTotale, float scontoApplicato, float percentualeOfferta, boolean checkin) {
        this(0, cliente, pacchetto, dataPrenotazione, prezzoTotale, scontoApplicato, percentualeOfferta, checkin);
        this.prezzoAssistenzaSpeciale = 0;
    }

    public Prenotazione(int id, Cliente cliente, PacchettoViaggio pacchetto, String dataPrenotazione, float prezzoTotale, float scontoApplicato, float percentualeOfferta, boolean checkin) {
        this.id = id;
        this.cliente = cliente;
        this.pacchetto = pacchetto;
        this.dataPrenotazione = dataPrenotazione;
        this.elencoViaggiatori = new ArrayList<>();
        this.prezzoTotale = prezzoTotale;
        this.scontoApplicato = scontoApplicato;
        this.percentualeOfferta = percentualeOfferta;
        this.prezzoAssistenzaSpeciale = 0;
        this.checkedIn = checkin;
    }

    public Prenotazione(int id, Cliente cliente, PacchettoViaggio pacchetto, String dataPrenotazione, List<Viaggiatore> elencoViaggiatori, float prezzoTotale, float scontoApplicato, float percentualeOfferta, boolean checkin) {
        this.id = id;
        this.cliente = cliente;
        this.pacchetto = pacchetto;
        this.dataPrenotazione = dataPrenotazione;
        this.elencoViaggiatori = elencoViaggiatori;
        this.prezzoTotale = prezzoTotale;
        this.scontoApplicato = scontoApplicato;
        this.percentualeOfferta = percentualeOfferta;
        this.prezzoAssistenzaSpeciale = 0;
        this.checkedIn = checkin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public PacchettoViaggio getPacchetto() {
        return pacchetto;
    }

    public void setPacchetto(PacchettoViaggio pacchetto) {
        this.pacchetto = pacchetto;
    }

    public String getDataPrenotazione() {
        return dataPrenotazione;
    }

    public void setDataPrenotazione(String dataPrenotazione) {
        this.dataPrenotazione = dataPrenotazione;
    }

    public float getPrezzoTotale(){
        return this.prezzoTotale;
    }

    public void setPrezzoTotale(float prezzoTotale){
        this.prezzoTotale = prezzoTotale;
    }

    public float getPrezzoAssistenzaSpeciale() {
        return prezzoAssistenzaSpeciale;
    }

    public void setPrezzoAssistenzaSpeciale(float prezzoAssistenzaSpeciale) {
        this.prezzoAssistenzaSpeciale = prezzoAssistenzaSpeciale;
    }

    public float getScontoApplicato(){
        return this.scontoApplicato;
    }

    public void setScontoApplicato(float scontoApplicato){
        this.scontoApplicato = scontoApplicato;
    }

    public float getPercentualeOfferta(){
        return percentualeOfferta;
    }

    public float getOffertaApplicata() {
        return this.percentualeOfferta;
    }

    public void setOffertaApplicata(float percentualeOfferta) {
        this.percentualeOfferta = percentualeOfferta;
    }

    public boolean isCheckedIn() {
        return checkedIn;
    }

    public void setCheckedIn(boolean checkedIn) {
        this.checkedIn = checkedIn;
    }


    public boolean aggiornaOreViaggio(Connection conn){
        if (this.cliente == null || this.pacchetto == null) {
            return false;
        }
        return this.cliente.aggiornaOreViaggio(conn, this.pacchetto.getOreViaggio());
    }

    //!RIVEDI
    public boolean applicaSconto(Connection conn, float scontoApplicato) {
        
        if (scontoApplicato > 0 && scontoApplicato % 3 == 0){
            int nVolte = (int) scontoApplicato / 3;

            PortafoglioOre po = this.cliente.getPo();
            if (po == null) {
                return true;
            }
            po.setSconto(0); 
        }

        return true;
    }

    public List<Viaggiatore> getElencoViaggiatori() {
        return Collections.unmodifiableList(this.elencoViaggiatori);
    }

    //!RIVEDI
    public void setElencoViaggiatori (List<Viaggiatore> elencoViaggiatori){
        this.elencoViaggiatori = elencoViaggiatori;
    }

    //*ASSISTENZA SPECIALE
    public void aggiornaAssistenza(Viaggiatore v, String tipoAssistenza, boolean valore) {
        switch (tipoAssistenza) {
            case "sediaRotelle":
                v.setSediaRotelle(valore);
                break;
            case "cecita":
                v.setCecita(valore);
                break;
        }
    }

    public void calcolaPrezzoAssistenzaSpeciale() {
        float costoSediaRotelle = 35.0f; 
        float costoCecita = 25.0f; 
        float totaleAssistenza = 0.0f;

        for (Viaggiatore v : elencoViaggiatori) {
            if (v.isSediaRotelle()) {
                totaleAssistenza += costoSediaRotelle;
            }
            if (v.isCecita()) {
                totaleAssistenza += costoCecita;
            }
        }

        this.prezzoAssistenzaSpeciale = totaleAssistenza;
    }

    //*CHEK-IN
    public boolean checkIn(Connection conn) {
        if(this.pacchetto == null){
            return false;
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-uuuu");
        LocalDate dataPartenza = LocalDate.parse(this.pacchetto.getDataPartenza(), formatter);
        LocalDate oggi = LocalDate.now();
        long giorniMancanti = ChronoUnit.DAYS.between(oggi, dataPartenza);

        if (giorniMancanti <= 2) {
            String query = "UPDATE Prenotazioni SET checkIn = 1 WHERE id = ?;";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, this.id);
                pstmt.executeUpdate();
            } catch (SQLException e){
                System.out.println("Errore salvataggio check-in: "+e);
                return false;
            }
            this.setCheckedIn(true);
            return true;
        }
        else {
            System.out.println("Check-in non consentito. Mancano più di 2 giorni alla partenza.");
            return false;
        }
    }    

    private boolean insertViaggiatoriDB(Connection conn, Viaggiatore v){
        String query = "INSERT INTO Viaggiatore (Nome, Cognome, DataNascita, TipoDocumento, CodiceDocumento, Prenotazione, SediaRotelle, \"Cecità\") values (?, ?, ?, ?, ?, ?, ?, ?);";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, v.getNome());
            pstmt.setString(2, v.getCognome());
            pstmt.setString(3, v.getDataNascita());
            pstmt.setString(4, v.getTipoDocumento());
            pstmt.setString(5, v.getCodiceDocumento());
            pstmt.setInt(6, id);
            pstmt.setInt(7, v.isSediaRotelle() ? 1 : 0);
            pstmt.setInt(8, v.isCecita() ? 1 : 0);
            pstmt.executeUpdate();

            return true;
        } catch (SQLException e){
            System.out.println("Errore insertViaggiatoriDB: "+e);
            return false;
        }
    }

    public boolean createViaggiatore(Connection conn, String nome, String cognome, String dataNascita, String tipoDocumento, String codiceDocumento){
        
        
        Viaggiatore v = new Viaggiatore(nome, cognome, dataNascita, tipoDocumento, codiceDocumento);
        if (!this.insertViaggiatoriDB(conn, v))
            return false;

        this.elencoViaggiatori.add(v);
        return true;
    }

    public List<Viaggiatore> getViaggiatori(){
        return Collections.unmodifiableList(elencoViaggiatori);
    }

}

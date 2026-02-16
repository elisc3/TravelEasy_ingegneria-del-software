package it.traveleasy;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Prenotazione {
    private Cliente cliente;
    private PacchettoViaggio pacchetto;
    private String dataPrenotazione;
    private List<Viaggiatore> elencoViaggiatori;
    private float prezzoTotale;
    private float scontoApplicato;
    private float percentualeOfferta;
    

    public Prenotazione(Cliente cliente, PacchettoViaggio pacchetto, String dataPrenotazione, List<Viaggiatore> elencoViaggiatori, float prezzoTotale, float scontoApplicato, float percentualeOfferta) {
        this.cliente = cliente;
        this.pacchetto = pacchetto;
        this.dataPrenotazione = dataPrenotazione;
        this.elencoViaggiatori = elencoViaggiatori;
        this.prezzoTotale = prezzoTotale;
        this.scontoApplicato = scontoApplicato;
        this.percentualeOfferta = percentualeOfferta;
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


    public boolean aggiornaOreViaggio(Connection conn){
        if (this.cliente == null || this.cliente.getPo() == null || this.pacchetto == null) {
            return false;
        }
        return this.cliente.getPo().incrementaOre(conn, this.pacchetto.getOreViaggio());
    }

    public boolean applicaSconto(Connection conn, float scontoApplicato) {
        
        if (scontoApplicato % 3 == 0){
            int nVolte = (int) scontoApplicato / 3;

            PortafoglioOre po = this.cliente.getPo();
            po.setSconto(0);
            po.setOre(po.getOre() - nVolte*10);

            if (!po.applicaScontoDB(conn))
                return false;    
        }

        return true;
    }

    public int getNViaggiatori(){
        return this.elencoViaggiatori.size();
    }

    public List<Viaggiatore> getElencoViaggiatori() {
        return this.elencoViaggiatori;
    }
}

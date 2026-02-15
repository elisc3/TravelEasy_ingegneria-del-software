package it.traveleasy;
import java.util.List;
import java.sql.Connection;

public class Prenotazione {
    private Cliente cliente;
    private PacchettoViaggio pacchetto;
    private String dataPrenotazione;
    private List<Viaggiatore> elencoViaggiatori;

    public Prenotazione(Cliente cliente, PacchettoViaggio pacchetto, String dataPrenotazione, List<Viaggiatore> elencoViaggiatori) {
        this.cliente = cliente;
        this.pacchetto = pacchetto;
        this.dataPrenotazione = dataPrenotazione;
        this.elencoViaggiatori = elencoViaggiatori;
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

    public boolean aggiornaOreViaggio(Connection conn){
        if (this.cliente == null || this.cliente.getPo() == null || this.pacchetto == null) {
            return false;
        }
        return this.cliente.getPo().incrementaOre(conn, this.pacchetto.getOreViaggio());
    }
}

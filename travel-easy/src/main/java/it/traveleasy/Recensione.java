package it.traveleasy;

public class Recensione {
    private int id;
    //private String riferimento;
    private int stelle;
    private String commento;
    private String data;
    private Cliente cliente;
    private Prenotazione prenotazione;

    public Recensione(int id, /*String riferimento,*/ int stelle, String commento, String data, Cliente cliente, Prenotazione prenotazione) {
        this.id = id;
        //this.riferimento = riferimento;
        this.stelle = stelle;
        this.commento = commento;
        this.data = data;
        this.cliente = cliente;
        this.prenotazione = prenotazione;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /*public String getRiferimento() {
        return riferimento;
    }

    public void setRiferimento(String riferimento) {
        this.riferimento = riferimento;
    }*/

    public int getStelle() {
        return stelle;
    }

    public void setStelle(int stelle) {
        this.stelle = stelle;
    }

    public String getCommento() {
        return commento;
    }

    public void setCommento(String commento) {
        this.commento = commento;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Prenotazione getPrenotazione(){
        return prenotazione;
    }

    public void setPrenotazione(Prenotazione prenotazione){
        this.prenotazione = prenotazione;
    }

    public String getData(){
        return data;
    }

    public void setData(String data){
        this.data = data;
    } 
}

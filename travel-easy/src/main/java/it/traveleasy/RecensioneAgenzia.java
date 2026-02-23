package it.traveleasy;

public class RecensioneAgenzia extends Recensione {
    
    public RecensioneAgenzia(int id, int stelle, String commento, String data, Cliente cliente, Prenotazione prenotazione){
        super(id, stelle, commento, data, cliente, prenotazione);
    }
}

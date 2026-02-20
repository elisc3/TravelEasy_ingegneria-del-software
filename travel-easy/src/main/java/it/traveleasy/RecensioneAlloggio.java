package it.traveleasy;

public class RecensioneAlloggio extends Recensione {
    
    public RecensioneAlloggio(int id, int stelle, String commento, String data, Cliente cliente, Prenotazione prenotazione){
        super(id, stelle, commento, data, cliente, prenotazione);
    }

}

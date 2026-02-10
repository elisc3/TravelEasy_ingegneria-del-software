package it.traveleasy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Cliente extends Utente {
    private PortafoglioVirtuale pv; //
    private CartaCredito cc;
    private TravelEasy te = null;

    public Cliente(int id, String nome, String cognome, String Telefono, String ruolo, int Account, PortafoglioVirtuale pv, CartaCredito cc) {
        super(id, nome, cognome, Telefono, ruolo, Account);
        this.pv = pv;
        this.cc = cc;
    }
    
    public PortafoglioVirtuale getPv() {
        return pv;
    }

    public void setPv(PortafoglioVirtuale pv) {
        this.pv = pv;
    }

    public CartaCredito getCc() {
        return cc;
    }

    public void setCc(CartaCredito cc) {
        this.cc = cc;
    }

    public void incrementaPortafoglio(float importo){
        this.pv.incrementaSaldo(importo);
        System.out.println("Saldo aggiornato della classe cliente: "+this.pv.getSaldo());
    }

    public void decrementaPortafoglio(float importo){
        this.pv.decrementaSaldo(importo);
        System.out.println("Saldo aggiornato della classe cliente: "+this.pv.getSaldo());
    }

    public void stampaSaldoPortafoglio(){
        System.out.println("Saldo aggiornato della classe cliente: "+this.pv.getSaldo());
    }

    
}
package it.traveleasy;

import java.sql.Connection;


public class CartaCredito {
    private Cliente cliente;
    private String numeroCarta;
    private String scadenza;
    private String cvv;
    private String circuito;
    private PortafoglioVirtuale portafoglioVirtuale;
    private Connection conn;

    public CartaCredito(int idUtente, String numeroCarta, String scadenza, String cvv, String circuito, int idPortafoglioVirtuale, Connection conn) {
        this.conn = conn;
        this.numeroCarta = numeroCarta;
        this.scadenza = scadenza;
        this.cvv = cvv;
        this.circuito = circuito;
    }
    
    public Cliente getUtente() {
        return cliente;
    }

    public void setUtente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getNumeroCarta() {
        return numeroCarta;
    }

    public void setNumeroCarta(String numeroCarta) {
        this.numeroCarta = numeroCarta;
    }

    public String getScadenza() {
        return scadenza;
    }

    public void setScadenza(String scadenza) {
        this.scadenza = scadenza;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getCircuito() {
        return circuito;
    }

    public void setCircuito(String circuito) {
        this.circuito = circuito;
    }

    public PortafoglioVirtuale getPortafoglioVirtuale(){
        return portafoglioVirtuale;
    }

    
}


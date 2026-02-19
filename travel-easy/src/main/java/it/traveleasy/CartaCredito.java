package it.traveleasy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CartaCredito {
    private Cliente cliente;
    private String numeroCarta;
    private String scadenza;
    private String cvv;
    private String circuito;
    private PortafoglioVirtuale portafoglioVirtuale;
    private Connection conn;

    public CartaCredito(String numeroCarta, String scadenza, String cvv, String circuito, int idPortafoglioVirtuale, Cliente cliente, Connection conn) {
        this.conn = conn;
        this.cliente = cliente;
        this.portafoglioVirtuale = this.cliente.getPv();
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

    public boolean controlCvv(String cvvByUtente){
        if (cvv.equals(cvvByUtente))
            return true;
        else
            return false;
    }

    public boolean insertOnPortafoglio(int idUtente, float importo){
        String query = "UPDATE PortafoglioVirtuale SET Saldo = Saldo + ? WHERE Utente = ?;";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setFloat(1, importo);
            pstmt.setInt(2, idUtente);
            pstmt.executeUpdate();

            this.portafoglioVirtuale.incrementaSaldo(importo);
            return true;
        } catch (SQLException e){
            System.out.println("Errore insertOnPortafoglio: "+e);
            return false;
        }
    }
}


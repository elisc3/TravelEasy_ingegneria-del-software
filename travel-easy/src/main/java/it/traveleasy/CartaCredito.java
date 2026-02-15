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
        //this.portafoglioVirtuale = this.recuperaPortafoglioById(idPortafoglioVirtuale);
        //this.cliente = this.recuperaUtente(idUtente);
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

    /*private PortafoglioVirtuale recuperaPortafoglioById(int idPortafoglio){
        String query = "SELECT * FROM PortafoglioVirtuale WHERE id = ?";


        try (PreparedStatement pstmt = conn.prepareStatement(query)){
                pstmt.setInt(1, idPortafoglio);
                ResultSet rs = pstmt.executeQuery();
                
                PortafoglioVirtuale pv = null;
                while (rs.next()){
                    int id = rs.getInt("id");
                    int utenteId = rs.getInt("Utente");
                    double saldo = rs.getDouble("Saldo");   
                    pv = new PortafoglioVirtuale(id, utenteId, saldo);
                }

                return pv;
            } catch (SQLException e){
                System.out.println("Errore getPacchettiByFilter:"+e);
                return null;
            } 
        
    }*/

    public boolean insertOnPortafoglio(int idUtente, float importo){
        String query = "UPDATE PortafoglioVirtuale SET Saldo = Saldo + ? WHERE Utente = ?;";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setFloat(1, importo);
            pstmt.setInt(2, idUtente);
            pstmt.executeUpdate();

            this.portafoglioVirtuale.incrementaSaldo(importo);
            System.out.println("Saldo aggiornato della classe CartaCredito: "+this.portafoglioVirtuale.getSaldo());
            this.cliente.stampaSaldoPortafoglio();
            return true;
        } catch (SQLException e){
            System.out.println("Errore insertOnPortafoglio: "+e);
            return false;
        }
    }
    
    /*private PortafoglioOre recuperaPortafoglioOre(int idCliente){
        String query = "SELECT * FROM PortafoglioOre WHERE proprietario = ?;";

        try (PreparedStatement pstmt = conn.prepareStatement(query)){
                pstmt.setInt(1, idCliente);
                ResultSet rs = pstmt.executeQuery();
                
                PortafoglioOre pv = null;
                while (rs.next()){
                    int id = rs.getInt("id");
                    
                }

                return pv;
            } catch (SQLException e){
                System.out.println("Errore getPacchettiByFilter:"+e);
                return null;
            }

    }
    

    private Cliente recuperaUtente(int idUtente){
        String query = "SELECT * FROM Utenti WHERE id = ?;";

        try (PreparedStatement pstmt = conn.prepareStatement(query)){
                pstmt.setInt(1, idUtente);
                ResultSet rs = pstmt.executeQuery();

                Cliente c = null;
                
                while (rs.next()){
                    int id = rs.getInt("id");
                    String nome = rs.getString("Nome");
                    String cognome = rs.getString("Cognome");
                    String telefono = rs.getString("Telefono");
                    String ruolo = rs.getString("Ruolo");
                    int account = rs.getInt("Account");

                    PortafoglioOre po = this.recuperaPortafoglioOre(id);
                    c = new Cliente(id, nome, cognome, telefono, ruolo, account, portafoglioVirtuale, this, po);
                }
                return c;
        } catch (SQLException e){
            System.out.println("Errore insertOnPortafoglio: "+e);
            return null;
        }

    }*/
}


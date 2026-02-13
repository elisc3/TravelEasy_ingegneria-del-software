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

    public boolean metodiPagamento(Connection conn){
        String query = "INSERT INTO PortafoglioVirtuale (Utente, Saldo) VALUES (?, ?);";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, this.getId());
            pstmt.setDouble(2, 0.0);
            pstmt.executeUpdate();
        } catch (SQLException e){
            System.out.println("Errore creazione Portafoglio Virtuale: "+e);
            return false;
        }

        query = "SELECT id FROM PortafoglioVirtuale WHERE Utente = ?;";
        int idPortafoglio = 0;
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, this.getId());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
                idPortafoglio = rs.getInt("id");
        } catch (SQLException e){
            System.out.println("Errore recupero id Portafoglio Virtuale: "+e);
            return false;
        }

        PortafoglioVirtuale pv = new PortafoglioVirtuale(idPortafoglio, this.getId(), 0.0);
        setPv(pv);

        int idPortafoglioVirtuale = pv.getId();
        
        query= "INSERT INTO CartaCredito (Utente, NumeroCarta, Scadenza, cvv, Circuito, PortafoglioVirtuale) VALUES (?, ?, ?, ?, ?, ?);";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, this.getId());
            pstmt.setString(2, "");
            pstmt.setString(3, "");
            pstmt.setString(4, "");
            pstmt.setString(5, "");
            pstmt.setInt(6, idPortafoglioVirtuale);
            pstmt.executeUpdate();
        } catch (SQLException e){
            System.out.println("Errore creazione carta di credito: "+e);
            return false;
        }



        this.cc = new CartaCredito(this.getId(), "", "", "", "", idPortafoglioVirtuale, conn);
        //te.aggiornaElencoCarte(this.getId(), cc);
        return true;
    }

    public boolean pagamentoOnPortafoglioDB(Connection conn, float importo){
        String query = "UPDATE PortafoglioVirtuale SET Saldo = Saldo - ? WHERE Utente = ?;";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setFloat(1, importo);
            pstmt.setInt(2, this.getId());
            pstmt.executeUpdate();

            this.pv.decrementaSaldo(importo);
            return true;
        } catch (SQLException e){
            System.out.println("Errore pagamentoPortafoglio: "+e);
            return false;
        }
    }

    public boolean rimborsoOnPortafoglioDB(Connection conn, float importo){
        String query = "UPDATE PortafoglioVirtuale SET Saldo = Saldo + ? WHERE Utente = ?;";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setFloat(1, importo);
            pstmt.setInt(2, this.getId());
            pstmt.executeUpdate();

            this.pv.incrementaSaldo(importo);
            return true;
        } catch (SQLException e){
            System.out.println("Errore rimborsoPortafoglio: "+e);
            return false;
        }
    }
    
}
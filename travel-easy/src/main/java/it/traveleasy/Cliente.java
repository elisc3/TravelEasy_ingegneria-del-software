package it.traveleasy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Cliente extends Utente {
    private PortafoglioVirtuale pv; 
    private CartaCredito cc;
    private TravelEasy te = null;
    private PortafoglioOre po;
    private Map<Integer, Prenotazione> elencoPrenotazioniEffettuate;
    private Map<Integer, Recensione> elencoRecensioni;
    private int nRiferimentiRecensione = 3;

    public Cliente(int id, String nome, String cognome, String Telefono, String ruolo, int Account, PortafoglioVirtuale pv, CartaCredito cc, PortafoglioOre po) {
        super(id, nome, cognome, Telefono, ruolo, Account);
        this.pv = pv;
        this.cc = cc;
        this.po = po;
        this.elencoPrenotazioniEffettuate = new HashMap<>();
        this.elencoRecensioni = new HashMap<>();
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

    public PortafoglioOre getPo() {
        return po;
    }

    public void setPo(PortafoglioOre po) {
        this.po = po;
    }

    public void incrementaPortafoglio(float importo){
        this.pv.incrementaSaldo(importo);
        //System.out.println("Saldo aggiornato della classe cliente: "+this.pv.getSaldo());
    }

    public void decrementaPortafoglio(float importo){
        this.pv.decrementaSaldo(importo);
        //System.out.println("Saldo aggiornato della classe cliente: "+this.pv.getSaldo());
    }

    public void stampaSaldoPortafoglio(){
        //System.out.println("Saldo aggiornato della classe cliente: "+this.pv.getSaldo());
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



        this.cc = new CartaCredito("", "", "", "", idPortafoglioVirtuale, this, conn);
        //te.aggiornaElencoCarte(this.getId(), cc);

        query = "INSERT INTO PortafoglioOre (Utente, Ore, Sconto) VALUES (?, ?, ?);";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, this.getId());
            pstmt.setFloat(2, 0.0F);
            pstmt.setInt(3, 0);
            pstmt.executeUpdate();
        } catch (SQLException e){
            System.out.println("Errore creazione Portafoglio Ore: "+e);
            return false;
        }

         query = "SELECT id FROM PortafoglioOre WHERE Utente = ?;";
         int idPortafoglioOre = 0;
         try (PreparedStatement pstmt = conn.prepareStatement(query)) {
             pstmt.setInt(1, this.getId());
             ResultSet rs = pstmt.executeQuery();
             if (rs.next())
                 idPortafoglioOre = rs.getInt("id");
         } catch (SQLException e){
             System.out.println("Errore recupero id Portafoglio Ore: "+e);
             return false;
         }

         PortafoglioOre po = new PortafoglioOre(idPortafoglioOre, this.getId(), 0.0f, 0);
         setPo(po);

        return true;
    }

    public boolean eliminaMetodiPagamento(Connection conn){
        String query = "DELETE FROM CartaCredito WHERE Utente = ?;";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, this.getId());
            pstmt.executeUpdate();
        } catch (SQLException e){
            System.out.println("Errore eliminazione carta di credito: "+e);
            return false;
        }

        query = "DELETE FROM PortafoglioVirtuale WHERE Utente = ?;";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, this.getId());
            pstmt.executeUpdate();
        } catch (SQLException e){
            System.out.println("Errore eliminazione portafoglio virtuale: "+e);
            return false;
        }

        query = "DELETE FROM PortafoglioOre WHERE Utente = ?;";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, this.getId());
            pstmt.executeUpdate();
        } catch (SQLException e){
            System.out.println("Errore eliminazione portafoglio ore: "+e);
            return false;
        }

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



    public void addPrenotazione(Prenotazione p){
        this.elencoPrenotazioniEffettuate.put(p.getId(), p);
    }

    public Map<Integer, Prenotazione> getElencoPrenotazioniEffettuate() {
        return this.elencoPrenotazioniEffettuate;
    }

    public void addRecensione(Recensione r){
        this.elencoRecensioni.put(r.getId(), r);
    }

    public Recensione[] getRecensioneByPrenotazione(int idPrenotazione){
        Recensione[] recensione = new Recensione[nRiferimentiRecensione];
        int count = 0;
        for (Recensione r: elencoRecensioni.values()){
            if (r.getPrenotazione().getId() == idPrenotazione){
                recensione[count++] = r;
                if (count == nRiferimentiRecensione)
                    return recensione;
            }   
        }
        return null;
    }
    
}

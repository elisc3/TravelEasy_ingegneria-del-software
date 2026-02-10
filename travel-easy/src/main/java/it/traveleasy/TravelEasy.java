package it.traveleasy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.Iterator;


import javax.swing.JOptionPane;


public class TravelEasy {
    private Map<String, Account> elencoAccount;
    //private Map<Integer, PortafoglioVirtuale> mappaPortafogli;
    private Connection conn;
    private Map<Integer, CompagniaTrasporto> elencoCompagnie;
    private Map<Integer, Alloggio> elencoAlloggi;
    private Map<Integer, CartaCredito> elencoCarte;
    private Map<Integer, PacchettoViaggio> elencoPacchetti;
    private Map<PacchettoViaggio, OffertaSpeciale> elencoOfferte;

    public TravelEasy(Connection conn){
        this.conn = conn;

        /*mappaPortafogli = this.recuperaPortafogliVirtuali();
        if (mappaPortafogli == null) {
            mappaPortafogli = new HashMap<>();
        }*/

        
        elencoCarte = this.recuperaCarte();
        if (elencoCarte == null) {
            elencoCarte = new HashMap<>();
        }

        elencoAccount = this.recuperaAccount();
        if (elencoAccount == null) {
            elencoAccount = new HashMap<>();
        }


       

        elencoCompagnie = this.recuperaCompagnie();
        if (elencoCompagnie == null)
            elencoCompagnie = new HashMap<>();

        elencoAlloggi = this.recuperaAlloggi();
        if (elencoAlloggi == null)
            elencoAlloggi = new HashMap<>();

        elencoPacchetti = this.recuperaPacchetti();
        if (elencoPacchetti == null)
            elencoPacchetti = new HashMap<>();

        elencoOfferte = this.recuperaOfferte();
        if (elencoOfferte == null)
            elencoOfferte = new HashMap<>();
    }

    public Map<Integer, Alloggio> recuperaAlloggi() {
        String query = "SELECT * FROM Alloggio;";

        Map<Integer, Alloggio> mappa = new HashMap<>();
        
        try (PreparedStatement pstmt = conn.prepareStatement(query)){
                
                ResultSet rs = pstmt.executeQuery();
                
                while (rs.next()){
                    int id = rs.getInt("id");
                    String nome = rs.getString("Nome");
                    String indirizzo = rs.getString("Indirizzo");
                    String tipo = rs.getString("TIPO");
                    int stelle = rs.getInt("Stelle");

                    mappa.put(id, new Alloggio(id, nome, indirizzo, tipo, stelle));
                }
                return mappa;
            } catch (SQLException e){
                System.out.println("Errore recupera alloggi:"+e);
                return null;
            } 
    }

    private Map<Integer, CartaCredito> recuperaCarte(){
        String query = "SELECT * FROM CartaCredito";
        Map<Integer, CartaCredito> mappa = new HashMap<>();

        try (PreparedStatement pstmt = conn.prepareStatement(query)){
            ResultSet rs = pstmt.executeQuery();
                
            while (rs.next()){
                int id = rs.getInt("id");
                int idUtente = rs.getInt("Utente");
                String numeroCarta = rs.getString("NumeroCarta");
                String scadenza = rs.getString("Scadenza");
                String cvv = rs.getString("cvv");
                String circuito = rs.getString("Circuito");
                int idPortafoglio = rs.getInt("PortafoglioVirtuale");

                mappa.put(idUtente, new CartaCredito(idUtente, numeroCarta, scadenza, cvv, circuito, idPortafoglio, conn));
            }
                return mappa;
        } catch (SQLException e){
            System.out.println("Errore in recupera carte: "+e);
            return null;
        }

    }

    public void aggiornaElencoCarte(int idUtente, CartaCredito cc){
        this.elencoCarte.put(idUtente, cc);
    }

    private Map<Integer, PacchettoViaggio> recuperaPacchetti(){
        String query = "SELECT * from PacchettiViaggio";

        Map<Integer, PacchettoViaggio> mappa = new HashMap<>();
        try (PreparedStatement pstmt = conn.prepareStatement(query)){
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){
                int id = rs.getInt("id");
                String codice = rs.getString("Codice");
                String titolo = rs.getString("Titolo");
                String citta= rs.getString("Città");
                String nazione = rs.getString("Nazione");
                String dataPartenza = rs.getString("DataPartenza");
                String dataRitorno = rs.getString("DataRitorno");
                String descrizione = rs.getString("Descrizione");
                float prezzo= rs.getFloat("Prezzo");
                int visibilità = rs.getInt("Visibilità");
                int idCompagniaTrasporto = rs.getInt("CompagniaTrasporto");
                int idAlloggio = rs.getInt("Alloggio");

                mappa.put(id, new PacchettoViaggio(id, codice, titolo, citta, nazione, dataPartenza, dataRitorno, descrizione, prezzo, visibilità, idCompagniaTrasporto, idAlloggio, conn));
            }
            return mappa;
        } catch (SQLException e){
            System.out.println("Errore recupero pacchetti: "+e);
            return null;
        }
    }

    public List<PacchettoViaggio> ricercaPacchetti(Connection conn, String città, String dataAndata, String dataRitorno, float prezzoMassimo){
            //  METODO PER UC2
            /*String query = "SELECT * FROM PacchettiViaggio "
                            +"WHERE Città = ? and DataPartenza = ? and DataRitorno = ? and Prezzo <= ? and Visibilità=?;";

            List<PacchettoViaggio> pacchetti = new ArrayList<>();

            if (conn == null) {
                return null;
            }

            try (PreparedStatement pstmt = conn.prepareStatement(query)){
                pstmt.setString(1, città);
                pstmt.setString(2, dataAndata);
                pstmt.setString(3, dataRitorno);
                pstmt.setFloat(4, prezzoMassimo);
                pstmt.setInt(5, 1);
                
                ResultSet rs = pstmt.executeQuery();
                
                while (rs.next()){
                    int idPacchetto = rs.getInt("id");
                    String titolo = rs.getString("Titolo");
                    String cittaR = rs.getString("Città");
                    String nazione = rs.getString("Nazione");
                    String dataPartenzaR = rs.getString("DataPartenza");
                    String dataRitornoR = rs.getString("DataRitorno");
                    String descrizione = rs.getString("Descrizione");
                    float prezzo= rs.getFloat("Prezzo");
                    int visibilità = rs.getInt("Visibilità");
                    int idCompagniaTrasporto = rs.getInt("CompagniaTrasporto");
                    int idAlloggio = rs.getInt("Alloggio");
                    
                    pacchetti.add(new PacchettoViaggio(idPacchetto, titolo, cittaR, nazione, dataPartenzaR, dataRitornoR, descrizione, prezzo, visibilità, idCompagniaTrasporto, idAlloggio));
                }
                    return pacchetti;
            } catch (SQLException e){
                System.out.println("Errore getPacchettiByFilter:"+e);
                return null;
            }  */
           
            List<PacchettoViaggio> pacchettiTrovati = new ArrayList<>();
            for (PacchettoViaggio p : this.elencoPacchetti.values()){
                String cittaP = p.getCittà();
                String dataAndataP = p.getDataPartenza();
                String dataRitornoP = p.getDataRitorno();
                int idPacchetto = p.getId();
                String codice = p.getCodice();
                OffertaSpeciale o = this.getOffertaByPack(p);
                float prezzoVero;
                if (o != null)
                    prezzoVero = o.getPrezzoScontato();
                else
                    prezzoVero = p.getPrezzo();

                if (cittaP.equals(città) && dataAndataP.equals(dataAndata) && dataRitornoP.equals(dataRitorno) && prezzoVero <= prezzoMassimo && p.isVisibilità() == 1)
                    pacchettiTrovati.add(new PacchettoViaggio(idPacchetto,  codice, p.getTitolo(), cittaP, p.getNazione(), dataAndataP, dataRitornoP, p.getDescrizione(), p.getPrezzo(), p.isVisibilità(), p.getIdCompagniaTrasporto(), p.getIdAlloggio(), conn));
            }
            return pacchettiTrovati;
    }

    public Alloggio getAlloggioByPacchetto(int idAlloggio){
        /*String query = "SELECT * FROM Alloggio WHERE id = ?;";
        Alloggio a = null;

        try (PreparedStatement pstmt = conn.prepareStatement(query)){
                pstmt.setInt(1, idAlloggio);
                ResultSet rs = pstmt.executeQuery();
                
                while (rs.next()){
                    String nome = rs.getString("Nome");
                    String indirizzo = rs.getString("Indirizzo");
                    String tipo = rs.getString("TIPO");
                    int stelle = rs.getInt("Stelle");

                    a = new Alloggio(idAlloggio, nome, indirizzo, tipo, stelle);
                }
                return a;
            } catch (SQLException e){
                System.out.println("Errore getPacchettiByFilter:"+e);
                return null;
            }  */
        return this.elencoAlloggi.get(idAlloggio);
    }

    public CartaCredito getCartaCreditoByUtente(int idCliente){
        return this.elencoCarte.get(idCliente);
    }

    public boolean controlCvv(Connection conn, int idUtente, String cvvByUtente){
        /*String query = "SELECT cvv FROM CartaCredito WHERE Utente = ?;";

        try (PreparedStatement pstmt = conn.prepareStatement(query)){
            pstmt.setInt(1, idUtente);

            ResultSet rs = pstmt.executeQuery();

            if(!rs.next())
                return false;

            String cvv = rs.getString("cvv");
            
            if (cvv.equals(cvvByUtente))
                return true;
            else
                return false;

        } catch (SQLException e) {
            System.out.println("Errore getCartaCreditoByUtente:"+e);
            return false;
        }*/

        CartaCredito cc = this.elencoCarte.get(idUtente);
        String cvv = cc.getCvv();

        if (cvv.equals(cvvByUtente))
            return true;
        else
            return false;
    }

    

    public float validazioneDatiNuovaRicarica(String numeroCarta, String scadenza, String cvv, String importo){
        if (numeroCarta.equals("") || scadenza.equals("") || cvv.equals("") || importo.equals(""))
            return -1.0F;
        
        try {
            int provaCvv = Integer.parseInt(cvv);
            if (provaCvv < 100 || provaCvv > 999)
                return -2.0F;
        } catch(Exception e) {
            return -3.0F;
        }

        if (scadenza == null || !scadenza.matches("^(0[1-9]|1[0-2])/\\d{2}$"))
            return -4.0F;
        float importoProva = 0.0F;
        try {
            importoProva = Float.parseFloat(importo);
            if (importoProva < 0.0F)
                return -5;
        } catch (Exception e){
            return -6;
        }
        
        return importoProva;
    }

    private PortafoglioVirtuale getPortafoglioByCliente(int idUtente){
        for (Account a: elencoAccount.values()){
            Cliente c = a.getCliente();
            if (c.getId() == idUtente)
                return c.getPv();
        }
        return null;
    }



    public boolean insertCartaCredito(Connection conn, int idUtente, String numeroCarta, String scadenza, String cvv, String circuito){
        String query = "UPDATE CartaCredito SET NumeroCarta = ?, Scadenza = ?, cvv = ?, Circuito = ?, PortafoglioVirtuale = ? WHERE Utente = ?;";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, numeroCarta);
            pstmt.setString(2, scadenza);
            pstmt.setString(3, cvv);
            pstmt.setString(4, circuito);
            
            int idPortafoglio = 0;
            PortafoglioVirtuale pv = this.getPortafoglioByCliente(idUtente);
            if (pv != null)
                idPortafoglio = pv.getId();

            pstmt.setInt(5, idPortafoglio);
            pstmt.setInt(6, idUtente);
            pstmt.executeUpdate();

            elencoCarte.put(idUtente, new CartaCredito(idUtente, numeroCarta, scadenza, cvv, circuito, idPortafoglio, conn));
            return true;
        } catch (SQLException e){
            System.out.println("Errore insertOnPortafoglio: "+e);
            return false;
        }
    }

    private PortafoglioVirtuale getPortafoglioByClienteDB(int idCliente){
        String query = "SELECT * FROM PortafoglioVirtuale WHERE Utente = ?;";

        try (PreparedStatement pstmt = conn.prepareStatement(query)){
            pstmt.setInt(1, idCliente);

            ResultSet rs = pstmt.executeQuery();
            PortafoglioVirtuale pv = null;
            while(rs.next()){
                int id = rs.getInt("id");
                int idClienteD = rs.getInt("Utente");
                double saldo = rs.getDouble("Saldo");
                pv = new PortafoglioVirtuale(id, idClienteD, saldo);
            }
            return pv;
        } catch (SQLException e) {
            System.out.println("Errore getPortafoglioByClienteDB: "+e);
            return null;
        }
    }

    public boolean eliminaOfferteDB(OffertaSpeciale o){
        String query = "DELETE FROM OffertaSpeciale WHERE id = ?;";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, o.getId());

            pstmt.executeUpdate();
            return true;
        } catch (SQLException e){
            System.out.println("Errore nuovo pacchetto: "+e);
            return false;
        }

    }
}

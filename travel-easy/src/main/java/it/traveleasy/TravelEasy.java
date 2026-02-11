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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;


public class TravelEasy {
    private Map<String, Account> elencoAccount;
    //private Map<Integer, PortafoglioVirtuale> mappaPortafogli;
    private Connection conn;
    private Map<Integer, CompagniaTrasporto> elencoCompagnie;
    private Map<Integer, Alloggio> elencoAlloggi;
    //private Map<Integer, CartaCredito> elencoCarte;
    private Map<Integer, PacchettoViaggio> elencoPacchetti;
    private Map<PacchettoViaggio, OffertaSpeciale> elencoOfferte;

    public TravelEasy(Connection conn){
        this.conn = conn;

        /*mappaPortafogli = this.recuperaPortafogliVirtuali();
        if (mappaPortafogli == null) {
            mappaPortafogli = new HashMap<>();
        }*/

        
        /*elencoCarte = this.recuperaCarte();
        if (elencoCarte == null) {
            elencoCarte = new HashMap<>();
        }*/

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

    //* RECUPERO MAPPE
    private Map<String, Account> recuperaAccount() {
        Map<String, Account> mappa = new HashMap<>();

        String query = "SELECT * FROM ACCOUNT";

         

        try (PreparedStatement pstmt = conn.prepareStatement(query)){
                
                ResultSet rs = pstmt.executeQuery();
                Cliente cliente = null;
                
                while (rs.next()){
                    int id = rs.getInt("id");
                    String email = rs.getString("Email");
                    String password = rs.getString("Password");
                    String ruolo = rs.getString("Ruolo");

                    String queryCliente = "SELECT * FROM Utenti WHERE Account = ?;";
                    try (PreparedStatement pstmtCliente = conn.prepareStatement(queryCliente)){
                        pstmtCliente.setInt(1, id);
                        ResultSet rsCliente = pstmtCliente.executeQuery();
                        if (rsCliente.next()){
                            int idCliente = rsCliente.getInt("id");
                            String nome = rsCliente.getString("Nome");
                            String cognome = rsCliente.getString("Cognome");
                            String telefono = rsCliente.getString("Telefono");
                            String ruoloCliente = rsCliente.getString("Ruolo");
                            //PortafoglioVirtuale pv = mappaPortafogli.get(idCliente);
                            PortafoglioVirtuale pv = this.getPortafoglioByClienteDB(idCliente);
                            //CartaCredito cc = this.elencoCarte.get(idCliente);
                            CartaCredito cc = this.getCartaCreditoByUtente(idCliente);
                            cliente = new Cliente(idCliente, nome, cognome, telefono, ruoloCliente, id, pv, cc);
                        }
                    } catch (SQLException e){
                        System.out.println("Errore recupero cliente in account: "+e);
                        return null;
                    }

                    mappa.put(email, new Account(conn, id, email, password, ruolo, cliente));
                    
                }
                return mappa;
            } catch (SQLException e){
                System.out.println("Errore getPacchettiByFilter:"+e);
                return null;
            } 
    }

    public CartaCredito getCartaCreditoByUtente(int idUtente){
        String query = "SELECT * FROM CartaCredito WHERE Utente = ?;";

        try (PreparedStatement pstmt = conn.prepareStatement(query)){
            pstmt.setInt(1, idUtente);
            ResultSet rs = pstmt.executeQuery();
            CartaCredito cc = null;
                
            while (rs.next()){
                int id = rs.getInt("id");
                int idUtenteD = rs.getInt("Utente");
                String numeroCarta = rs.getString("NumeroCarta");
                String scadenza = rs.getString("Scadenza");
                String cvv = rs.getString("cvv");
                String circuito = rs.getString("Circuito");
                int idPortafoglio = rs.getInt("PortafoglioVirtuale");

                cc = new CartaCredito(idUtenteD, numeroCarta, scadenza, cvv, circuito, idPortafoglio, conn);
            }
            return cc;
            
        } catch (SQLException e){
            System.out.println("Errore getCartaCreditoByUtente: "+e);
            return null;
        }


    }

    public Map<Integer, CompagniaTrasporto> recuperaCompagnie() {
        String query = "SELECT * FROM CompagniaTrasporto;";

        Map<Integer, CompagniaTrasporto> mappa = new HashMap<>();
        
        try (PreparedStatement pstmt = conn.prepareStatement(query)){
                
                ResultSet rs = pstmt.executeQuery();
                
                while (rs.next()){
                    int id = rs.getInt("id");
                    String nome = rs.getString("Nome");
                    String tipo = rs.getString("TIPO");

                    mappa.put(id, new CompagniaTrasporto(id, nome, tipo));
                }
                return mappa;
            } catch (SQLException e){
                System.out.println("Errore recupera compagnie:"+e);
                return null;
            } 
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

    /*private Map<Integer, CartaCredito> recuperaCarte(){
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

    }*/

    /*public void aggiornaElencoCarte(int idUtente, CartaCredito cc){
        this.elencoCarte.put(idUtente, cc);
    }*/

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

    public void aggiornaElencoPacchetti(PacchettoViaggio p){
        this.elencoPacchetti.put(p.getId(), p);
    }

    public Map<PacchettoViaggio, OffertaSpeciale> recuperaOfferte(){
        String query = "SELECT * FROM OffertaSpeciale";
        Map<PacchettoViaggio, OffertaSpeciale> mappa = new HashMap<>();

        try (PreparedStatement pstmt = conn.prepareStatement(query)){
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()){
                int id = rs.getInt("id");
                int idPacchetto = rs.getInt("Pacchetto");
                float percentuale = rs.getFloat("ScontoPercentuale");
                float prezzoScontato = rs.getFloat("PrezzoScontato");
                String dataFine = rs.getString("DataFine");
                int disponibilità = rs.getInt("Disponibilità");
               // boolean visibilità = rs.getBoolean("Visibilità");

                PacchettoViaggio pacchetto = this.elencoPacchetti.get(idPacchetto);
                mappa.put(pacchetto, new OffertaSpeciale(id, pacchetto, percentuale, prezzoScontato, dataFine, disponibilità /*, visibilità*/));
            }
            return mappa;
        } catch (SQLException e){
            System.out.println("Errore recupero offerte: "+e);
            return null;
        }

    }

    public void aggiornaOfferte(OffertaSpeciale o){
        this.elencoOfferte.put(o.getPacchetto(), o);
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

    public CompagniaTrasporto getCompagniaTrasportoByPacchetto(int idCompagnia){
        /*String query = "SELECT * FROM CompagniaTrasporto WHERE id = ?;";
        CompagniaTrasporto c = null;
        try (PreparedStatement pstmt = conn.prepareStatement(query)){
                pstmt.setInt(1, idCompagnia);
                ResultSet rs = pstmt.executeQuery();
                
                while (rs.next()){
                    String nome = rs.getString("Nome");
                    String tipo = rs.getString("TIPO");

                    c = new CompagniaTrasporto(idCompagnia, nome, tipo);
                }
                return c;
            } catch (SQLException e){
                System.out.println("Errore getPacchettiByFilter:"+e);
                return null;
            }     */
        return this.elencoCompagnie.get(idCompagnia);   
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


    //*REGISTRAZIONE
    private boolean checkEmail(String emailByUser){
        return !elencoAccount.containsKey(emailByUser);
    }

    private boolean validazioneDati(String nome, String cognome, String email, String password, String confermaPassword, String telefono) {
        
        if (nome.equals("") || cognome.equals("") || email.equals("") || password.equals("") || confermaPassword.equals("") || telefono.equals(""))
            return false;

        Pattern patternEMAIL =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        
        if (!patternEMAIL.matcher(email.trim()).matches())
            return false;

        if (!password.equals(confermaPassword))
            return false;

        return true;
    }

    private int recuperaIdAccount(String email){
        String query = "SELECT id FROM Account where Email = ?;";

        try (PreparedStatement pstmt = conn.prepareStatement(query)){
                pstmt.setString(1, email);
                
                ResultSet rs = pstmt.executeQuery();
                int idRecuperato = 0;
                while (rs.next()){
                    idRecuperato = rs.getInt("id");
                }
                return idRecuperato;
            } catch (SQLException e){
                System.out.println("Errore recuperaId in account:"+e);
                return 0;
            }
    }

    public String registrazione(Connection conn, String nome, String cognome, String email, String password, String confermaPassword, String telefono){
        if (!validazioneDati(nome, cognome, email, password, confermaPassword, telefono)) {
            System.out.println("registrazione: validazioneDati fallita");
            return "errore";
        }

        if (!checkEmail(email)) {
            System.out.println("registrazione: email gia' presente");
            return "errore";
        }

        String query = "INSERT INTO Account (Email, Password, Ruolo) values (?, ?, ?);";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            pstmt.setString(3, "Cliente");
            pstmt.executeUpdate();
            
        } catch (SQLException e){
            System.out.println("Errore registrazioneInAccount: "+e);
            return "errore";
        }

        int idAccount = recuperaIdAccount(email);

        if(idAccount == 0) {
            System.out.println("registrazione: idAccount non recuperato");
            return "errore";
        }

        Account newAccount = new Account(conn, idAccount, email, password, "Cliente", null);

        int idCliente = newAccount.createClient(conn, nome, cognome, telefono);

        if (idCliente == 0) {
            System.out.println("registrazione: creazione cliente fallita");
            return "errore";
        }

        //PortafoglioVirtuale pv = newAccount.getPortafoglioVirtuale();
        //mappaPortafogli.put(idCliente, pv);

        CartaCredito cc = newAccount.getCartaCredito();
        //elencoCarte.put(idCliente, cc);
        
        elencoAccount.put(email, newAccount);

        return newAccount.getEmail();
    }
    
    //*LOGIN
    public String[] login(Connection conn, String email, String password){
        Account a = elencoAccount.get(email);

        String[] res = new String[2];

        if(a == null) {
            res[0] = "errore";
            return res;
        }

        if (!a.validazioneCredenziali(email, password)) {
            res[0] = "errore";
            return res;
        }

        res[0] = a.getEmail();
        res[1] = a.getRuolo();

        return res;
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

            //elencoCarte.put(idUtente, new CartaCredito(idUtente, numeroCarta, scadenza, cvv, circuito, idPortafoglio, conn));
            Cliente cliente = null;
            for (Account a: elencoAccount.values()){
                Cliente c = a.getCliente();
                if (c.getId() == idUtente){
                    cliente = c;
                    break;
                }
            }
            cliente.setCc(new CartaCredito(idUtente, numeroCarta, scadenza, cvv, circuito, idPortafoglio, conn));
            return true;
        } catch (SQLException e){
            System.out.println("Errore insertOnPortafoglio: "+e);
            return false;
        }
    }

    public float validazioneDatiNuovoPacchetto(String titolo, String citta, String nazione, String descrizione, String prezzo, String compagnia, String alloggio, String dataPartenza, String dataRitorno){
        if (titolo.equals("") || citta.equals("") || nazione.equals("") || descrizione.equals("") || prezzo.equals("") || compagnia.equals("") || alloggio.equals("") || dataPartenza.equals("") || dataRitorno.equals(""))
            return -1.0F;
        float prezzoF;
        try {
            prezzoF = Float.parseFloat(prezzo);
            if (prezzoF <= 0.0F)
                return 0.0F;
            return prezzoF;
        } catch (Exception e){
            return 0.0F;
        }
    }

    private int controllaPacchettiDuplicati(String codice ,String citta, String nazione, String dataPartenza, String dataRitorno, int idCompagnia, int idAlloggio, float prezzo){
        /*String query = "SELECT * FROM PacchettiViaggio WHERE Città = ? AND Nazione = ? and DataPartenza = ? and DataRitorno = ? and CompagniaTrasporto = ? and Alloggio = ?;";

         try (PreparedStatement pstmt = conn.prepareStatement(query)){
            pstmt.setString(1, citta);
            pstmt.setString(2, nazione);
            pstmt.setString(3, dataPartenza);
            pstmt.setString(4, dataRitorno);
            pstmt.setInt(5, idCompagnia);
            pstmt.setInt(6, idAlloggio);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()){
                JOptionPane.showMessageDialog(null, "Esiste già un pacchetto viaggio con queste caratteristiche", "ATTENZIONE", 2);
                return false;
            } else {
                return true;
            }
            
        } catch (SQLException e) {
            System.out.println("Errore getCartaCreditoByUtente:"+e);
            
            return false;
        }*/

        for (PacchettoViaggio p : elencoPacchetti.values()){
            if (p.getCodice().equals(codice))
                return -1;
        }
        

        for (PacchettoViaggio p : elencoPacchetti.values()){
            if (p.getCittà().equals(citta) && p.getNazione().equals(nazione) && p.getDataPartenza().equals(dataPartenza) && p.getDataRitorno().equals(dataRitorno) && p.getIdCompagniaTrasporto() == idCompagnia && p.getIdAlloggio() == idAlloggio && p.getPrezzo() == prezzo)
                return -2;
        }
        return 0;
        
    }

    public boolean coerenzaDate(String dataPartenza, String dataArrivo) {
        DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MM-uuuu")
                .withResolverStyle(ResolverStyle.STRICT);

        LocalDate date1 = LocalDate.parse(dataPartenza, FMT); // partenza
        LocalDate date2 = LocalDate.parse(dataArrivo, FMT);   // arrivo

        // controllo aggiuntivo: la partenza non deve essere precedente a oggi
        if (date1.isBefore(LocalDate.now())) {
            return false;
        }

        // partenza <= arrivo
        return !date1.isAfter(date2);
    }


    public boolean nuovoPacchetto(Connection conn, String codice, String titolo, String citta, String nazione, String descrizione, float prezzo, int visibilità, String compagnia, String alloggio, String dataPartenza, String dataRitorno){
        String query = "INSERT INTO PacchettiViaggio (Città, Titolo, Nazione, DataPartenza, DataRitorno, Descrizione, Prezzo, Visibilità, CompagniaTrasporto, Alloggio, Codice) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        int idAlloggio = 0;
        for (Alloggio a : elencoAlloggi.values()) {
            if (a.getNome().equals(alloggio)){
                idAlloggio = a.getId();
                break;
            }
        }

        int idCompagniaTrasporto = 0;
        for (CompagniaTrasporto c : elencoCompagnie.values()){
            if (c.getNome().equals(compagnia)){
                idCompagniaTrasporto = c.getId();
                break;
            }
        }

        if (!this.coerenzaDate(dataPartenza, dataRitorno)){
            JOptionPane.showMessageDialog(null, "Date inserite non valide. Prego ricontrollare.", "ERRORE", 0);
            return false;
        }

        int esitoPacchettiDuplicati = this.controllaPacchettiDuplicati(codice, citta, nazione, dataPartenza, dataRitorno, idCompagniaTrasporto, idAlloggio, prezzo);

        if (esitoPacchettiDuplicati == -2){
            JOptionPane.showMessageDialog(null, "Esiste già un pacchetto con queste caratteristiche", "ATTENZIONE", 2);
            return false; 
        } else if (esitoPacchettiDuplicati == -1){
            JOptionPane.showMessageDialog(null, "Il codice inserito appartiene a un pacchetto già esistente.", "ERRORE", 0);
            return false;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, citta);
            pstmt.setString(2, titolo);
            pstmt.setString(3, nazione);
            pstmt.setString(4, dataPartenza);
            pstmt.setString(5, dataRitorno);
            pstmt.setString(6, descrizione);
            pstmt.setFloat(7, prezzo);
            pstmt.setInt(8, visibilità);
            pstmt.setInt(9, idCompagniaTrasporto);
            pstmt.setInt(10, idAlloggio);
            pstmt.setString(11, codice);

            pstmt.executeUpdate();
            int newId = 0;
            try (Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery("SELECT last_insert_rowid();")) {
                if (rs.next()) {
                     newId = rs.getInt(1);
                }
            }
            this.aggiornaElencoPacchetti(new PacchettoViaggio(newId, codice, titolo, citta, nazione, dataPartenza, dataRitorno, descrizione, prezzo, visibilità, idCompagniaTrasporto, idAlloggio, conn));
            return true;
        } catch (SQLException e){
            System.out.println("Errore nuovo pacchetto: "+e);
            return false;
        }
    }

    public Map<Integer, PacchettoViaggio> getElencoPacchetti(){
        return this.elencoPacchetti;
    }

    public OffertaSpeciale getOffertaByPack(PacchettoViaggio p){
        OffertaSpeciale o =  this.elencoOfferte.get(p);
        if (o != null)
            return o;
        else return null;
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

    public boolean eliminaOfferte() {
        DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);

        Iterator<Map.Entry<PacchettoViaggio, OffertaSpeciale>> it = elencoOfferte.entrySet().iterator();
        while (it.hasNext()) {
            OffertaSpeciale o = it.next().getValue();
            LocalDate fine = LocalDate.parse(o.getDataFine(), FMT);

            if (fine.isBefore(LocalDate.now())) {
                if (!eliminaOfferteDB(o))
                    return false;
                it.remove(); 
            }
        }
        return true;
    }
}

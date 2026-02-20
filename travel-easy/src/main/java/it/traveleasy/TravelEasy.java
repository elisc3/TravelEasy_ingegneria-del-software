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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;



public class TravelEasy implements AssistenzaObserver{
    private Map<String, Account> elencoAccount;
    private Connection conn;
    private Map<Integer, CompagniaTrasporto> elencoCompagnie;
    private Map<Integer, Alloggio> elencoAlloggi;
    private Map<Integer, PacchettoViaggio> elencoPacchetti;
    private Map<PacchettoViaggio, OffertaSpeciale> elencoOfferte;
    private final List<OffertaObserver> offertaObservers = new ArrayList<>();
    private final List<RicaricaObserver> ricaricaObservers = new ArrayList<>();
    private final List<PrenotazioneObserver> prenotazioneObservers = new ArrayList<>();
    private final List<RecensioneObserver> recensioneObservers = new ArrayList<>();
    private Map<Integer, Prenotazione> elencoPrenotazioni;
    private Map<Integer, Recensione> elencoRecensioni;

    public TravelEasy(Connection conn){
        this.conn = conn;

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

        elencoPrenotazioni = this.recuperaPrenotazioni();
        if (elencoPrenotazioni == null)
            elencoPrenotazioni = new HashMap<>();

        elencoRecensioni = this.recuperaRecensioni();
        if (elencoRecensioni == null)
            elencoRecensioni = new HashMap<>();
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
                            PortafoglioVirtuale pv = this.getPortafoglioByClienteDB(idCliente);
                            
                            PortafoglioOre po = this.gePortafoglioOreByUtente(idCliente);
                            cliente = new Cliente(idCliente, nome, cognome, telefono, ruoloCliente, id, pv, null, po);
                            CartaCredito cc = this.getCartaCreditoByUtenteDB(cliente);
                            cliente.setCc(cc); 
                        }
                    } catch (SQLException e){
                        System.out.println("Errore recupero cliente in account: "+e);
                        return null;
                    }

                    mappa.put(email, new Account(id, email, password, ruolo, cliente));
                    
                }
                return mappa;
            } catch (SQLException e){
                System.out.println("Errore getPacchettiByFilter:"+e);
                return null;
            } 
    }

    public PortafoglioOre gePortafoglioOreByUtente(int idUtente){
        String query = "SELECT * FROM PortafoglioOre WHERE proprietario = ?;";

        try (PreparedStatement pstmt = conn.prepareStatement(query)){
            pstmt.setInt(1, idUtente);
            ResultSet rs = pstmt.executeQuery();
            PortafoglioOre po = null;
                
            while (rs.next()){
                int id = rs.getInt("id");
                int idUtenteD = rs.getInt("proprietario");
                float ore = rs.getFloat("ore");
                int sconto = rs.getInt("sconto");

                po = new PortafoglioOre(id, idUtenteD, ore, sconto);
            }
            return po;
            
        } catch (SQLException e){
            System.out.println("Errore gePortafoglioOreByUtente: "+e);
            return null;
        }
    }

    public CartaCredito getCartaCreditoByUtenteDB(Cliente cliente){
        String query = "SELECT * FROM CartaCredito WHERE Utente = ?;";

        try (PreparedStatement pstmt = conn.prepareStatement(query)){
            pstmt.setInt(1, cliente.getId());
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

                cc = new CartaCredito(numeroCarta, scadenza, cvv, circuito, idPortafoglio, cliente, conn);
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
                float oreViaggio = rs.getFloat("OreViaggio");
                int visibilità = rs.getInt("Visibilità");
                int idCompagniaTrasporto = rs.getInt("CompagniaTrasporto");
                int idAlloggio = rs.getInt("Alloggio");

                mappa.put(id, new PacchettoViaggio(id, codice, titolo, citta, nazione, dataPartenza, dataRitorno, descrizione, prezzo, oreViaggio, visibilità, idCompagniaTrasporto, idAlloggio, conn));
            }
            return mappa;
        } catch (SQLException e){
            System.out.println("Errore recupero pacchetti: "+e);
            return null;
        }
    }

    private Map<PacchettoViaggio, OffertaSpeciale> recuperaOfferte(){
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
                
                PacchettoViaggio pacchetto = this.elencoPacchetti.get(idPacchetto);
                mappa.put(pacchetto, new OffertaSpeciale(id, pacchetto, percentuale, prezzoScontato, dataFine, disponibilità));
            }
            return mappa;
        } catch (SQLException e){
            System.out.println("Errore recupero offerte: "+e);
            return null;
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

    private List<Viaggiatore> recuperaViaggiatoriByPrenotazione(int idPrenotazione){
        String query = "SELECT * from Viaggiatore WHERE Prenotazione = ?;";
        List<Viaggiatore> lista = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(query)){
            pstmt.setInt(1, idPrenotazione);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()){
                int id = rs.getInt("id");
                String nome = rs.getString("Nome");
                String cognome = rs.getString("Cognome");
                String dataNascita = rs.getString("DataNascita");
                String tipoDocumento = rs.getString("TipoDocumento");
                String CodiceDocumento = rs.getString("CodiceDocumento");
                boolean sediaRotelle = rs.getInt("SediaRotelle") == 1;
                boolean cecita = rs.getInt("Cecità") == 1;

                Viaggiatore viaggiatore = new Viaggiatore(nome, cognome, dataNascita, tipoDocumento, CodiceDocumento);
                viaggiatore.setSediaRotelle(sediaRotelle);
                viaggiatore.setCecita(cecita);
                lista.add(viaggiatore);
            }
            return lista;
        } catch (SQLException e){
            System.out.println("Errore recupero offerte: "+e);
            return null;
        }
    }

    private Map<Integer, Prenotazione> recuperaPrenotazioni(){
        String query =
            "SELECT * FROM Prenotazioni " +
            "ORDER BY substr(DataPrenotazione, 7, 4) || '-' || substr(DataPrenotazione, 4, 2) || '-' || substr(DataPrenotazione, 1, 2) DESC";

        Map<Integer, Prenotazione> mappa = new HashMap<>();

        try (PreparedStatement pstmt = conn.prepareStatement(query)){
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()){
                int id = rs.getInt("id");
                int idCliente = rs.getInt("Utente");
                int idPacchetto = rs.getInt("Pacchetto");
                String dataPrenotazione = rs.getString("DataPrenotazione");
                float prezzoTotale = rs.getFloat("PrezzoTotale");
                float scontoApplicato = rs.getFloat("ScontoApplicato");
                float percentualeOfferta = rs.getFloat("OffertaSpeciale");
                int checkedInInt = rs.getInt("CheckIn");
                float prezzoAssistenzaSpeciale = rs.getFloat("PrezzoAssistenzaSpeciale");
                boolean checkin = checkedInInt == 1;
                
                List<Viaggiatore> elencoViaggiatori = this.recuperaViaggiatoriByPrenotazione(id);
                PacchettoViaggio pacchettoViaggio = elencoPacchetti.get(idPacchetto);
                Cliente cliente = this.getClienteById(idCliente);

               
                Prenotazione newPrenotazione = new Prenotazione(id, cliente, pacchettoViaggio, dataPrenotazione, elencoViaggiatori, prezzoTotale, scontoApplicato, percentualeOfferta, checkin);
                newPrenotazione.setPrezzoAssistenzaSpeciale(prezzoAssistenzaSpeciale);
                mappa.put(id, newPrenotazione);
                cliente.addPrenotazione(newPrenotazione);
                
            }
            return mappa;
        } catch (SQLException e){
            System.out.println("Errore recupero offerte: "+e);
            return null;
        }
    }

    public Map<Integer, Prenotazione> getPrenotazioni(){
        return Collections.unmodifiableMap(this.elencoPrenotazioni);
    }

    public Prenotazione getPrenotazioneById(int idPrenotazione) {
        return this.elencoPrenotazioni.get(idPrenotazione);
    }

    private Map<Integer, Recensione> recuperaRecensioni(){
        String query = "SELECT * FROM Recensione;";
        Map<Integer, Recensione> elencoRecensioni = new HashMap<>();

        try (PreparedStatement pstmt = conn.prepareStatement(query)){
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()){
                int id = rs.getInt("id");
                String riferimento = rs.getString("Riferimento");
                int stelle = rs.getInt("Stelle");
                String commento = rs.getString("Commento");
                int idCliente = rs.getInt("Cliente");
                int idPrenotazione = rs.getInt("Prenotazione");
                String data = rs.getString("DataRecensione");
                Cliente cliente = getClienteById(idCliente);
                Prenotazione prenotazione = elencoPrenotazioni.get(idPrenotazione);

                RecensioneAgenzia rAgenzia;
                RecensioneTrasporto rTrasporto;
                RecensioneAlloggio rAlloggio;

                switch (riferimento){
                    case "Agenzia":
                        rAgenzia = new RecensioneAgenzia(idPrenotazione, stelle, commento, data, cliente, prenotazione);
                        elencoRecensioni.put(id, rAgenzia);
                        cliente.addRecensione(rAgenzia);
                        break;
                    case "Trasporto":
                        rTrasporto = new RecensioneTrasporto(idPrenotazione, stelle, commento, data, cliente, prenotazione);
                        elencoRecensioni.put(id, rTrasporto);
                        cliente.addRecensione(rTrasporto);
                        break;
                    case "Alloggio":
                        rAlloggio = new RecensioneAlloggio(idPrenotazione, stelle, commento, data, cliente, prenotazione);
                        elencoRecensioni.put(id, rAlloggio);
                        cliente.addRecensione(rAlloggio);
                        break;
                }
            }
            return elencoRecensioni;

        } catch (SQLException e){
            System.out.println("Errore recupera recesioni: "+e);
            return null;
        }
    }

    //*RICERCA PACCHETTO
    public List<PacchettoViaggio> ricercaPacchetti(String città, String dataAndata, String dataRitorno, float prezzoMassimo){
           //metodo per uc2
           
            List<PacchettoViaggio> pacchettiTrovati = new ArrayList<>();
            for (PacchettoViaggio p : this.elencoPacchetti.values()){
                String cittaP = p.getCittà();
                String dataAndataP = p.getDataPartenza();
                String dataRitornoP = p.getDataRitorno();
                OffertaSpeciale o = this.getOffertaByPack(p);
                float prezzoVero;
                if (o != null)
                    prezzoVero = o.getPrezzoScontato();
                else
                    prezzoVero = p.getPrezzo();

                if (cittaP.equals(città) && dataAndataP.equals(dataAndata) && dataRitornoP.equals(dataRitorno) && prezzoVero <= prezzoMassimo && p.isVisibilità() == 1)
                    pacchettiTrovati.add(p);
            }
            return pacchettiTrovati;
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

    public String registrazione(String nome, String cognome, String email, String password, String confermaPassword, String telefono){
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

        Account newAccount = new Account(idAccount, email, password, "Cliente", null);

        int idCliente = newAccount.createClient(conn, nome, cognome, telefono);

        if (idCliente == 0) {
            System.out.println("registrazione: creazione cliente fallita");
            return "errore";
        }
        
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

    //*ELIMINAZIONE ACCOUNT
    public boolean eliminaAccount(Connection conn, String email, String password){
        Account a = elencoAccount.get(email);

        if(!a.validazioneCredenziali(email, password))
            return false;

        if (!a.eliminaCliente(conn))
            return false;

        elencoAccount.remove(email);

        String query = "DELETE FROM Account WHERE Email = ? AND Password = ?;";

        try (PreparedStatement pstmt = conn.prepareStatement(query)){
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            elencoAccount.remove(email);
        } catch (SQLException e){
            System.out.println("Errore elimina account: "+e);
        }

        return true;
    }    
    
    //*NUOVA RICARICA
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

    public boolean insertCartaCredito(int idUtente, String numeroCarta, String scadenza, String cvv, String circuito){
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
            Cliente cliente = this.getClienteById(idUtente);
            cliente.setCc(new CartaCredito(numeroCarta, scadenza, cvv, circuito, idPortafoglio, cliente, conn));
            return true;
        } catch (SQLException e){
            System.out.println("Errore insertOnPortafoglio: "+e);
            return false;
        }
    }

    //*INSERIMENTO PACCHETTO
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

        for (PacchettoViaggio p : elencoPacchetti.values()){
            if (p.getCodice().equals(codice))
                return -1;
        }
        

        for (PacchettoViaggio p : elencoPacchetti.values()){
            if (p.getCittà().equals(citta) && p.getNazione().equals(nazione) && p.getDataPartenza().equals(dataPartenza) && p.getDataRitorno().equals(dataRitorno) && p.getCompagniaTrasporto().getId() == idCompagnia && p.getAlloggio().getId() == idAlloggio && p.getPrezzo() == prezzo)
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

    public int nuovoPacchetto(Connection conn, String codice, String titolo, String citta, String nazione, String descrizione, float prezzo, float oreViaggio, int visibilità, String compagnia, String alloggio, String dataPartenza, String dataRitorno){
        String query = "INSERT INTO PacchettiViaggio (Città, Titolo, Nazione, DataPartenza, DataRitorno, Descrizione, Prezzo, Visibilità, CompagniaTrasporto, Alloggio, Codice, OreViaggio) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

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
            return -1;
        }

        int esitoPacchettiDuplicati = this.controllaPacchettiDuplicati(codice, citta, nazione, dataPartenza, dataRitorno, idCompagniaTrasporto, idAlloggio, prezzo);

        if (esitoPacchettiDuplicati == -2){
            return -2; 
        } else if (esitoPacchettiDuplicati == -1){
            return -3;
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
            pstmt.setFloat(12, oreViaggio);

            pstmt.executeUpdate();
            int newId = 0;
            try (Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery("SELECT last_insert_rowid();")) {
                if (rs.next()) {
                     newId = rs.getInt(1);
                }
            }
            PacchettoViaggio p = new PacchettoViaggio(newId, codice, titolo, citta, nazione, dataPartenza, dataRitorno, descrizione, prezzo, oreViaggio, visibilità, idCompagniaTrasporto, idAlloggio, conn);
            elencoPacchetti.put(p.getId(), p);
            return 0;
        } catch (SQLException e){
            System.out.println("Errore nuovo pacchetto: "+e);
            return -4;
        }
    }

    public Map<Integer, PacchettoViaggio> getElencoPacchetti(){
        return Collections.unmodifiableMap(this.elencoPacchetti);
    }

    public OffertaSpeciale getOffertaByPack(PacchettoViaggio p){
        OffertaSpeciale o =  this.elencoOfferte.get(p);
        if (o != null)
            return o;
        else return null;
    }

    public OffertaSpeciale getOffertaByPackOperatore(PacchettoViaggio p){
        return this.elencoOfferte.get(p);
    }

    
    //*ELIMINAZIONE OFFERTE SCADUTE O ESAURITE
    public boolean eliminaOfferteDB(OffertaSpeciale o){
        String query = "DELETE FROM OffertaSpeciale WHERE id = ?;";

        //String query = "UPDATE OffertaSpeciale SET Visibilità = ? WHERE id = ?;";
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

            if (fine.isBefore(LocalDate.now()) || o.getDisponibilità() == 0) {
                if (!eliminaOfferteDB(o))
                    return false;
            }
        }
        return true;
    }

    private boolean rimuoviOffertaEsaurita(OffertaSpeciale offerta) {
        if (offerta == null) {
            return true;
        }

        if (!eliminaOfferteDB(offerta)) {
            return false;
        }

        PacchettoViaggio pacchettoRimosso = null;
        for (PacchettoViaggio p : elencoOfferte.keySet()) {
            if (p.getId() == offerta.getPacchetto().getId()) {
                pacchettoRimosso = p;
                break;
            }
        }

        if (pacchettoRimosso != null) {
            elencoOfferte.remove(pacchettoRimosso);
            notifyOffertaEliminata(pacchettoRimosso);
        } else {
            notifyOffertaEliminata(offerta.getPacchetto());
        }

        return true;
    }

    //*OBSERVER
    public void addOffertaObserver(OffertaObserver observer) {
        if (observer != null && !offertaObservers.contains(observer)) offertaObservers.add(observer);
    }

    public void removeOffertaObserver(OffertaObserver observer) {
        offertaObservers.remove(observer);
    }

    private void notifyOffertaCreata(OffertaSpeciale offerta) {
        for (OffertaObserver observer : new ArrayList<>(offertaObservers)) {
            observer.onOffertaCreata(offerta);
        }
    }

    private void notifyOffertaEliminata(PacchettoViaggio pacchetto) {
        for (OffertaObserver observer : new ArrayList<>(offertaObservers)) {
            observer.onOffertaEliminata(pacchetto);
        }
    }

    public void addRicaricaObserver(RicaricaObserver observer) {
        if (observer != null && !ricaricaObservers.contains(observer)) ricaricaObservers.add(observer);
    }

    public void removeRicaricaObserver(RicaricaObserver observer) {
        ricaricaObservers.remove(observer);
    }

    public void ricaricaEffettuata(int idUtente) {
        PortafoglioVirtuale pv = getPortafoglioByClienteDB(idUtente);
        double saldo = 0.0;
        if (pv != null) {
            saldo = pv.getSaldo();
        }
        notifyRicaricaEffettuata(idUtente, saldo);
    }

    private void notifyRicaricaEffettuata(int idUtente, double nuovoSaldo) {
        for (RicaricaObserver observer : new ArrayList<>(ricaricaObservers)) {
            observer.onRicaricaEffettuata(idUtente, nuovoSaldo);
        }
    }

    public void addPrenotazioneObserver(PrenotazioneObserver observer) {
        if (observer != null && !prenotazioneObservers.contains(observer)) prenotazioneObservers.add(observer);
    }

    public void removePrenotazioneObserver(PrenotazioneObserver observer) {
        prenotazioneObservers.remove(observer);
    }

    private void notifyPrenotazioneModificata(Prenotazione prenotazione) {
        for (PrenotazioneObserver observer : new ArrayList<>(prenotazioneObservers)) {
            observer.onPrenotazioneModificata(prenotazione);
        }
    }

    public void addRecensioneObserver(RecensioneObserver observer) {
        if (observer != null && !recensioneObservers.contains(observer)) recensioneObservers.add(observer);
    }

    public void removeRecensioneObserver(RecensioneObserver observer) {
        recensioneObservers.remove(observer);
    }

    private void notifyRecensioneCreata(Recensione recensione) {
        for (RecensioneObserver observer : new ArrayList<>(recensioneObservers)) {
            observer.onRecensioneCreata(recensione);
        }
    }

    

    //*PRENOTAZIONE

    //!RIVEDI
    public float getTotalePrenotazione(Cliente cliente, PacchettoViaggio pacchetto, List<Viaggiatore> elencoViaggiatori){
        OffertaSpeciale o = elencoOfferte.get(pacchetto);
        float prezzoVero = 0.0F;
        if (o != null)
            prezzoVero = o.getPrezzoScontato();
        else
            prezzoVero = pacchetto.getPrezzo();

        float sconto = 0.0F;
        if (cliente != null && cliente.getPo() != null) {
            sconto = cliente.getPo().getSconto();
        }
        float totaleSenzaSconto = prezzoVero*elencoViaggiatori.size();
        

        return totaleSenzaSconto - totaleSenzaSconto*sconto/100;
    }

    




    //!RIVEDI
    public boolean registrazionePrenotazione(int idPrenotazione, float scontoApplicato, float totaleAggiornato, float offertaApplicata){
        return registrazionePrenotazione(idPrenotazione, scontoApplicato, totaleAggiornato, offertaApplicata, 0.0F);
    }

    //!RIVEDI
    public boolean registrazionePrenotazione(int idPrenotazione, float scontoApplicato, float totaleAggiornato, float offertaApplicata, float prezzoAssistenzaSpeciale){
        String query = "UPDATE Prenotazioni SET DataPrenotazione = ?, PrezzoTotale = ?, ScontoApplicato= ?, OffertaSpeciale = ?, PrezzoAssistenzaSpeciale = ?, CheckIn = ? WHERE id = ?;";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {

            
            String dataPrenotazione = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-uuuu"));
            pstmt.setString(1, dataPrenotazione);
            pstmt.setFloat(2, totaleAggiornato);
            pstmt.setFloat(3, scontoApplicato);
            pstmt.setFloat(4, offertaApplicata);
            pstmt.setFloat(5, prezzoAssistenzaSpeciale);
            pstmt.setInt(6, 0);
            pstmt.setInt(7, idPrenotazione);
            pstmt.executeUpdate();

            Prenotazione p = this.elencoPrenotazioni.get(idPrenotazione);

            p.setDataPrenotazione(dataPrenotazione);
            p.setPrezzoTotale(totaleAggiornato);
            p.setScontoApplicato(scontoApplicato);
            p.setOffertaApplicata(offertaApplicata);
            p.setPrezzoAssistenzaSpeciale(prezzoAssistenzaSpeciale);

            if (!p.applicaSconto(conn, scontoApplicato))
                return false;
            if (!p.aggiornaOreViaggio(conn))
                return false;
            OffertaSpeciale o = this.elencoOfferte.get(p.getPacchetto());
            if (o != null){
                if(!o.diminuisciDisponibilità(conn))
                    return false;
                if (o.getDisponibilità() <= 0) {
                    if (!rimuoviOffertaEsaurita(o))
                        return false;
                }
            }

            
            return true;
        } catch (SQLException e){
            System.out.println("Errore registrazionePrenotazione: "+e);
            return false;
        }
    }

    //!RIVEDI
    public void inserisciDatiViaggiatore(List<Viaggiatore> elencoViaggiatori, Viaggiatore v){
        elencoViaggiatori.add(v);
    }

    //*MODIFICA PACCHETTI
    public boolean modificaPacchettoPrenotazione(Prenotazione prenotazione, PacchettoViaggio nuovoPacchetto) {
        if (prenotazione == null || nuovoPacchetto == null) {
            return false;
        }

        int idPrenotazione = prenotazione.getId();
        if (idPrenotazione <= 0) {
            return false;
        }

        float nuovaOffertaApplicata = 0.0F;
        OffertaSpeciale o = this.elencoOfferte.get(nuovoPacchetto);
        if (o != null) {
            nuovaOffertaApplicata = o.getScontoPercentuale();
        }

        float nuovoTotale = this.getTotalePrenotazione(prenotazione.getCliente(), nuovoPacchetto, prenotazione.getElencoViaggiatori());

        String query = "UPDATE Prenotazioni SET Pacchetto = ?, PrezzoTotale = ?, OffertaSpeciale = ? WHERE id = ?;";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, nuovoPacchetto.getId());
            pstmt.setFloat(2, nuovoTotale);
            pstmt.setFloat(3, nuovaOffertaApplicata);
            pstmt.setInt(4, idPrenotazione);

            int updatedRows = pstmt.executeUpdate();
            if (updatedRows != 1) {
                return false;
            }

            prenotazione.setPacchetto(nuovoPacchetto);
            prenotazione.setPrezzoTotale(nuovoTotale);
            prenotazione.setOffertaApplicata(nuovaOffertaApplicata);
            notifyPrenotazioneModificata(prenotazione);
            return true;
        } catch (SQLException e) {
            System.out.println("Errore modificaPacchettoPrenotazione: " + e);
            return false;
        }
    }

    //*RECENSIONI
    //!RIVEDI
    public boolean validaDatiNuovaRecensione(String commentoAgenzia, String commentoTrasporto, String commentoAlloggio){
        return !(commentoAgenzia.isBlank() || commentoTrasporto.isBlank() || commentoAlloggio.isBlank());
    }

    public boolean inserisciRecensione(Cliente cliente, Prenotazione prenotazione, String commento, int stelle, String riferimento){
        String query = "INSERT INTO Recensione(Riferimento, Stelle, Commento, Cliente, Prenotazione, DataRecensione) values (?, ?, ?, ?, ?, ?);";
        
        try(PreparedStatement pstmt = conn.prepareStatement(query)){
            pstmt.setString(1, riferimento);
            pstmt.setInt(2, stelle);
            pstmt.setString(3, commento);
            pstmt.setInt(4, cliente.getId());
            pstmt.setInt(5, prenotazione.getId());
            String dataRecensione = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-uuuu"));
            pstmt.setString(6, dataRecensione);


            pstmt.executeUpdate();

            int newId = 0;
            try (Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery("SELECT last_insert_rowid();")) {
                if (rs.next()) {
                     newId = rs.getInt(1);
                }
            }

            RecensioneAgenzia rAgenzia;
            RecensioneTrasporto rTrasporto;
            RecensioneAlloggio rAlloggio;
            switch (riferimento){
                case "Agenzia":
                    rAgenzia = new RecensioneAgenzia(newId, stelle, commento, dataRecensione, cliente, prenotazione);
                    this.elencoRecensioni.put(newId, rAgenzia);
                    cliente.addRecensione(rAgenzia); 
                    notifyRecensioneCreata(rAgenzia);
                    break;
                case "Alloggio":
                    rAlloggio = new RecensioneAlloggio(newId, stelle, commento, dataRecensione, cliente, prenotazione);
                    this.elencoRecensioni.put(newId, rAlloggio);
                    cliente.addRecensione(rAlloggio); 
                    notifyRecensioneCreata(rAlloggio);
                    break;
                case "Trasporto":
                    rTrasporto = new RecensioneTrasporto(newId, stelle, commento, dataRecensione, cliente, prenotazione);
                    this.elencoRecensioni.put(newId, rTrasporto);
                    cliente.addRecensione(rTrasporto); 
                    notifyRecensioneCreata(rTrasporto);
                    break;
            }
            return true;
        } catch (SQLException e){
            System.out.println("Errore inserimento nuova recensione: "+e);
            return false;
        }
    }

    public Map<Integer, Recensione> getRecensioni(){
        return Collections.unmodifiableMap(this.elencoRecensioni);
    }

    public float getMediaRecensioni(String riferimento){
        int somma = 0;
        int nRecensioni = 0;
        for (Recensione r: elencoRecensioni.values()){

            int stelle = r.getStelle();
            switch (riferimento) {
                case "Agenzia":
                    if (r instanceof RecensioneAgenzia){
                        somma += stelle;
                        nRecensioni++;
                    }
                    break;
                case "Trasporto":
                    if (r instanceof RecensioneTrasporto){
                        somma += stelle;
                        nRecensioni++;
                    }
                    break;
                case "Alloggio":
                    if (r instanceof RecensioneAlloggio){
                        somma += stelle;
                        nRecensioni++;
                    }
                    break;
            }
        }
        return (float)somma/nRecensioni;
    }

    public int getNTotaleRecensioni(){
        return this.elencoRecensioni.size()/3;
    }
    
    //*ASSISTENZA SPECIALE
    @Override public void onAssistenzaChanged(Prenotazione prenotazione, Viaggiatore viaggiatore, String tipoAssistenza, boolean valore) { 
        prenotazione.aggiornaAssistenza(viaggiatore, tipoAssistenza, valore); 
    }

    public void confermaAssistenzaSpeciale(Prenotazione prenotazione){
        prenotazione.calcolaPrezzoAssistenzaSpeciale();
    }

    //*ELIMINAZIONE PRENOTAZIONE
    private float calcolaRimborsoEliminazione(Prenotazione prenotazione) {
        if (prenotazione == null || prenotazione.getPacchetto() == null) {
            return -1.0F;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-uuuu");
        LocalDate dataPartenza = LocalDate.parse(prenotazione.getPacchetto().getDataPartenza(), formatter);
        LocalDate oggi = LocalDate.now();
        long giorniAllaPartenza = java.time.temporal.ChronoUnit.DAYS.between(oggi, dataPartenza);

        if (giorniAllaPartenza < 2) {
            return -1.0F;
        }

        float prezzoPrenotazione = prenotazione.getPrezzoTotale();
        if (giorniAllaPartenza >= 7) {
            return prezzoPrenotazione;
        }
        return prezzoPrenotazione * 0.5F;
    }

    public float getRimborsoEliminazionePrenotazione(Prenotazione prenotazione) {
        return calcolaRimborsoEliminazione(prenotazione);
    }

    private boolean eliminaPrenotazioneDB(int idPrenotazione) {
        String deleteViaggiatori = "DELETE FROM Viaggiatore WHERE Prenotazione = ?;";
        String deletePrenotazione = "DELETE FROM Prenotazioni WHERE id = ?;";

        try (PreparedStatement pstmtViaggiatori = conn.prepareStatement(deleteViaggiatori);
             PreparedStatement pstmtPrenotazione = conn.prepareStatement(deletePrenotazione)) {

            pstmtViaggiatori.setInt(1, idPrenotazione);
            pstmtViaggiatori.executeUpdate();

            pstmtPrenotazione.setInt(1, idPrenotazione);
            int rows = pstmtPrenotazione.executeUpdate();
            return rows == 1;
        } catch (SQLException e) {
            System.out.println("Errore eliminaPrenotazioneDB: " + e);
            return false;
        }
    }

    public int eliminaPrenotazione(Prenotazione prenotazione, float rimborso) {
        if (prenotazione == null || prenotazione.getCliente() == null) {
            return -2;
        }

        //float rimborso = calcolaRimborsoEliminazione(prenotazione);
        if (rimborso < 0.0F) {
            return -3;
        }

        Cliente cliente = prenotazione.getCliente();
        if (!cliente.rimborsoOnPortafoglioDB(conn, rimborso)) {
            return -1;
        }

        if(!cliente.levaOreViaggio(conn, prenotazione.getPacchetto().getOreViaggio()))
            return -1;

        if (!eliminaPrenotazioneDB(prenotazione.getId())) {
            cliente.pagamentoOnPortafoglioDB(conn, rimborso);
            return -2;
        }

        elencoPrenotazioni.remove(prenotazione.getId());
        cliente.removePrenotazione(prenotazione.getId());
        notifyPrenotazioneModificata(prenotazione);
        return 0;
    }

    //*CHECK-IN
    public boolean effettuaCheckIn(Prenotazione p){
        if (p == null) {
            return false;
        }

        if(p.isCheckedIn()) {
            System.out.println("Check-in già effettuato.");
            return false;
        }

        return p.checkIn(conn);
    }

    //!RIVEDI
    public boolean modificaViaggiatori(Prenotazione p, String nome, String cognome, String dataNascita, String tipoDocumento, String codiceDocumento, boolean cecita, boolean sediaRotelle, int index){
        

        p.replaceViaggiatori(conn, nome, cognome, dataNascita, tipoDocumento, codiceDocumento, cecita, sediaRotelle, index);
        if (true) {
            notifyPrenotazioneModificata(p);
            return true;
        }
        return false;
    }

    public boolean assistenzaSpecialeModificata(float nuovoPrezzo, float vecchioPrezzo) {
        return nuovoPrezzo != vecchioPrezzo;
    }

    //*METODI VARI
    public void aggiornaOfferte(OffertaSpeciale o){
        this.elencoOfferte.put(o.getPacchetto(), o);
        notifyOffertaCreata(o);
    }

    public CompagniaTrasporto getCompagniaTrasportoByPacchetto(int idCompagnia){
        return this.elencoCompagnie.get(idCompagnia);   
    }

    public Alloggio getAlloggioByPacchetto(int idAlloggio){
        return this.elencoAlloggi.get(idAlloggio);
    }

    public Cliente getClienteById(int idCliente){
        for (Account a: elencoAccount.values()){
            Cliente c = a.getCliente();
            if (c.getId() == idCliente)
                return c;
        }
        return null;
    }

    public Account getAccountToHomeView(String email){
        return this.elencoAccount.get(email);
    }

    public int validazioneViaggiatore(String nome, String cognome, String dataNascita, String tipoDocumento, String codiceDocumento){

            if (nome.isBlank() || cognome.isBlank() || dataNascita.isBlank() ||tipoDocumento.isBlank() || codiceDocumento.isBlank())
                return -1;
            
            DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);

            LocalDate d = LocalDate.parse(dataNascita, FMT);

            if (d.isAfter(LocalDate.now()))
                return -2;

            if (tipoDocumento.equals("Carta d'identità") && codiceDocumento.length() != 9){
                return -3;
            } else if (tipoDocumento.equals("Patente di guida") && codiceDocumento.length() != 10){
                return -4;
            } else if (tipoDocumento.equals("Passaporto") && codiceDocumento.length() != 9){
                return -5;
            } 
        return 0;
    }

    public int createPrenotazione(PacchettoViaggio pacchetto, int idUtente){
        String query = "INSERT INTO Prenotazioni (Utente, Pacchetto, DataPrenotazione, PrezzoTotale, ScontoApplicato, OffertaSpeciale, PrezzoAssistenzaSpeciale, CheckIn) values (?, ?, ?, ?, ?, ?, ?, ?);";

        try (PreparedStatement pstmt = conn.prepareStatement(query)){
            pstmt.setInt(1, idUtente);
            pstmt.setInt(2, pacchetto.getId());
            pstmt.setString(3, "");
            pstmt.setFloat(4, 0.0F);
            pstmt.setFloat(5, 0.0F);
            pstmt.setFloat(6, 0.0F);
            pstmt.setFloat(7, 0.0F);
            pstmt.setInt(8, 0);
            pstmt.executeUpdate();

            int newId = 0;
            query = "SELECT last_insert_rowid();";

            try (PreparedStatement pstmt2 = conn.prepareStatement(query)) {
                ResultSet rs = pstmt2.executeQuery();
                if (rs.next()) {
                    newId = rs.getInt(1);
                }
            }

            Cliente c = getClienteById(idUtente);
            Prenotazione p = new Prenotazione(newId, c, pacchetto, "", 0.0F, 0.0F, 0.0F, false);

            elencoPrenotazioni.put(newId, p);
            if (c != null) {
                c.addPrenotazione(p);
            }
            return newId;
        } catch (SQLException e){
            System.out.println("Errore createPrenotazione: "+e);
            return 0;
        }
    }

    public boolean createViaggiatore(int idPrenotazione, String nome, String cognome, String dataNascita, String tipoDocumento, String codiceDocumento){
        Prenotazione p = this.elencoPrenotazioni.get(idPrenotazione);
        if (p == null) {
            return false;
        }

        return p.createViaggiatore(conn, nome, cognome, dataNascita, tipoDocumento, codiceDocumento);
    }

    public boolean annullaPrenotazioneBozza(int idPrenotazione) {
        Prenotazione p = this.elencoPrenotazioni.get(idPrenotazione);
        if (p == null) {
            return false;
        }

        if (!p.eliminaBozzaDaDB(conn)) {
            return false;
        }

        elencoPrenotazioni.remove(idPrenotazione);
        Cliente cliente = p.getCliente();
        if (cliente != null) {
            cliente.removePrenotazione(idPrenotazione);
        }
        notifyPrenotazioneModificata(p);
        return true;
    }

    public List<Viaggiatore> getViaggiatoriByPrenotazione(int idPrenotazione){
        Prenotazione p = this.elencoPrenotazioni.get(idPrenotazione);
        if (p == null) {
            return null;
        }

        return p.getElencoViaggiatori();
    }
}

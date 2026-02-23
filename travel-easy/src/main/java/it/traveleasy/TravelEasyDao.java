
package it.traveleasy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.*;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import it.traveleasy.TravelEasyDao.JdbcTravelEasyDao;

public interface TravelEasyDao {
    TravelEasyDao INSTANCE = new JdbcTravelEasyDao();

    Map<String, Account> recuperaAccount(Connection conn);
    Cliente recuperaClienteByAccountId(Connection conn, int accountId);
    PortafoglioVirtuale getPortafoglioByClienteDB(Connection conn, int idCliente);
    PortafoglioOre getPortafoglioOreByUtente(Connection conn, int idUtente);
    CartaCredito getCartaCreditoByUtenteDB(Connection conn, Cliente cliente);

    Map<Integer, CompagniaTrasporto> recuperaCompagnie(Connection conn);
    Map<Integer, Alloggio> recuperaAlloggi(Connection conn);
    Map<Integer, PacchettoViaggio> recuperaPacchetti(Connection conn);
    Map<PacchettoViaggio, OffertaSpeciale> recuperaOfferte(Connection conn, Map<Integer, PacchettoViaggio> pacchetti);
    
    List<Viaggiatore> recuperaViaggiatoriByPrenotazione(Connection conn, int idPrenotazione);
    Map<Integer, Prenotazione> recuperaPrenotazioni(Connection conn, Map<Integer, PacchettoViaggio> elencoPacchetti, Function<Integer, Cliente> clienteById, Function<Integer, List<Viaggiatore>> viaggiatoriByPrenotazione);

    Map<Integer, Recensione> recuperaRecensioni(Connection conn, Function<Integer, Cliente> clienteById, Map<Integer, Prenotazione> elencoPrenotazioni);
    
    int recuperaIdAccount(Connection conn, String email);
    boolean createAccount(Connection conn, String email, String password, String ruolo);
    boolean eliminaAccount(Connection conn, String email, String password);

    PacchettoViaggio createPacchettoViaggio(Connection conn, String codice, String titolo, String citta, String nazione, String descrizione, float prezzo, float oreViaggio, int visibilità, String dataPartenza, String dataRitorno, int idCompagniaTrasporto, int idAlloggio);

    boolean eliminaOfferta(Connection conn, OffertaSpeciale o);
    OffertaSpeciale createNuovaOfferta(Connection conn, PacchettoViaggio pacchetto, float percentuale, float prezzoScontato, String dataFine, int disponibilità);


    Prenotazione createPrenotazione(Connection conn, int idUtente, PacchettoViaggio pacchetto, Function<Integer, Cliente> clienteById);
    boolean updatePrenotazione(Connection conn, String dataPrenotazione, float totaleAggiornato, float scontoApplicato, float offertaApplicata, float prezzoAssistenzaSpeciale, int idPrenotazione);
    
    boolean updatePacchettoPrenotazione(Connection conn, int idPacchetto, float nuovoTotale, float nuovaOffertaApplicata, int idPrenotazione);

    Recensione createRecensione(Connection conn, String riferimento, int stelle, String commento, Cliente cliente, Prenotazione prenotazione);
    
    boolean eliminaPrenotazioneDB(Connection conn, int idPrenotazione);

    class JdbcTravelEasyDao implements TravelEasyDao{
        
        @Override
        public Map<String, Account> recuperaAccount(Connection conn) {
            Map<String, Account> mappa = new HashMap<>();
            String query = "SELECT * FROM ACCOUNT";

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String email = rs.getString("Email");
                    String password = rs.getString("Password");
                    String ruolo = rs.getString("Ruolo");

                    mappa.put(email, new Account(id, email, password, ruolo, null));
                }
                return mappa;
            } catch (SQLException e) {
                System.out.println("Errore getPacchettiByFilter:" + e);
                return null;
            }
        }

        @Override
        public Cliente recuperaClienteByAccountId(Connection conn, int accountId) {
            String queryCliente = "SELECT * FROM Utenti WHERE Account = ?;";
            try (PreparedStatement pstmtCliente = conn.prepareStatement(queryCliente)) {
                pstmtCliente.setInt(1, accountId);
                ResultSet rsCliente = pstmtCliente.executeQuery();
                if (rsCliente.next()) {
                    int idCliente = rsCliente.getInt("id");
                    String nome = rsCliente.getString("Nome");
                    String cognome = rsCliente.getString("Cognome");
                    String telefono = rsCliente.getString("Telefono");
                    String ruoloCliente = rsCliente.getString("Ruolo");
                    PortafoglioVirtuale pv = getPortafoglioByClienteDB(conn, idCliente);
                    PortafoglioOre po = this.getPortafoglioOreByUtente(conn, idCliente);
                    Cliente cliente = new Cliente(idCliente, nome, cognome, telefono, ruoloCliente, accountId, pv, null, po);
                    CartaCredito cc = this.getCartaCreditoByUtenteDB(conn, cliente);
                    cliente.setCc(cc);
                    return cliente;
                }
                return null;
            } catch (SQLException e) {
                System.out.println("Errore recupero cliente in account: " + e);
                return null;
            }
        }

        
        @Override
        public PortafoglioOre getPortafoglioOreByUtente(Connection conn, int idUtente){
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

        @Override
        public PortafoglioVirtuale getPortafoglioByClienteDB(Connection conn, int idCliente){
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

        @Override
        public CartaCredito getCartaCreditoByUtenteDB(Connection conn, Cliente cliente){
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

                    cc = new CartaCredito(numeroCarta, scadenza, cvv, circuito, idPortafoglio, cliente);
                }
                return cc;
                
            } catch (SQLException e){
                System.out.println("Errore getCartaCreditoByUtente: "+e);
                return null;
            }
        }

        @Override
        public Map<Integer, CompagniaTrasporto> recuperaCompagnie(Connection conn) {
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

        @Override
        public Map<Integer, Alloggio> recuperaAlloggi(Connection conn){
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

        @Override
        public Map<Integer, PacchettoViaggio> recuperaPacchetti(Connection conn){
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

        @Override
        public Map<PacchettoViaggio, OffertaSpeciale> recuperaOfferte(Connection conn, Map<Integer, PacchettoViaggio> pacchetti){
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
                    
                    PacchettoViaggio pacchetto = pacchetti.get(idPacchetto);
                    mappa.put(pacchetto, new OffertaSpeciale(id, pacchetto, percentuale, prezzoScontato, dataFine, disponibilità));
                }
                return mappa;
            } catch (SQLException e){
                System.out.println("Errore recupero offerte: "+e);
                return null;
            }
        }

        @Override
        public List<Viaggiatore> recuperaViaggiatoriByPrenotazione(Connection conn, int idPrenotazione){
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

        @Override
        public Map<Integer, Prenotazione> recuperaPrenotazioni(Connection conn, Map<Integer, PacchettoViaggio> elencoPacchetti, Function<Integer, Cliente> clienteById, Function<Integer, List<Viaggiatore>> viaggiatoriByPrenotazione){
            String query =
                "SELECT * FROM Prenotazioni " +
                "ORDER BY substr(DataPrenotazione, 7, 4) || '-' || substr(DataPrenotazione, 4, 2) || '-' || substr(DataPrenotazione, 1, 2) DESC";

            Map<Integer, Prenotazione> mappa = new HashMap<>();

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
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

                    List<Viaggiatore> elencoViaggiatori = viaggiatoriByPrenotazione.apply(id);
                    PacchettoViaggio pacchettoViaggio = elencoPacchetti.get(idPacchetto);
                    Cliente cliente = clienteById.apply(idCliente);

                    Prenotazione p = new Prenotazione(
                        id, cliente, pacchettoViaggio, dataPrenotazione,
                        elencoViaggiatori, prezzoTotale, scontoApplicato, percentualeOfferta, checkin
                    );
                    p.setPrezzoAssistenzaSpeciale(prezzoAssistenzaSpeciale);
                    mappa.put(id, p);
                }
                return mappa;
            } catch (SQLException e) {
                System.out.println("Errore recupero offerte: " + e);
                return null;
            }
        }

        @Override
        public Map<Integer, Recensione> recuperaRecensioni(Connection conn, Function<Integer, Cliente> clienteById, Map<Integer, Prenotazione> elencoPrenotazioni){
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
                    Cliente cliente = clienteById.apply(idCliente);
                    Prenotazione prenotazione = elencoPrenotazioni.get(idPrenotazione);

                    RecensioneAgenzia rAgenzia;
                    RecensioneTrasporto rTrasporto;
                    RecensioneAlloggio rAlloggio;

                    switch (riferimento){
                        case "Agenzia":
                            rAgenzia = new RecensioneAgenzia(idPrenotazione, stelle, commento, data, cliente, prenotazione);
                            elencoRecensioni.put(id, rAgenzia);
                            //cliente.addRecensione(rAgenzia);
                            break;
                        case "Trasporto":
                            rTrasporto = new RecensioneTrasporto(idPrenotazione, stelle, commento, data, cliente, prenotazione);
                            elencoRecensioni.put(id, rTrasporto);
                            //cliente.addRecensione(rTrasporto);
                            break;
                        case "Alloggio":
                            rAlloggio = new RecensioneAlloggio(idPrenotazione, stelle, commento, data, cliente, prenotazione);
                            elencoRecensioni.put(id, rAlloggio);
                            //cliente.addRecensione(rAlloggio);
                            break;
                    }
                }
                return elencoRecensioni;

            } catch (SQLException e){
                System.out.println("Errore recupera recesioni: "+e);
                return null;
            }
        }

        @Override
        public int recuperaIdAccount(Connection conn, String email){
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

        @Override
        public boolean createAccount(Connection conn, String email, String password, String ruolo) {
            String query = "INSERT INTO Account (Email, Password, Ruolo) values (?, ?, ?);";

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, email);
                pstmt.setString(2, password);
                pstmt.setString(3, ruolo);
                pstmt.executeUpdate();
                return true;
            } catch (SQLException e) {
                System.out.println("Errore registrazioneInAccount: " + e);
                return false;
            }
        }

        @Override
        public boolean eliminaAccount(Connection conn, String email, String password){
            String query = "DELETE FROM Account WHERE Email = ? AND Password = ?;";

            try (PreparedStatement pstmt = conn.prepareStatement(query)){
                pstmt.setString(1, email);
                pstmt.setString(2, password);
                pstmt.executeUpdate();

                return true;
            } catch (SQLException e){
                System.out.println("Errore elimina account: "+e);
                return false;
            }
        }

        @Override
        public PacchettoViaggio createPacchettoViaggio(Connection conn, String codice, String titolo, String citta, String nazione, String descrizione, float prezzo, float oreViaggio, int visibilità, String dataPartenza, String dataRitorno, int idCompagniaTrasporto, int idAlloggio){
            String query = "INSERT INTO PacchettiViaggio (Città, Titolo, Nazione, DataPartenza, DataRitorno, Descrizione, Prezzo, Visibilità, CompagniaTrasporto, Alloggio, Codice, OreViaggio) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
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
                return new PacchettoViaggio(newId, codice, titolo, citta, nazione, dataPartenza, dataRitorno, descrizione, prezzo, oreViaggio, visibilità, idCompagniaTrasporto, idAlloggio, conn);
            } catch (SQLException e){
                System.out.println("Errore nuovo pacchetto: "+e);
                return null;
            }
        }

        @Override
        public boolean eliminaOfferta(Connection conn, OffertaSpeciale o){
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

        @Override
        public OffertaSpeciale createNuovaOfferta(Connection conn, PacchettoViaggio pacchetto, float percentuale, float prezzoScontato, String dataFine, int disponibilità){
            String query = "INSERT INTO OffertaSpeciale (Pacchetto, ScontoPercentuale, PrezzoScontato, DataFine, Disponibilità) values (?, ?, ?, ?, ?);";
           
            try (PreparedStatement pstmt = conn.prepareStatement(query)){
                pstmt.setInt(1, pacchetto.getId());
                pstmt.setFloat(2, percentuale);
                pstmt.setFloat(3, prezzoScontato);
                pstmt.setString(4, dataFine);
                pstmt.setInt(5, disponibilità);
                
                

                pstmt.executeUpdate();

                int newId = 0;
                try (Statement st = conn.createStatement();
                    ResultSet rs = st.executeQuery("SELECT last_insert_rowid();")) {
                    if (rs.next()) {
                        newId = rs.getInt(1);
                    }
                }

                return new OffertaSpeciale(newId, pacchetto, percentuale, prezzoScontato, dataFine, disponibilità);
                
            } catch (SQLException e){
                System.out.println("Errore nuovaOfferta: "+e);
                return null;
            }
        }

        @Override
        public Prenotazione createPrenotazione(Connection conn, int idUtente, PacchettoViaggio pacchetto, Function<Integer, Cliente> clienteById){
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

                Cliente c = clienteById.apply(idUtente);
                
                
                return new Prenotazione(newId, c, pacchetto, "", 0.0F, 0.0F, 0.0F, false);
            } catch (SQLException e){
                System.out.println("Errore createPrenotazione: "+e);
                return null;
            }
        }

        @Override
        public boolean updatePrenotazione(Connection conn, String dataPrenotazione, float totaleAggiornato, float scontoApplicato, float offertaApplicata, float prezzoAssistenzaSpeciale, int idPrenotazione){
            String query = "UPDATE Prenotazioni SET DataPrenotazione = ?, PrezzoTotale = ?, ScontoApplicato= ?, OffertaSpeciale = ?, PrezzoAssistenzaSpeciale = ?, CheckIn = ? WHERE id = ?;";

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {

                
                
                pstmt.setString(1, dataPrenotazione);
                pstmt.setFloat(2, totaleAggiornato);
                pstmt.setFloat(3, scontoApplicato);
                pstmt.setFloat(4, offertaApplicata);
                pstmt.setFloat(5, prezzoAssistenzaSpeciale);
                pstmt.setInt(6, 0);
                pstmt.setInt(7, idPrenotazione);
                pstmt.executeUpdate();

                return true;
            } catch (SQLException e){
                System.out.println("Errore registrazionePrenotazione: "+e);
                return false;
            }
        }

        @Override
        public boolean updatePacchettoPrenotazione(Connection conn, int idPacchetto, float nuovoTotale, float nuovaOffertaApplicata, int idPrenotazione){
            String query = "UPDATE Prenotazioni SET Pacchetto = ?, PrezzoTotale = ?, OffertaSpeciale = ? WHERE id = ?;";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, idPacchetto);
                pstmt.setFloat(2, nuovoTotale);
                pstmt.setFloat(3, nuovaOffertaApplicata);
                pstmt.setInt(4, idPrenotazione);

                int updatedRows = pstmt.executeUpdate();
                if (updatedRows != 1) {
                    return false;
                }

                return true;
            } catch (SQLException e) {
                System.out.println("Errore modificaPacchettoPrenotazione: " + e);
                return false;
            }
        }

        @Override
        public Recensione createRecensione(Connection conn, String riferimento, int stelle, String commento, Cliente cliente, Prenotazione prenotazione){
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
                switch (riferimento) {
                    case "Agenzia":
                        return new RecensioneAgenzia(newId, stelle, commento, dataRecensione, cliente, prenotazione);
                    case "Alloggio":
                        return new RecensioneAlloggio(newId, stelle, commento, dataRecensione, cliente, prenotazione);
                    case "Trasporto":
                        return new RecensioneTrasporto(newId, stelle, commento, dataRecensione, cliente, prenotazione);
                    default:
                        return null;
                }
                
            } catch (SQLException e){
                System.out.println("Errore inserimento nuova recensione: "+e);
                return null;
            }
        }

        @Override
        public boolean eliminaPrenotazioneDB(Connection conn, int idPrenotazione){
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
    }
}


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
        Map<String, Account> mappa = TravelEasyDao.INSTANCE.recuperaAccount(conn);
        if (mappa == null) {
            return null;
        }

        for (Account account : mappa.values()) {
            Cliente cliente = TravelEasyDao.INSTANCE.recuperaClienteByAccountId(conn, account.getId());
            account.setUtente(cliente);
        }
        return mappa;
    }


    public Map<Integer, CompagniaTrasporto> recuperaCompagnie() {
        return TravelEasyDao.INSTANCE.recuperaCompagnie(conn);
    }

    public Map<Integer, Alloggio> recuperaAlloggi() {
        return TravelEasyDao.INSTANCE.recuperaAlloggi(conn);
    }

    private Map<Integer, PacchettoViaggio> recuperaPacchetti(){
        return TravelEasyDao.INSTANCE.recuperaPacchetti(conn);
    }

    private Map<PacchettoViaggio, OffertaSpeciale> recuperaOfferte(){
        return TravelEasyDao.INSTANCE.recuperaOfferte(conn, elencoPacchetti);
    }

    private List<Viaggiatore> recuperaViaggiatoriByPrenotazione(int idPrenotazione){
        return TravelEasyDao.INSTANCE.recuperaViaggiatoriByPrenotazione(conn, idPrenotazione);
    }

    private Map<Integer, Prenotazione> recuperaPrenotazioni(){
        Map<Integer, Prenotazione> mappa = TravelEasyDao.INSTANCE.recuperaPrenotazioni(conn, elencoPacchetti, this::getClienteById, this::recuperaViaggiatoriByPrenotazione);
        
        if (mappa == null) return null;

        for (Prenotazione p : mappa.values()) {
            Cliente c = p.getCliente();
            if (c != null) {
                c.addPrenotazione(p);
            }
        }
        return mappa;
    }

    public Map<Integer, Prenotazione> getPrenotazioni(){
        return Collections.unmodifiableMap(this.elencoPrenotazioni);
    }

    public Prenotazione getPrenotazioneById(int idPrenotazione) {
        return this.elencoPrenotazioni.get(idPrenotazione);
    }

    private Map<Integer, Recensione> recuperaRecensioni(){
        Map<Integer, Recensione> mappa = TravelEasyDao.INSTANCE.recuperaRecensioni(conn, this::getClienteById, elencoPrenotazioni);
        if (mappa == null) return null;
        for (Recensione r: mappa.values()){
            Cliente c = r.getCliente();
            c.addRecensione(r);
        }
        
        return mappa;
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
        return TravelEasyDao.INSTANCE.recuperaIdAccount(conn, email);
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

        if (!TravelEasyDao.INSTANCE.createAccount(conn, email, password, "Cliente")) {
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
    public boolean eliminaAccount(String email, String password){
        Account a = elencoAccount.get(email);

        if(!a.validazioneCredenziali(email, password))
            return false;

        if (!a.eliminaCliente(conn))
            return false;

        elencoAccount.remove(email);

        if (!TravelEasyDao.INSTANCE.eliminaAccount(conn, email, password))
            return false;
        
        elencoAccount.remove(email);
        return true;
    }    
    
    //*NUOVA RICARICA
    public float validazioneDatiNuovaRicarica(String numeroCarta, String scadenza, String cvv, String importo, Cliente cliente){
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
                return -5.0F;
        } catch (Exception e){
            return -6.0F;
        }
        
        if (!cliente.controlCvv(cvv)) return -7.0F;

        return importoProva;
    }

    public boolean ricarica(Cliente c, float importo){
        return c.insertOnPortafoglio(conn, importo);
    }

    public boolean insertCartaCredito(Cliente cliente, String numeroCarta, String scadenza, String cvv, String circuito){
        
        return cliente.insertCartaCredito(conn, numeroCarta, scadenza, cvv, circuito);
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
        
        
        PacchettoViaggio p = TravelEasyDao.INSTANCE.createPacchettoViaggio(conn, codice, titolo, citta, nazione, descrizione, prezzo, oreViaggio, visibilità, dataPartenza, dataRitorno, idCompagniaTrasporto, idAlloggio);
        if (p == null)
            return -4;

        elencoPacchetti.put(p.getId(), p);
        return 0;
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

    
    //*ELIMINAZIONE OFFERTE SCADUTE O ESAURITE
    

    public boolean eliminaOfferte() {
        DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);

        Iterator<Map.Entry<PacchettoViaggio, OffertaSpeciale>> it = elencoOfferte.entrySet().iterator();
        while (it.hasNext()) {
            OffertaSpeciale o = it.next().getValue();
            LocalDate fine = LocalDate.parse(o.getDataFine(), FMT);

            if (fine.isBefore(LocalDate.now()) || o.getDisponibilità() == 0) {
                if (!TravelEasyDao.INSTANCE.eliminaOfferta(conn, o))
                    return false;
            }
        }
        return true;
    }

    private boolean rimuoviOffertaEsaurita(OffertaSpeciale offerta) {
        if (offerta == null) {
            return true;
        }

        if (!TravelEasyDao.INSTANCE.eliminaOfferta(conn, offerta)) {
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

    //*INSERIMENTO OFFERTA
    public float validazioneDatiNuovaOfferta(PacchettoViaggio pacchetto, String percentuale, String dataPartenza, String dataFine, String numeroMassimoPacchetti){
        if (percentuale == null || percentuale.isBlank())
            return -1.0F;
        
        float provaF;
        try {
            provaF = Float.parseFloat(percentuale);
            if (provaF <= 0 || provaF > 100)
                return -2.0F;
        } catch (Exception e){
            return -3.0F;
        }

        if (dataFine == null || dataFine.isBlank()) return -5.0F;
        if (dataPartenza == null || dataPartenza.isBlank()) return -5.0F;

        if (!dataFine.matches("\\d{2}-\\d{2}-\\d{4}")) return -5.0F;
        if (!dataPartenza.matches("\\d{2}-\\d{2}-\\d{4}")) return -5.0F;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-uuuu")
                .withResolverStyle(ResolverStyle.STRICT);

        try {
            LocalDate fine = LocalDate.parse(dataFine, formatter);
            LocalDate partenza = LocalDate.parse(dataPartenza, formatter);

            if (fine.isBefore(LocalDate.now())) return -5.0F;

            if (fine.isAfter(partenza)) return -5.0F;

            
        } catch (Exception e) {
            return -5.0F;
        }

        if (numeroMassimoPacchetti == null || numeroMassimoPacchetti.isBlank())
            return -6.0F;
        int provaN;
        try{
             provaN = Integer.parseInt(numeroMassimoPacchetti);
            if (provaN <= 0)
                return -7.0F;
        } catch (Exception e){
            return -8.0F;
        }

        return provaF;
    }

    public boolean createNuovaOfferta(float percentuale, String dataFine, int disponibilità, PacchettoViaggio pacchetto){
        
        float prezzo = pacchetto.getPrezzo();
        float prezzoScontato = prezzo - prezzo*percentuale/100;

        OffertaSpeciale newOfferta = TravelEasyDao.INSTANCE.createNuovaOfferta(conn, pacchetto, percentuale, prezzoScontato, dataFine, disponibilità);
        if (newOfferta == null)
            return false;
        this.elencoOfferte.put(pacchetto, newOfferta);
        notifyOffertaCreata(newOfferta);

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
        PortafoglioVirtuale pv = TravelEasyDao.INSTANCE.getPortafoglioByClienteDB(conn, idUtente);
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
        

        Prenotazione p = TravelEasyDao.INSTANCE.createPrenotazione(conn, idUtente, pacchetto, this::getClienteById);
        
        if (p == null)
            return -1;
        int newId = p.getId();
        Cliente c = getClienteById(idUtente);

        elencoPrenotazioni.put(newId, p);
        if (c != null) {
            c.addPrenotazione(p);
        }

        return newId;
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

    public boolean registrazionePrenotazione(int idPrenotazione, float scontoApplicato, float totaleAggiornato, float offertaApplicata){
        return registrazionePrenotazione(idPrenotazione, scontoApplicato, totaleAggiornato, offertaApplicata, 0.0F);
    }

    public boolean registrazionePrenotazione(int idPrenotazione, float scontoApplicato, float totaleAggiornato, float offertaApplicata, float prezzoAssistenzaSpeciale){
        
        Prenotazione p = this.elencoPrenotazioni.get(idPrenotazione);
        String dataPrenotazione = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-uuuu"));

        if (!TravelEasyDao.INSTANCE.updatePrenotazione(conn, dataPrenotazione, totaleAggiornato, scontoApplicato, offertaApplicata, prezzoAssistenzaSpeciale, idPrenotazione))
            return false;

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
    }

    public boolean pagamentoOnPortafoglioDB(float totale, Cliente c) {
        return c.pagamentoOnPortafoglioDB(conn, totale);
    }

    public boolean rimborsoOnPortafoglioDB(float rimborso, Cliente c) {
        return c.rimborsoOnPortafoglioDB(conn, rimborso);
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

        if (!TravelEasyDao.INSTANCE.updatePacchettoPrenotazione(conn, nuovoPacchetto.getId(), nuovoTotale, nuovaOffertaApplicata, idPrenotazione))
            return false;

        prenotazione.setPacchetto(nuovoPacchetto);
        prenotazione.setPrezzoTotale(nuovoTotale);
        prenotazione.setOffertaApplicata(nuovaOffertaApplicata);
        notifyPrenotazioneModificata(prenotazione);

        return true;
    }

    public int modificaViaggiatori(Prenotazione p, String nome, String cognome, String dataNascita, String tipoDocumento, String codiceDocumento, boolean cecita, boolean sediaRotelle, int index){
        int res = validazioneViaggiatore(nome, cognome, dataNascita, tipoDocumento, codiceDocumento);
         
        if(res != 0)
            return res;
        
        p.replaceViaggiatori(conn, nome, cognome, dataNascita, tipoDocumento, codiceDocumento, cecita, sediaRotelle, index);
        
        notifyPrenotazioneModificata(p);

        return res;
    }

    public void calcolaPrezzoAssistenzaSpeciale(Prenotazione prenotazione) {
        prenotazione.calcolaPrezzoAssistenzaSpeciale();
    }

    public boolean replaceViaggiatoriDB(Prenotazione p){
        return p.replaceViaggiatoriDB(conn);
    }

    public void setPrezzoAssistenzaSpeciale(float nuovoPrezzoAssistenza, Prenotazione prenotazione) {
        prenotazione.setPrezzoAssistenzaSpeciale(nuovoPrezzoAssistenza);
    }

    public void setPrezzoTotale(float nuovoTotale, Prenotazione prenotazione) {
        prenotazione.setPrezzoTotale(nuovoTotale);
    }

    //*RECENSIONI
    public boolean validaDatiNuovaRecensione(String commentoAgenzia, String commentoTrasporto, String commentoAlloggio){
        return !(commentoAgenzia.isBlank() || commentoTrasporto.isBlank() || commentoAlloggio.isBlank());
    }

    public boolean inserisciRecensione(Cliente cliente, Prenotazione prenotazione, String commento, int stelle, String riferimento){
        
        RecensioneAgenzia rAgenzia;
        RecensioneTrasporto rTrasporto;
        RecensioneAlloggio rAlloggio;
        switch (riferimento){
            case "Agenzia":
                rAgenzia = (RecensioneAgenzia)TravelEasyDao.INSTANCE.createRecensione(conn, riferimento, stelle, commento, cliente, prenotazione);
                this.elencoRecensioni.put(rAgenzia.getId(), rAgenzia);
                cliente.addRecensione(rAgenzia); 
                notifyRecensioneCreata(rAgenzia);
                return rAgenzia != null;
            case "Alloggio":
                rAlloggio = (RecensioneAlloggio)TravelEasyDao.INSTANCE.createRecensione(conn, riferimento, stelle, commento, cliente, prenotazione);
                this.elencoRecensioni.put(rAlloggio.getId(), rAlloggio);
                cliente.addRecensione(rAlloggio); 
                notifyRecensioneCreata(rAlloggio);
                return rAlloggio != null;
            case "Trasporto":
                rTrasporto = (RecensioneTrasporto)TravelEasyDao.INSTANCE.createRecensione(conn, riferimento, stelle, commento, cliente, prenotazione);
                this.elencoRecensioni.put(rTrasporto.getId(), rTrasporto);
                cliente.addRecensione(rTrasporto); 
                notifyRecensioneCreata(rTrasporto);
                return rTrasporto != null;
            default:
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
        prenotazione.aggiornaAssistenza(conn, viaggiatore, tipoAssistenza, valore); 
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

        if (giorniAllaPartenza < 2 && giorniAllaPartenza >= 0) {
            return -1.0F;
        }

        if (giorniAllaPartenza < 0)
            return -2.0F;

        float prezzoPrenotazione = prenotazione.getPrezzoTotale();
        if (giorniAllaPartenza >= 7) {
            return prezzoPrenotazione;
        }
        return prezzoPrenotazione * 0.5F;
    }

    public float getRimborsoEliminazionePrenotazione(Prenotazione prenotazione) {
        return calcolaRimborsoEliminazione(prenotazione);
    }

    

    public int eliminaPrenotazione(Prenotazione prenotazione, float rimborso) {
        if (prenotazione == null || prenotazione.getCliente() == null) {
            return -2;
        }

        if (rimborso < 0.0F) {
            return -3;
        }

        Cliente cliente = prenotazione.getCliente();
        if (!prenotazione.rimborsoOnPortafoglioDB(conn, rimborso)) {
            return -1;
        }

        if(!prenotazione.levaOreViaggio(conn))
            return -1;

        if (!TravelEasyDao.INSTANCE.eliminaPrenotazioneDB(conn, prenotazione.getId())) {
            prenotazione.pagamentoOnPortafoglioDB(conn, rimborso);
            return -2;
        }

        elencoPrenotazioni.remove(prenotazione.getId());
        cliente.removePrenotazione(prenotazione.getId());
        notifyPrenotazioneModificata(prenotazione);
        return 0;
    }

    //*CHECK-IN
    public int effettuaCheckIn(Prenotazione p){
        if (p == null) {
            return -3;
        }

        if(p.isCheckedIn()) {
            System.out.println("Check-in già effettuato.");
            return -4;
        }

        return p.checkIn(conn);
    }

    public boolean assistenzaSpecialeModificata(float nuovoPrezzo, float vecchioPrezzo) {
        return nuovoPrezzo != vecchioPrezzo;
    }

    //*METODI VARI

    public Cliente getClienteById(int idCliente){
        for (Account a: elencoAccount.values()){
            Cliente c = a.getCliente();
            if (c != null && c.getId() == idCliente)
                return c;
        }
        return null;
    }

    public Account getAccountToHomeView(String email){
        return this.elencoAccount.get(email);
    }

    public List<Viaggiatore> getViaggiatoriByPrenotazione(int idPrenotazione){
        Prenotazione p = this.elencoPrenotazioni.get(idPrenotazione);
        if (p == null) {
            return null;
        }

        return p.getElencoViaggiatori();
    }
}

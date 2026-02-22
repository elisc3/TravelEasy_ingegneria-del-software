package it.traveleasy;

import java.sql.Connection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Cliente extends Utente {
    private PortafoglioVirtuale pv; 
    private CartaCredito cc;   
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
    }

    public void decrementaPortafoglio(float importo){
        this.pv.decrementaSaldo(importo);
    }

    //*CREAZIONE OGGETTI CONNESSI
    public boolean metodiPagamento(Connection conn){
        if (!PortafoglioVirtualeDao.INSTANCE.insert(conn, this.getId(), 0.0)) {
            return false;
        }

        int idPortafoglio = PortafoglioVirtualeDao.INSTANCE.findIdByUtente(conn, this.getId());
        if (idPortafoglio == 0) {
            return false;
        }

        PortafoglioVirtuale pv = new PortafoglioVirtuale(idPortafoglio, this.getId(), 0.0);
        setPv(pv);

        int idPortafoglioVirtuale = pv.getId();

        if (!CartaCreditoDao.INSTANCE.insertPlaceholder(conn, this.getId(), idPortafoglioVirtuale)) {
            return false;
        }

        this.cc = new CartaCredito("", "", "", "", idPortafoglioVirtuale, this);

        if (!PortafoglioOreDao.INSTANCE.insert(conn, this.getId(), 0.0F, 0)) {
            return false;
        }

         int idPortafoglioOre = PortafoglioOreDao.INSTANCE.findIdByProprietario(conn, this.getId());
         if (idPortafoglioOre == 0) {
            return false;
         }

         PortafoglioOre po = new PortafoglioOre(idPortafoglioOre, this.getId(), 0.0f, 0);
         setPo(po);

        return true;
    }

    //*ELIMINAZIONE OGGETTI CONNESSI
    public boolean eliminaMetodiPagamento(Connection conn){
        if (!CartaCreditoDao.INSTANCE.deleteByUtente(conn, this.getId())) {
            return false;
        }

        if (!PortafoglioVirtualeDao.INSTANCE.deleteByUtente(conn, this.getId())) {
            return false;
        }

        if (!PortafoglioOreDao.INSTANCE.deleteByProprietario(conn, this.getId())) {
            return false;
        }

        return true;
    }
    
    //*PAGAMENTO
    //!RIVEDIAMOLE (2)
    public boolean pagamentoOnPortafoglioDB(Connection conn, float importo){
        if (!PortafoglioVirtualeDao.INSTANCE.decrementSaldoByUtente(conn, this.getId(), importo)) {
            return false;
        }

        this.pv.decrementaSaldo(importo);
        return true;
    }

    public boolean rimborsoOnPortafoglioDB(Connection conn, float importo){
        if (!PortafoglioVirtualeDao.INSTANCE.incrementSaldoByUtente(conn, this.getId(), importo)) {
            return false;
        }

        this.pv.incrementaSaldo(importo);
        return true;
    }

    public void addPrenotazione(Prenotazione p){
        this.elencoPrenotazioniEffettuate.put(p.getId(), p);
    }

    public void removePrenotazione(int idPrenotazione){
        this.elencoPrenotazioniEffettuate.remove(idPrenotazione);
    }

    public Map<Integer, Prenotazione> getElencoPrenotazioniEffettuate() {
        return Collections.unmodifiableMap(this.elencoPrenotazioniEffettuate);
    }

    public void addRecensione(Recensione r){
        this.elencoRecensioni.put(r.getId(), r);
    }

    public boolean aggiornaOreViaggio(Connection conn, float oreViaggio){
        if (this.po == null) {
            return false;
        }
        return this.po.incrementaOre(conn, oreViaggio);
    }

    public boolean levaOreViaggio(Connection conn, float oreViaggio){
        if (this.po == null) {
            return false;
        }
        return this.po.decrementaOre(conn, oreViaggio);
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

    public boolean controlCvv(String cvv){
        return cc.controlCvv(cvv);
    }

    public boolean insertOnPortafoglio(Connection conn, float importo){
        return cc.insertOnPortafoglio(conn, importo);
    }

    public boolean insertCartaCredito(Connection conn, String numeroCarta, String scadenza, String cvv, String circuito) {
        if (!CartaCreditoDao.INSTANCE.updateByUtente(conn, this.getId(), numeroCarta, scadenza, cvv, circuito, this.pv.getId())) {
            return false;
        }

        this.setCc(new CartaCredito(numeroCarta, scadenza, cvv, circuito, this.pv.getId(), this));
        return true;
    }

}

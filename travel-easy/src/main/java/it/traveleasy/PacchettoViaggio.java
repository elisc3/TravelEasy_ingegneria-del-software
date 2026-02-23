package it.traveleasy;

import java.sql.Connection;

public class PacchettoViaggio {
    private int id;
    private String codice;
    private String città;
    private String titolo;
    private String nazione;
    private String dataPartenza;
    private String dataRitorno;
    private String descrizione;
    private float prezzo;
    private float oreViaggio;
    private int visibilità;
    private CompagniaTrasporto compagniaTrasporto;
    private Alloggio alloggio;
    private Connection conn;

    public PacchettoViaggio(int id, String codice, String titolo,  String città, String nazione, String dataPartenza, String dataRitorno, String descrizione, float prezzo, float oreViaggio, int visibilità, int idCompagniaTrasporto, int idAlloggio, Connection conn){
        this.id= id;
        this.codice = codice;
        this.titolo = titolo;
        this.città = città;
        this.nazione = nazione;
        this.dataPartenza = dataPartenza;
        this.dataRitorno = dataRitorno;
        this.descrizione = descrizione;
        this.prezzo = prezzo;
        this.oreViaggio = oreViaggio;
        this.visibilità = visibilità;
        this.conn = conn;
        this.compagniaTrasporto = this.recuperaCompagnia(idCompagniaTrasporto);
        this.alloggio = this.recuperaAlloggio(idAlloggio);
    }

    public int getId() {
        return id;
    }

    public String getTitolo(){
        return titolo;
    }

    public String getCittà() {
        return città;
    }

    public String getNazione() {
        return nazione;
    }

    public String getDataPartenza() {
        return dataPartenza;
    }

    public String getDataRitorno() {
        return dataRitorno;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public float getPrezzo() {
        return prezzo;
    }

    public int isVisibilità() {
        return visibilità;
    }

    public CompagniaTrasporto getCompagniaTrasporto() {
        return compagniaTrasporto;
    }

    public Alloggio getAlloggio(){
        return alloggio;
    }

    public String getCodice(){
        return this.codice;
    }

    public float getOreViaggio(){
        return this.oreViaggio;
    }

    private CompagniaTrasporto recuperaCompagnia(int idCompagniaTrasporto){
        return PacchettoViaggioDao.INSTANCE.findCompagniaById(conn, idCompagniaTrasporto);
    }

    private Alloggio recuperaAlloggio(int idAlloggio){
        return PacchettoViaggioDao.INSTANCE.findAlloggioById(conn, idAlloggio);
    }


}

package it.traveleasy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

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
        String query = "SELECT * from CompagniaTrasporto WHERE id = ?;";

        try (PreparedStatement pstmt = conn.prepareStatement(query)){
            pstmt.setInt(1, idCompagniaTrasporto);

            ResultSet rs = pstmt.executeQuery();
            String nome = rs.getString("Nome");
            String tipo = rs.getString("TIPO");

            CompagniaTrasporto c = new CompagniaTrasporto(idCompagniaTrasporto, nome, tipo);
            return c;
        } catch (SQLException e){
            System.out.println("Errore recuperaCompagnia: "+e);
            return null;
        }
    }

    private Alloggio recuperaAlloggio(int idAlloggio){
        String query = "SELECT * from Alloggio WHERE id = ?;";

        try (PreparedStatement pstmt = conn.prepareStatement(query)){
            pstmt.setInt(1, idAlloggio);

            ResultSet rs = pstmt.executeQuery();
            String nome = rs.getString("Nome");
            String indirizzo = rs.getString("Indirizzo");
            String tipo = rs.getString("TIPO");
            int stelle = rs.getInt("Stelle");

            Alloggio a = new Alloggio(idAlloggio, nome, indirizzo, tipo, stelle);
            return a;
        } catch (SQLException e){
            System.out.println("Errore recuperaAlloggio: "+e);
            return null;
        }
    }


}
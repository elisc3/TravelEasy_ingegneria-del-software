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
    private int visibilità;
    private int idCompagniaTrasporto;
    private int idAlloggio;
    private Connection conn;

    public PacchettoViaggio(int id, String codice, String titolo,  String città, String nazione, String dataPartenza, String dataRitorno, String descrizione, float prezzo, int visibilità, int idCompagniaTrasporto, int idAlloggio, Connection conn){
        this.id= id;
        this.codice = codice;
        this.titolo = titolo;
        this.città = città;
        this.nazione = nazione;
        this.dataPartenza = dataPartenza;
        this.dataRitorno = dataRitorno;
        this.descrizione = descrizione;
        this.prezzo = prezzo;
        this.visibilità = visibilità;
        this.idCompagniaTrasporto = idCompagniaTrasporto;
        this.idAlloggio = idAlloggio;
        this.conn = conn;
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

    public int getIdCompagniaTrasporto() {
        return idCompagniaTrasporto;
    }

    public int getIdAlloggio(){
        return idAlloggio;
    }

    public String getCodice(){
        return this.codice;
    }

    public float validazionePercentunaleNuovaOfferta(String percentuale){
        if (percentuale == null || percentuale.isBlank())
            return -1.0F;
        
        float provaN;
        try {
            provaN = Float.parseFloat(percentuale);
            if (provaN < 0 || provaN > 100)
                return -2.0F;
        } catch (Exception e){
            return -3.0F;
        }

        return provaN;
    }

    public boolean validazioneDataInserimentoOfferta(String dataFine, String dataPartenza) {
        if (dataFine == null || dataFine.isBlank()) return false;
        if (dataPartenza == null || dataPartenza.isBlank()) return false;

        if (!dataFine.matches("\\d{2}-\\d{2}-\\d{4}")) return false;
        if (!dataPartenza.matches("\\d{2}-\\d{2}-\\d{4}")) return false;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-uuuu")
                .withResolverStyle(ResolverStyle.STRICT);

        try {
            LocalDate fine = LocalDate.parse(dataFine, formatter);
            LocalDate partenza = LocalDate.parse(dataPartenza, formatter);

            if (fine.isBefore(LocalDate.now())) return false;

            if (fine.isAfter(partenza)) return false;

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public int validazioneNumeroPacchettiNuovaOfferta(String numeroMassimoPacchetti){
        if (numeroMassimoPacchetti == null || numeroMassimoPacchetti.isBlank())
            return -1;
        int provaN;
        try{
             provaN = Integer.parseInt(numeroMassimoPacchetti);
            if (provaN <= 0)
                return -2;
        } catch (Exception e){
            return -3;
        }

        return provaN;
    }


    public OffertaSpeciale nuovaOfferta(float percentuale, String dataFine, int disponibilità){
        String query = "INSERT INTO OffertaSpeciale (Pacchetto, ScontoPercentuale, PrezzoScontato, DataFine, Disponibilità, Visibilità) values (?, ?, ?, ?, ?, ?);";
        
        float prezzoScontato = prezzo - prezzo*percentuale/100;

        try (PreparedStatement pstmt = conn.prepareStatement(query)){
            pstmt.setInt(1, id);
            pstmt.setFloat(2, percentuale);
            pstmt.setFloat(3, prezzoScontato);
            pstmt.setString(4, dataFine);
            pstmt.setInt(5, disponibilità);
            pstmt.setBoolean(6, true);

            pstmt.executeUpdate();

            int newId = 0;
            try (Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery("SELECT last_insert_rowid();")) {
                if (rs.next()) {
                     newId = rs.getInt(1);
                }
            }
            return new OffertaSpeciale(newId, this, percentuale, prezzoScontato, dataFine, disponibilità, true);

        } catch (SQLException e){
            System.out.println("Errore nuovaOfferta: "+e);
            return null;
        }
        
    }   


}
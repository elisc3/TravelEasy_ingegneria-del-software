package it.traveleasy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OffertaSpeciale {
    private int id;
    //private int idPacchetto;
    private PacchettoViaggio pacchetto;
    private float scontoPercentuale;
    private float prezzoScontato;
    private String dataFine;
    private int Disponibilità;
    //private boolean visibilità;

    public OffertaSpeciale(int id, PacchettoViaggio pacchetto, float scontoPercentuale, float prezzoScontato, String dataFine, int Disponibilità /*, boolean visibilità*/) {
        this.id = id;
        this.pacchetto = pacchetto;
        this.scontoPercentuale = scontoPercentuale;
        this.prezzoScontato = prezzoScontato;
        this.dataFine = dataFine;
        this.Disponibilità = Disponibilità;
        //this.visibilità = visibilità;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PacchettoViaggio getPacchetto() {
        return pacchetto;
    }

    public void setPacchetto(PacchettoViaggio idPacchetto) {
        this.pacchetto = idPacchetto;
    }

    public float getScontoPercentuale() {
        return scontoPercentuale;
    }

    public void setScontoPercentuale(float scontoPercentuale) {
        this.scontoPercentuale = scontoPercentuale;
    }

    public float getPrezzoScontato() {
        return prezzoScontato;
    }

    public void setPrezzoScontato(float prezzoScontato) {
        this.prezzoScontato = prezzoScontato;
    }

    public String getDataFine() {
        return dataFine;
    }

    public void setDataFine(String dataFine) {
        this.dataFine = dataFine;
    }

    public int getDisponibilità() {
        return Disponibilità;
    }

    public void setDisponibilità(int Disponibilità) {
        this.Disponibilità = Disponibilità;
    }

    /*public boolean isVisibilità() {
        return visibilità;
    }

    public void setVisibilità(boolean visibilità) {
        this.visibilità = visibilità;
    }*/

    private boolean diminuisciDisponibilitàDB(Connection conn){
        String query = "UPDATE OffertaSpeciale SET Disponibilità = Disponibilità - 1 WHERE id = ?;";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();

            return true;
        } catch (SQLException e){
            System.out.println("Errore diminuisci diponibilità offerta: "+e);
            return false;
        }
        
    }

    public boolean diminuisciDisponibilità(Connection conn){
        
        if(this.diminuisciDisponibilitàDB(conn)){
            Disponibilità--;
            return true;
        } else
            return false;  
    }
}

package it.traveleasy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PortafoglioOre {
    private int id;
    private int utenteId;
    private float  ore;
    private int sconto;

    public PortafoglioOre(int id, int utenteId, float ore, int sconto) {
        this.id = id;
        this.utenteId = utenteId;
        this.ore = ore;
        this.sconto = sconto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUtenteId() {
        return utenteId;
    }

    public void setUtenteId(int utenteId) {
        this.utenteId = utenteId;
    }

    public float getOre() {
        return ore;
    }

    public void setOre(float ore) {
        this.ore = ore;
    }

    public int getSconto() {
        return sconto;
    }

    public void setSconto(int sconto) {
        this.sconto = sconto;
    }

    public boolean incrementaOre(Connection conn, float ore) {
        this.ore += ore;
        aggiornaSconto();

        String query = "UPDATE PortafoglioOre SET Ore = ?, Sconto = ? WHERE id = ?;";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setFloat(1, this.ore);
            pstmt.setInt(2, this.sconto);
            pstmt.setInt(3, this.id);
            pstmt.executeUpdate();
        } catch (SQLException e){
            System.out.println("Errore aggiornamento Portafoglio Ore: "+e);
            return false;
        }
        return true;
    }

    public boolean aggiornaSconto() {
        if(this.ore >= 10) {
            int n = (int) (this.ore / 10);
            this.sconto = n * 3;
        }

        return true;
    }
}

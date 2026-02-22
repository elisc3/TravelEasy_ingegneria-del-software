package it.traveleasy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface PrenotazioneDao {
    PrenotazioneDao INSTANCE = new JdbcPrenotazioneDao();

    boolean updateCheckIn(Connection conn, int prenotazioneId);

    boolean insertViaggiatore(Connection conn, int prenotazioneId, Viaggiatore v);

    boolean deleteViaggiatoriByPrenotazione(Connection conn, int prenotazioneId);

    boolean deletePrenotazioneById(Connection conn, int prenotazioneId);

}

class JdbcPrenotazioneDao implements PrenotazioneDao {
    @Override
    public boolean updateCheckIn(Connection conn, int prenotazioneId) {
        String query = "UPDATE Prenotazioni SET checkIn = 1 WHERE id = ?;";
        try (java.sql.PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, prenotazioneId);
            pstmt.executeUpdate();
            return true;
        } catch (java.sql.SQLException e) {
            System.out.println("Errore salvataggio check-in: " + e);
            return false;
        }
    }

    @Override
    public boolean insertViaggiatore(Connection conn, int prenotazioneId, Viaggiatore v) {
        String query = "INSERT INTO Viaggiatore (Nome, Cognome, DataNascita, TipoDocumento, CodiceDocumento, Prenotazione, SediaRotelle, \"Cecit\u00E0\") values (?, ?, ?, ?, ?, ?, ?, ?);";
        try (java.sql.PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, v.getNome());
            pstmt.setString(2, v.getCognome());
            pstmt.setString(3, v.getDataNascita());
            pstmt.setString(4, v.getTipoDocumento());
            pstmt.setString(5, v.getCodiceDocumento());
            pstmt.setInt(6, prenotazioneId);
            pstmt.setInt(7, v.isSediaRotelle() ? 1 : 0);
            pstmt.setInt(8, v.isCecita() ? 1 : 0);
            pstmt.executeUpdate();
            return true;
        } catch (java.sql.SQLException e) {
            System.out.println("Errore insertViaggiatoriDB: " + e);
            return false;
        }
    }

    @Override
    public boolean deleteViaggiatoriByPrenotazione(Connection conn, int prenotazioneId) {
        String query = "DELETE FROM Viaggiatore WHERE Prenotazione = ?;";
        try (java.sql.PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, prenotazioneId);
            pstmt.executeUpdate();
            return true;
        } catch (java.sql.SQLException e) {
            System.out.println("Errore deleteViaggiatoriDB: " + e);
            return false;
        }
    }

    @Override
    public boolean deletePrenotazioneById(Connection conn, int prenotazioneId) {
        String query = "DELETE FROM Prenotazioni WHERE id = ?;";
        try (java.sql.PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, prenotazioneId);
            return pstmt.executeUpdate() == 1;
        } catch (java.sql.SQLException e) {
            System.out.println("Errore deletePrenotazioneById: " + e);
            return false;
        }
    }
}

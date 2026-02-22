package it.traveleasy;

import java.sql.Connection;

public interface OffertaSpecialeDao {
    OffertaSpecialeDao INSTANCE = new JdbcOffertaSpecialeDao();

    boolean decrementDisponibilita(Connection conn, int offertaId);

    boolean deleteById(Connection conn, int offertaId);
}

class JdbcOffertaSpecialeDao implements OffertaSpecialeDao {
    @Override
    public boolean decrementDisponibilita(Connection conn, int offertaId) {
        String query = "UPDATE OffertaSpeciale SET Disponibilit\u00E0 = Disponibilit\u00E0 - 1 WHERE id = ?;";
        try (java.sql.PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, offertaId);
            pstmt.executeUpdate();
            return true;
        } catch (java.sql.SQLException e) {
            System.out.println("Errore diminuisci disponibilit\u00E0 offerta: " + e);
            return false;
        }
    }

    @Override
    public boolean deleteById(Connection conn, int offertaId) {
        String query = "DELETE FROM OffertaSpeciale WHERE id = ?;";
        try (java.sql.PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, offertaId);
            pstmt.executeUpdate();
            return true;
        } catch (java.sql.SQLException e) {
            System.out.println("Errore delete offerta: " + e);
            return false;
        }
    }
}

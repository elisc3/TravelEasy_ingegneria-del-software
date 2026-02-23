package it.traveleasy;

import java.sql.Connection;

public interface PortafoglioOreDao {
    PortafoglioOreDao INSTANCE = new JdbcPortafoglioOreDao();

    boolean insert(Connection conn, int utenteId, float ore, int sconto);

    int findIdByProprietario(Connection conn, int utenteId);

    boolean update(Connection conn, int id, float ore, float sconto);

    boolean deleteByProprietario(Connection conn, int utenteId);
}

class JdbcPortafoglioOreDao implements PortafoglioOreDao {
    @Override
    public boolean insert(Connection conn, int utenteId, float ore, int sconto) {
        String query = "INSERT INTO PortafoglioOre (Ore, Sconto, proprietario) VALUES (?, ?, ?);";
        try (java.sql.PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setFloat(1, ore);
            pstmt.setInt(2, sconto);
            pstmt.setInt(3, utenteId);
            pstmt.executeUpdate();
            return true;
        } catch (java.sql.SQLException e) {
            System.out.println("Errore creazione Portafoglio Ore: " + e);
            return false;
        }
    }

    @Override
    public int findIdByProprietario(Connection conn, int utenteId) {
        String query = "SELECT id FROM PortafoglioOre WHERE proprietario = ?;";
        try (java.sql.PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, utenteId);
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (java.sql.SQLException e) {
            System.out.println("Errore recupero id Portafoglio Ore: " + e);
        }
        return 0;
    }

    @Override
    public boolean update(Connection conn, int id, float ore, float sconto) {
        String query = "UPDATE PortafoglioOre SET Ore = ?, Sconto = ? where id = ?;";
        try (java.sql.PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setFloat(1, ore);
            pstmt.setFloat(2, sconto);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
            return true;
        } catch (java.sql.SQLException e) {
            System.out.println("Errore aggiornamento Portafoglio Ore: " + e);
            return false;
        }
    }

    @Override
    public boolean deleteByProprietario(Connection conn, int utenteId) {
        String query = "DELETE FROM PortafoglioOre WHERE proprietario = ?;";
        try (java.sql.PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, utenteId);
            pstmt.executeUpdate();
            return true;
        } catch (java.sql.SQLException e) {
            System.out.println("Errore eliminazione portafoglio ore: " + e);
            return false;
        }
    }
}

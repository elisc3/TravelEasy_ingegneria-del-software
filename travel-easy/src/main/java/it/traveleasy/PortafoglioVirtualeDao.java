package it.traveleasy;

import java.sql.Connection;

public interface PortafoglioVirtualeDao {
    PortafoglioVirtualeDao INSTANCE = new JdbcPortafoglioVirtualeDao();

    boolean insert(Connection conn, int utenteId, double saldo);

    int findIdByUtente(Connection conn, int utenteId);

    boolean incrementSaldoByUtente(Connection conn, int utenteId, float importo);

    boolean decrementSaldoByUtente(Connection conn, int utenteId, float importo);

    boolean deleteByUtente(Connection conn, int utenteId);
}

class JdbcPortafoglioVirtualeDao implements PortafoglioVirtualeDao {
    @Override
    public boolean insert(Connection conn, int utenteId, double saldo) {
        String query = "INSERT INTO PortafoglioVirtuale (Utente, Saldo) VALUES (?, ?);";
        try (java.sql.PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, utenteId);
            pstmt.setDouble(2, saldo);
            pstmt.executeUpdate();
            return true;
        } catch (java.sql.SQLException e) {
            System.out.println("Errore creazione Portafoglio Virtuale: " + e);
            return false;
        }
    }

    @Override
    public int findIdByUtente(Connection conn, int utenteId) {
        String query = "SELECT id FROM PortafoglioVirtuale WHERE Utente = ?;";
        try (java.sql.PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, utenteId);
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (java.sql.SQLException e) {
            System.out.println("Errore recupero id Portafoglio Virtuale: " + e);
        }
        return 0;
    }

    @Override
    public boolean incrementSaldoByUtente(Connection conn, int utenteId, float importo) {
        return updateSaldoByUtente(conn, utenteId, importo, true);
    }

    @Override
    public boolean decrementSaldoByUtente(Connection conn, int utenteId, float importo) {
        return updateSaldoByUtente(conn, utenteId, importo, false);
    }

    @Override
    public boolean deleteByUtente(Connection conn, int utenteId) {
        String query = "DELETE FROM PortafoglioVirtuale WHERE Utente = ?;";
        try (java.sql.PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, utenteId);
            pstmt.executeUpdate();
            return true;
        } catch (java.sql.SQLException e) {
            System.out.println("Errore eliminazione portafoglio virtuale: " + e);
            return false;
        }
    }

    private boolean updateSaldoByUtente(Connection conn, int utenteId, float importo, boolean increment) {
        String query = increment
            ? "UPDATE PortafoglioVirtuale SET Saldo = Saldo + ? WHERE Utente = ?;"
            : "UPDATE PortafoglioVirtuale SET Saldo = Saldo - ? WHERE Utente = ?;";

        try (java.sql.PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setFloat(1, importo);
            pstmt.setInt(2, utenteId);
            pstmt.executeUpdate();
            return true;
        } catch (java.sql.SQLException e) {
            System.out.println("Errore aggiornamento saldo PortafoglioVirtuale: " + e);
            return false;
        }
    }
}

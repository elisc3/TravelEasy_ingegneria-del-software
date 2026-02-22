package it.traveleasy;

import java.sql.Connection;

public interface AccountDao {
    AccountDao INSTANCE = new JdbcAccountDao();

    int findIdByEmail(Connection conn, String email);

    int insert(Connection conn, String email, String password, String ruolo);

    boolean deleteByCredentials(Connection conn, String email, String password);
}

class JdbcAccountDao implements AccountDao {
    @Override
    public int findIdByEmail(Connection conn, String email) {
        String query = "SELECT id FROM Account WHERE Email = ?;";
        try (java.sql.PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (java.sql.SQLException e) {
            System.out.println("Errore findIdByEmail: " + e);
        }
        return 0;
    }

    @Override
    public int insert(Connection conn, String email, String password, String ruolo) {
        String query = "INSERT INTO Account (Email, Password, Ruolo) values (?, ?, ?);";
        try (java.sql.PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            pstmt.setString(3, ruolo);
            pstmt.executeUpdate();
            return 1;
        } catch (java.sql.SQLException e) {
            System.out.println("Errore insert account: " + e);
            return 0;
        }
    }

    @Override
    public boolean deleteByCredentials(Connection conn, String email, String password) {
        String query = "DELETE FROM Account WHERE Email = ? AND Password = ?;";
        try (java.sql.PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true;
        } catch (java.sql.SQLException e) {
            System.out.println("Errore deleteByCredentials: " + e);
            return false;
        }
    }
}

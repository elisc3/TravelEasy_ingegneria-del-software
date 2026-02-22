package it.traveleasy;

import java.sql.Connection;

public interface UtenteDao {
    UtenteDao INSTANCE = new JdbcUtenteDao();

    boolean insertCliente(Connection conn, String nome, String cognome, String telefono, int accountId);

    int findIdByAccount(Connection conn, int accountId);

    boolean deleteById(Connection conn, int utenteId);
}

class JdbcUtenteDao implements UtenteDao {
    @Override
    public boolean insertCliente(Connection conn, String nome, String cognome, String telefono, int accountId) {
        String query = "INSERT INTO Utenti (Nome, Cognome, Telefono, Ruolo, Account) values (?, ?, ?, ?, ?);";
        try (java.sql.PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, nome);
            pstmt.setString(2, cognome);
            pstmt.setString(3, telefono);
            pstmt.setString(4, "Cliente");
            pstmt.setInt(5, accountId);
            pstmt.executeUpdate();
            return true;
        } catch (java.sql.SQLException e) {
            System.out.println("Errore insert utente cliente: " + e);
            return false;
        }
    }

    @Override
    public int findIdByAccount(Connection conn, int accountId) {
        String query = "SELECT id FROM Utenti WHERE Account = ?;";
        try (java.sql.PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, accountId);
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (java.sql.SQLException e) {
            System.out.println("Errore findIdByAccount: " + e);
        }
        return 0;
    }

    @Override
    public boolean deleteById(Connection conn, int utenteId) {
        String query = "DELETE FROM Utenti WHERE id = ?;";
        try (java.sql.PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, utenteId);
            pstmt.executeUpdate();
            return true;
        } catch (java.sql.SQLException e) {
            System.out.println("Errore delete utente: " + e);
            return false;
        }
    }
}

package it.traveleasy;

import java.sql.Connection;

public interface CartaCreditoDao {
    CartaCreditoDao INSTANCE = new JdbcCartaCreditoDao();

    boolean insertPlaceholder(Connection conn, int utenteId, int idPortafoglioVirtuale);

    boolean updateByUtente(
        Connection conn,
        int utenteId,
        String numeroCarta,
        String scadenza,
        String cvv,
        String circuito,
        int idPortafoglioVirtuale
    );

    boolean deleteByUtente(Connection conn, int utenteId);
}

class JdbcCartaCreditoDao implements CartaCreditoDao {
    @Override
    public boolean insertPlaceholder(Connection conn, int utenteId, int idPortafoglioVirtuale) {
        String query = "INSERT INTO CartaCredito (Utente, NumeroCarta, Scadenza, cvv, Circuito, PortafoglioVirtuale) VALUES (?, ?, ?, ?, ?, ?);";
        try (java.sql.PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, utenteId);
            pstmt.setString(2, "");
            pstmt.setString(3, "");
            pstmt.setString(4, "");
            pstmt.setString(5, "");
            pstmt.setInt(6, idPortafoglioVirtuale);
            pstmt.executeUpdate();
            return true;
        } catch (java.sql.SQLException e) {
            System.out.println("Errore creazione carta di credito: " + e);
            return false;
        }
    }

    @Override
    public boolean updateByUtente(
        Connection conn,
        int utenteId,
        String numeroCarta,
        String scadenza,
        String cvv,
        String circuito,
        int idPortafoglioVirtuale
    ) {
        String query = "UPDATE CartaCredito SET NumeroCarta = ?, Scadenza = ?, cvv = ?, Circuito = ?, PortafoglioVirtuale = ? WHERE Utente = ?;";
        try (java.sql.PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, numeroCarta);
            pstmt.setString(2, scadenza);
            pstmt.setString(3, cvv);
            pstmt.setString(4, circuito);
            pstmt.setInt(5, idPortafoglioVirtuale);
            pstmt.setInt(6, utenteId);
            pstmt.executeUpdate();
            return true;
        } catch (java.sql.SQLException e) {
            System.out.println("Errore aggiornamento carta di credito: " + e);
            return false;
        }
    }

    @Override
    public boolean deleteByUtente(Connection conn, int utenteId) {
        String query = "DELETE FROM CartaCredito WHERE Utente = ?;";
        try (java.sql.PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, utenteId);
            pstmt.executeUpdate();
            return true;
        } catch (java.sql.SQLException e) {
            System.out.println("Errore eliminazione carta di credito: " + e);
            return false;
        }
    }
}

package it.traveleasy;

import java.sql.Connection;

public interface PacchettoViaggioDao {
    PacchettoViaggioDao INSTANCE = new JdbcPacchettoViaggioDao();

    CompagniaTrasporto findCompagniaById(Connection conn, int idCompagniaTrasporto);

    Alloggio findAlloggioById(Connection conn, int idAlloggio);
}

class JdbcPacchettoViaggioDao implements PacchettoViaggioDao {
    @Override
    public CompagniaTrasporto findCompagniaById(Connection conn, int idCompagniaTrasporto) {
        String query = "SELECT * from CompagniaTrasporto WHERE id = ?;";
        try (java.sql.PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idCompagniaTrasporto);
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String nome = rs.getString("Nome");
                    String tipo = rs.getString("TIPO");
                    return new CompagniaTrasporto(idCompagniaTrasporto, nome, tipo);
                }
            }
        } catch (java.sql.SQLException e) {
            System.out.println("Errore recuperaCompagnia: " + e);
        }
        return null;
    }

    @Override
    public Alloggio findAlloggioById(Connection conn, int idAlloggio) {
        String query = "SELECT * from Alloggio WHERE id = ?;";
        try (java.sql.PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idAlloggio);
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String nome = rs.getString("Nome");
                    String indirizzo = rs.getString("Indirizzo");
                    String tipo = rs.getString("TIPO");
                    int stelle = rs.getInt("Stelle");
                    return new Alloggio(idAlloggio, nome, indirizzo, tipo, stelle);
                }
            }
        } catch (java.sql.SQLException e) {
            System.out.println("Errore recuperaAlloggio: " + e);
        }
        return null;
    }
}

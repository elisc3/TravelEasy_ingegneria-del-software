package it.traveleasy.testsupport;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class TestDbSupport {
    private TestDbSupport() {
    }

    public static Path createTempDbPath() throws IOException {
        return Files.createTempFile("travel-easy-test-", ".db");
    }

    public static Connection openConnection(Path dbPath) throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + dbPath.toAbsolutePath());
    }

    public static void runSqlScript(Connection conn, String resourcePath) throws IOException, SQLException {
        String sql = Files.readString(Path.of("src", "test", "resources", resourcePath), StandardCharsets.UTF_8);
        String[] statements = sql.split(";");
        try (Statement st = conn.createStatement()) {
            for (String raw : statements) {
                String statement = raw.trim();
                if (!statement.isEmpty()) {
                    st.execute(statement);
                }
            }
        }
    }

    public static int countRows(Connection conn, String tableName) throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + tableName)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}

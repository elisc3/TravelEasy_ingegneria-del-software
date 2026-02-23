package it.traveleasy.testsupport;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import it.traveleasy.TravelEasy;

public abstract class BaseTravelEasyTest {
    protected Path dbPath;
    protected Connection conn;
    protected TravelEasy te;

    @BeforeEach
    void setUpBase() throws Exception {
        dbPath = TestDbSupport.createTempDbPath();
        conn = TestDbSupport.openConnection(dbPath);
        TestDbSupport.runSqlScript(conn, "sql/schema.sql");
        TestDbSupport.runSqlScript(conn, "sql/seed_base.sql");
        te = new TravelEasy(conn);
    }

    @AfterEach
    void tearDownBase() throws Exception {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
        if (dbPath != null) {
            Files.deleteIfExists(dbPath);
        }
    }
}

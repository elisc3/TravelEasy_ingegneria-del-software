package it.traveleasy;

import java.io.File;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

public class App extends Application {
    private static Connection conn;
    private static TravelEasy te;

    static final int WIDTH = 920;
    static final int HEIGHT = 560;
    static final String STYLESHEET = "/styles/app.css";
    private String DB_URL;

    @Override
    public void start(Stage stage) {
        stage.setTitle("TravelEasy");

        conn = this.connect();
        te = new TravelEasy(conn);

        //conn = db.connect();
        showLogin(stage);
        stage.show();
    }

    public Connection connect() {
        try {

            
        String basePath = new File(System.getProperty("user.dir")).getAbsolutePath();

        // Percorso relativo del database all'interno del pacchetto
        String dbPath = Paths.get(basePath, "travel-easy.db").toString();
        DB_URL = "jdbc:sqlite:" + dbPath;
       
            Connection conn = DriverManager.getConnection(DB_URL);
            try (Statement st = conn.createStatement()) {
                st.execute("PRAGMA busy_timeout = 5000;");
            }
            System.out.println("Connessione al database riuscita!");
            //this.creaTabelle(conn);
            return conn;
        } catch (Exception e) {
            System.out.println("Errore di connessione: " + e.getMessage());
            return null;
        }
    }

    static void showLogin(Stage stage) {
        configureWindowForAuth(stage);
        LoginView view = new LoginView(stage, conn, te);
        Scene scene = new Scene(view.getRoot(), WIDTH, HEIGHT);
        scene.getStylesheets().add(App.class.getResource(STYLESHEET).toExternalForm());
        stage.setScene(scene);
    }

    static void showSignup(Stage stage) {
        configureWindowForAuth(stage);
        SignupView view = new SignupView(stage, te, conn);
        Scene scene = new Scene(view.getRoot(), WIDTH, HEIGHT);
        scene.getStylesheets().add(App.class.getResource(STYLESHEET).toExternalForm());
        stage.setScene(scene);
    }

    static void showHome(Stage stage, String emailUtente) {
        HomeView view = new HomeView(te, conn, emailUtente);
        Scene scene = new Scene(view.getRoot(), WIDTH, HEIGHT);
        scene.getStylesheets().add(App.class.getResource(STYLESHEET).toExternalForm());
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> javafx.application.Platform.exit());
        configureWindowForHome(stage);
    }

    static void showOperatoreHomeView(Stage stage) {
        OperatoreHomeView view = new OperatoreHomeView(conn, te);
        Scene scene = new Scene(view.getRoot(), WIDTH, HEIGHT);
        scene.getStylesheets().add(App.class.getResource(STYLESHEET).toExternalForm());
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> javafx.application.Platform.exit());
        configureWindowForHome(stage);
    }

    private static void configureWindowForAuth(Stage stage) {
        stage.setFullScreen(false);
        stage.setMaximized(false);
        stage.setResizable(false);
        stage.setFullScreenExitHint("");
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
    }

    private static void configureWindowForHome(Stage stage) {
        stage.setResizable(false);
        stage.setMaximized(true);
        stage.setFullScreen(false);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

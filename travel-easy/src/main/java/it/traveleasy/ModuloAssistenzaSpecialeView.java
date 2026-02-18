package it.traveleasy;

import java.sql.Connection;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.List;

public class ModuloAssistenzaSpecialeView {

    private final VBox root;
    private Connection conn;
    private final TravelEasy te;
    private final Prenotazione prenotazione;
    private final AssistenzaObserver observer;
    private final List<Viaggiatore> viaggiatori;
    private final DialogCloseHandler closeHandler;

    public interface DialogCloseHandler {
        void onConferma(List<Viaggiatore> viaggiatori);
    }

    public ModuloAssistenzaSpecialeView(Connection conn, TravelEasy te, Prenotazione prenotazione, AssistenzaObserver observer) {
        this.conn = conn;
        this.te = te;
        this.prenotazione = prenotazione;
        this.observer = observer;
        this.viaggiatori = prenotazione != null ? prenotazione.getElencoViaggiatori() : null;
        this.closeHandler = null;
        this.root = build();
    }

    public ModuloAssistenzaSpecialeView(List<Viaggiatore> viaggiatori, DialogCloseHandler closeHandler) {
        this.conn = null;
        this.te = null;
        this.prenotazione = null;
        this.observer = null;
        this.viaggiatori = viaggiatori;
        this.closeHandler = closeHandler;
        this.root = build();
    }

    public VBox getRoot() {
        return root;
    }

    private VBox build() {
        VBox content = new VBox(12);
        content.setPadding(new Insets(16));
        content.getStyleClass().add("booking-content");

        Label title = new Label("Assistenza speciale");
        title.getStyleClass().add("section-title");

        Label subtitle = new Label("Seleziona eventuali richieste per ciascun viaggiatore");
        subtitle.getStyleClass().add("package-meta");

        VBox list = new VBox(12);
        list.getStyleClass().add("package-list");

        // Costruzione card per ogni viaggiatore
        if (viaggiatori != null) {
            for (Viaggiatore v : viaggiatori) {
                list.getChildren().add(buildViaggiatoreCard(v));
            }
        }

        ScrollPane scrollPane = new ScrollPane(list);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("package-scroll");

        Button confirmButton = new Button("Conferma");
        confirmButton.getStyleClass().add("primary-button");
        confirmButton.setOnAction(e -> {
            if (te != null && prenotazione != null) {
                te.confermaAssistenzaSpeciale(prenotazione);
                openPagamentoView(prenotazione.getPrezzoAssistenzaSpeciale());
            } else if (closeHandler != null) {
                closeHandler.onConferma(viaggiatori);
            }
        });

        content.getChildren().addAll(title, subtitle, scrollPane, confirmButton);
        return content;
    }

    private VBox buildViaggiatoreCard(Viaggiatore v) {
        Label traveler = new Label(v.getNome() + " " + v.getCognome());
        traveler.getStyleClass().add("package-title");

        // --- SEDIA A ROTELLE ---
        CheckBox sediaCheck = new CheckBox("Sedia rotelle");
        sediaCheck.getStyleClass().add("package-description");
        sediaCheck.setSelected(v.isSediaRotelle());

        sediaCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (observer != null && prenotazione != null) {
                observer.onAssistenzaChanged(prenotazione, v, "sediaRotelle", newVal);
            } else {
                v.setSediaRotelle(newVal);
            }
        });

        Label sediaPrice = new Label("EUR 35.00");
        sediaPrice.getStyleClass().add("package-meta");

        HBox spacer1 = new HBox();
        HBox.setHgrow(spacer1, Priority.ALWAYS);

        HBox sediaRow = new HBox(10, sediaCheck, spacer1, sediaPrice);
        sediaRow.setAlignment(Pos.CENTER_LEFT);

        // --- CECITÀ ---
        CheckBox cecitaCheck = new CheckBox("Cecità");
        cecitaCheck.getStyleClass().add("package-description");
        cecitaCheck.setSelected(v.isCecita());

        cecitaCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (observer != null && prenotazione != null) {
                observer.onAssistenzaChanged(prenotazione, v, "cecita", newVal);
            } else {
                v.setCecita(newVal);
            }
        });

        Label cecitaPrice = new Label("EUR 25.00");
        cecitaPrice.getStyleClass().add("package-meta");

        HBox spacer2 = new HBox();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        HBox cecitaRow = new HBox(10, cecitaCheck, spacer2, cecitaPrice);
        cecitaRow.setAlignment(Pos.CENTER_LEFT);

        // --- CARD COMPLETA ---
        VBox card = new VBox(10, traveler, sediaRow, cecitaRow);
        card.getStyleClass().add("package-card");
        card.setPadding(new Insets(14, 16, 14, 16));

        return card;
    }

    private void openPagamentoView(float prezzoAssistenzaSpeciale) {
        Stage currentStage = (Stage) root.getScene().getWindow();
        currentStage.close();

        Stage stage = new Stage();

        PagamentoView pagamentoView = new PagamentoView(te, prenotazione, prenotazione.getPacchetto(), this.conn, prezzoAssistenzaSpeciale);

        Scene scene = new Scene(pagamentoView.getRoot(), 740, 600);
        scene.getStylesheets().add(App.class.getResource(App.STYLESHEET).toExternalForm());

        stage.setTitle("Travel Easy - Pagamento");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

}

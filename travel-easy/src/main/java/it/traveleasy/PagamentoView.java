package it.traveleasy;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
//import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.Scene;
import java.sql.Connection;


public class PagamentoView {

    private final StackPane root;
    private TravelEasy te;
    private Connection conn;
    private int idUtente;

    public PagamentoView(TravelEasy te, Connection conn, int idUtente) {
        this.te = te;
        this.conn = conn;
        this.idUtente = idUtente;
        root = new StackPane();
        root.getStyleClass().add("payment-root");
        root.getChildren().add(buildCard());
    }

    public StackPane getRoot() {
        return root;
    }

    private HBox buildHeader() {
        Label title = new Label("Travel Easy");
        title.getStyleClass().add("app-title");

        HBox header = new HBox(title);
        header.getStyleClass().add("top-bar");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(16, 40, 16, 40));
        return header;
    }

    private VBox buildContent() {
        Label sectionTitle = new Label("Pagamento");
        sectionTitle.getStyleClass().add("section-title");

        Label totalLabel = new Label("Totale dovuto: € 0.00");
        totalLabel.getStyleClass().add("package-price");

        Label balanceLabel = new Label("Saldo attuale portafoglio: € 0.00");
        balanceLabel.getStyleClass().add("package-meta");

        Button confirmButton = new Button("Conferma pagamento");
        confirmButton.getStyleClass().add("primary-button");
        confirmButton.setOnAction(e -> {
        });

        Button rechargeButton = new Button("Nuova ricarica portafoglio");
        rechargeButton.getStyleClass().add("secondary-button");
        rechargeButton.setOnAction(e -> { 
            openRechargeWindow();
        });

        VBox content = new VBox(16, sectionTitle, totalLabel, balanceLabel, confirmButton, rechargeButton);
        content.setAlignment(Pos.CENTER_LEFT);
        return content;
    }

    private VBox buildCard() {
        VBox card = new VBox(20, buildHeader(), buildContent());
        card.getStyleClass().add("payment-card");
        card.setPadding(new Insets(28, 32, 32, 32));
        card.setMaxWidth(520);
        card.setAlignment(Pos.TOP_LEFT);
        return card;
    }

    private void openRechargeWindow() {
        Stage stage = new Stage();
        RicaricaView view = new RicaricaView(te, conn, idUtente);
        Scene scene = new Scene(view.getRoot(), 520, 460);
        scene.getStylesheets().add(App.class.getResource(App.STYLESHEET).toExternalForm());
        stage.setTitle("Travel Easy - Ricarica");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
}

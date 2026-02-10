package it.traveleasy;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.sql.Connection;

public class PacchettoVacanzaCard {

    private final VBox root;
    private TravelEasy te;
    private Connection conn;
    private int idUtente;
    private float discountPercent;

    public PacchettoVacanzaCard(PacchettoViaggio pacchetto, TravelEasy te, Connection conn, int idUtente) {
        this(pacchetto, te, conn, idUtente, 0, 0);
    }

    public PacchettoVacanzaCard(PacchettoViaggio pacchetto, TravelEasy te, Connection conn, int idUtente, float discountPercent, float prezzoScontato) {
        this.te = te;
        this.conn = conn;
        this.idUtente = idUtente;
        this.discountPercent = discountPercent;

        Label title = new Label(pacchetto.getTitolo());
        title.getStyleClass().add("package-title");

        Label code = new Label("Codice: " + pacchetto.getCodice());
        code.getStyleClass().add("package-meta");

        String citta = pacchetto.getCittà();
        String nazione = pacchetto.getNazione();

        Label destination = new Label(citta+", "+nazione);
        destination.getStyleClass().add("package-destination");

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate dataPartenza = LocalDate.parse(pacchetto.getDataPartenza(), dateFormat);
        LocalDate dataRitorno = LocalDate.parse(pacchetto.getDataRitorno(), dateFormat);

        int durataGiorni = (int)ChronoUnit.DAYS.between(dataPartenza, dataRitorno);


        Label duration = new Label(durataGiorni + " giorni");
        duration.getStyleClass().add("package-meta");

        Label description = new Label(pacchetto.getDescrizione());
        description.getStyleClass().add("package-description");
        description.setWrapText(true);

        VBox priceBox = buildPriceBox(pacchetto.getPrezzo(), prezzoScontato);

        Button detailsButton = new Button("Dettagli");
        detailsButton.getStyleClass().add("secondary-button");
        detailsButton.setOnAction(e -> {
            openPackageDetails(pacchetto, te);
        });

        Button bookButton = new Button("Prenota");
        bookButton.getStyleClass().add("primary-button");
        bookButton.setOnAction(e -> openBookingDialog());

        VBox info = new VBox(6, title, code, destination, duration, description);
        info.setAlignment(Pos.CENTER_LEFT);

        VBox actions = new VBox(10, priceBox, detailsButton, bookButton);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.setPrefWidth(140);

        HBox content = new HBox(20, info, actions);
        content.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(info, Priority.ALWAYS);

        root = new VBox(content);
        root.getStyleClass().add("package-card");
        root.setPadding(new Insets(16, 20, 16, 20));
    }

    public VBox getRoot() {
        return root;
    }

    private VBox buildPriceBox(float prezzo, float prezzoScontato) {
        VBox box = new VBox(4);

        if (discountPercent > 0) {
            float original = prezzo;
            
            float savings = original - prezzoScontato;

            Label discountLabel = new Label("-" + discountPercent + "%");
            discountLabel.getStyleClass().add("package-meta");

            Label originalPrice = new Label(String.format("EUR %.2f", original));
            originalPrice.getStyleClass().add("package-meta");

            Label discountedPrice = new Label(String.format("EUR %.2f", prezzoScontato));
            discountedPrice.getStyleClass().add("package-price");

            Label savingsLabel = new Label(String.format("Risparmi EUR %.2f", savings));
            savingsLabel.getStyleClass().add("package-meta");

            box.getChildren().addAll(discountLabel, originalPrice, discountedPrice, savingsLabel);
        } else {
            Label price = new Label(String.format("EUR %.2f", prezzo));
            price.getStyleClass().add("package-price");
            box.getChildren().add(price);
        }

        box.setAlignment(Pos.CENTER_RIGHT);
        return box;
    }

    private void openBookingDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Prenotazione");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStyleClass().add("booking-dialog");
        dialogPane.setPrefWidth(720);
        dialogPane.setPrefHeight(600);

        VBox content = new VBox(12);
        content.setPadding(new Insets(16));
        content.getStyleClass().add("booking-content");

        Label prompt = new Label("Quante persone sono interessate?");
        prompt.getStyleClass().add("section-title");

        TextField peopleField = new TextField();
        peopleField.setPromptText("Numero persone");
        peopleField.getStyleClass().add("input");

        VBox peopleForm = new VBox(12);

        Button generateButton = new Button("Genera campi");
        generateButton.getStyleClass().add("secondary-button");
        generateButton.setOnAction(e -> {
            peopleForm.getChildren().clear();

            int count;
            try {
                count = Integer.parseInt(peopleField.getText());
            } catch (NumberFormatException ex) {
                return;
            }

            if (count <= 0) {
                return;
            }

            for (int i = 1; i <= count; i++) {
                VBox personCard = new VBox(8);
                personCard.getStyleClass().add("package-card");
                personCard.setPadding(new Insets(12));

                Label personTitle = new Label("Persona " + i);
                personTitle.getStyleClass().add("package-title");

                TextField nameField = new TextField();
                nameField.setPromptText("Nome");
                nameField.getStyleClass().add("input");

                TextField surnameField = new TextField();
                surnameField.setPromptText("Cognome");
                surnameField.getStyleClass().add("input");

                DatePicker birthDateField = new DatePicker();
                birthDateField.setPromptText("Data di nascita");
                birthDateField.getStyleClass().add("date-picker");

                TextField docField = new TextField();
                docField.setPromptText("Documento (codice)");
                docField.getStyleClass().add("input");

                personCard.getChildren().addAll(
                    personTitle,
                    nameField,
                    surnameField,
                    birthDateField,
                    docField
                );

                peopleForm.getChildren().add(personCard);
            }
        });

        Button confirmButton = new Button("Conferma dati");
        confirmButton.getStyleClass().add("primary-button");
        confirmButton.setOnAction(e -> {
            Stage dialogStage = (Stage) dialogPane.getScene().getWindow();
            dialogStage.close();
            openPaymentWindow();
        });

        ScrollPane scrollPane = new ScrollPane(peopleForm);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("package-scroll");

        content.getChildren().addAll(prompt, peopleField, generateButton, scrollPane, confirmButton);

        dialogPane.setContent(content);
        dialogPane.getButtonTypes().setAll(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private void openPaymentWindow() {
        Stage stage = new Stage();
        PagamentoView view = new PagamentoView(te, conn, idUtente);
        Scene scene = new Scene(view.getRoot(), App.WIDTH, App.HEIGHT);
        scene.getStylesheets().add(App.class.getResource(App.STYLESHEET).toExternalForm());
        stage.setTitle("Travel Easy - Pagamento");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private void openPackageDetails(PacchettoViaggio pacchetto, TravelEasy te) {
        Stage stage = new Stage();
        PacchettoView view = new PacchettoView(pacchetto, te);
        Scene scene = new Scene(view.getRoot(), 720, 640);
        scene.getStylesheets().add(App.class.getResource(App.STYLESHEET).toExternalForm());
        stage.setTitle("Travel Easy - Dettagli Pacchetto");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
}


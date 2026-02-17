package it.traveleasy;

import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ModuloAssistenzaSpecialeView {
    private final VBox root;
    private final List<Viaggiatore> viaggiatori;
    private final ConfirmHandler confirmHandler;

    public interface ConfirmHandler {
        void onConferma(List<Viaggiatore> viaggiatori);
    }

    public ModuloAssistenzaSpecialeView(List<Viaggiatore> viaggiatori, ConfirmHandler confirmHandler) {
        this.viaggiatori = viaggiatori;
        this.confirmHandler = confirmHandler;
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

        for (Viaggiatore v : viaggiatori) {
            list.getChildren().add(buildViaggiatoreCard(v));
        }

        ScrollPane scrollPane = new ScrollPane(list);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("package-scroll");

        Button confirmButton = new Button("Conferma");
        confirmButton.getStyleClass().add("primary-button");
        confirmButton.setOnAction(e -> {
            if (confirmHandler != null) {
                confirmHandler.onConferma(viaggiatori);
            }
        });

        content.getChildren().addAll(title, subtitle, scrollPane, confirmButton);
        return content;
    }

    private VBox buildViaggiatoreCard(Viaggiatore v) {
        Label traveler = new Label(v.getNome() + " " + v.getCognome());
        traveler.getStyleClass().add("package-title");

        HBox sediaRow = buildAssistenzaRow("Sedia rotelle", "EUR 35.00");
        HBox cecitaRow = buildAssistenzaRow("Cecità", "EUR 25.00");

        VBox card = new VBox(10, traveler, sediaRow, cecitaRow);
        card.getStyleClass().add("package-card");
        card.setPadding(new Insets(14, 16, 14, 16));
        return card;
    }

    private HBox buildAssistenzaRow(String label, String prezzo) {
        CheckBox checkBox = new CheckBox(label);
        checkBox.getStyleClass().add("package-description");

        Label price = new Label(prezzo);
        price.getStyleClass().add("package-meta");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox row = new HBox(10, checkBox, spacer, price);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }
}

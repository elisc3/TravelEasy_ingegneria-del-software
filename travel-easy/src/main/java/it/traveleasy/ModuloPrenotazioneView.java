package it.traveleasy;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class ModuloPrenotazioneView {
    private final VBox root;
    private final DialogCloseHandler closeHandler;

    public interface DialogCloseHandler {
        void onConferma();
    }

    public ModuloPrenotazioneView(DialogCloseHandler closeHandler) {
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
            if (closeHandler != null) {
                closeHandler.onConferma();
            }
        });

        ScrollPane scrollPane = new ScrollPane(peopleForm);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("package-scroll");

        content.getChildren().addAll(prompt, peopleField, generateButton, scrollPane, confirmButton);
        return content;
    }
}

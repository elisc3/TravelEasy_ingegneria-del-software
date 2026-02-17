package it.traveleasy;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ModuloPrenotazioneView {
    private final VBox root;
    private final DialogCloseHandler closeHandler;
    private final TravelEasy te;
    private final List<VBox> listaPersonCards;
    private final List<Viaggiatore> listaViaggiatori;

    public interface DialogCloseHandler {
        void onConferma(List<Viaggiatore> viaggiatori);
    }

    public ModuloPrenotazioneView(DialogCloseHandler closeHandler, PacchettoViaggio pacchetto, TravelEasy te) {
        this.closeHandler = closeHandler;
        this.te = te;
        this.listaViaggiatori = new ArrayList<>();
        this.listaPersonCards = new ArrayList<>();
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
            listaPersonCards.clear();
            listaViaggiatori.clear();

            int count;
            try {
                count = Integer.parseInt(peopleField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Numero persone inserito non valido.", "ERRORE", 0);
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

                ComboBox<String> docTypeField = new ComboBox<>();
                docTypeField.getItems().addAll("Carta d'identità", "Patente di guida", "Passaporto");
                docTypeField.setPromptText("Tipo di documento");
                docTypeField.getStyleClass().add("input");

                TextField docField = new TextField();
                docField.setPromptText("Documento (codice)");
                docField.getStyleClass().add("input");

                personCard.getChildren().addAll(
                    personTitle,
                    nameField,
                    surnameField,
                    birthDateField,
                    docTypeField,
                    docField
                );

                peopleForm.getChildren().add(personCard);
                listaPersonCards.add(personCard);
            }
        });

        Button confirmButton = new Button("Conferma dati");
        confirmButton.getStyleClass().add("primary-button");
        confirmButton.setOnAction(e -> {
            listaViaggiatori.clear();

            for (VBox card : listaPersonCards) {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-uuuu");
                DatePicker birthDate = (DatePicker) card.getChildren().get(3);
                LocalDate d = birthDate.getValue();
                String data = (d == null) ? "" : d.format(fmt);

                TextField nameField = (TextField) card.getChildren().get(1);
                TextField surnameField = (TextField) card.getChildren().get(2);

                @SuppressWarnings("unchecked")
                ComboBox<String> docTypeField = (ComboBox<String>) card.getChildren().get(4);
                TextField docField = (TextField) card.getChildren().get(5);

                Viaggiatore v = new Viaggiatore(
                    nameField.getText(),
                    surnameField.getText(),
                    data,
                    docTypeField.getValue(),
                    docField.getText()
                );
                te.inserisciDatiViaggiatore(listaViaggiatori, v);
            }

            for (int i = 0; i < listaViaggiatori.size(); i++) {
                Viaggiatore v = listaViaggiatori.get(i);
                int esitoValidazioneDati = v.validazioneDatiPrenotazione(v);
                int pos = i + 1;
                if (esitoValidazioneDati == -1) {
                    JOptionPane.showMessageDialog(null, "Hai dimenticato qualche campo in persona " + pos + ".", "ATTENZIONE", 2);
                    return;
                } else if (esitoValidazioneDati == -2) {
                    JOptionPane.showMessageDialog(null, "Data inserita non valida in persona " + pos + ".", "ERRORE", 0);
                    return;
                } else if (esitoValidazioneDati == -3) {
                    JOptionPane.showMessageDialog(null, "Codice del documento non valido in persona " + pos + ".", "ERRORE", 0);
                    return;
                } else if (esitoValidazioneDati == -4) {
                    JOptionPane.showMessageDialog(null, "Codice del documento non valido in persona " + pos + ".", "ERRORE", 0);
                    return;
                } else if (esitoValidazioneDati == -5) {
                    JOptionPane.showMessageDialog(null, "Codice del documento non valido in persona " + pos + ".", "ERRORE", 0);
                    return;
                }
            }

            openAssistenzaSpecialeWindow();
        });

        ScrollPane scrollPane = new ScrollPane(peopleForm);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("package-scroll");

        content.getChildren().addAll(prompt, peopleField, generateButton, scrollPane, confirmButton);
        return content;
    }

    private void openAssistenzaSpecialeWindow() {
        Stage stage = new Stage();
        ModuloAssistenzaSpecialeView view = new ModuloAssistenzaSpecialeView(listaViaggiatori, viaggiatori -> {
            stage.close();
            if (closeHandler != null) {
                closeHandler.onConferma(viaggiatori);
            }
        });
        Scene scene = new Scene(view.getRoot(), 740, 600);
        scene.getStylesheets().add(App.class.getResource(App.STYLESHEET).toExternalForm());
        stage.setTitle("Travel Easy - Assistenza Speciale");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
}

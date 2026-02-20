package it.traveleasy;

import java.time.format.DateTimeFormatter;

import javax.swing.JOptionPane;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ModuloOffertaView {
    private final VBox root;
    private final Runnable onOffertaInserita;

    public ModuloOffertaView(TravelEasy te, PacchettoViaggio p) {
        this(te, p, null);
    }

    public ModuloOffertaView(TravelEasy te, PacchettoViaggio p, Runnable onOffertaInserita) {
        this.onOffertaInserita = onOffertaInserita;
        Label sectionTitle = new Label("Inserisci offerta");
        sectionTitle.getStyleClass().add("section-title");

        TextField percentualeField = new TextField();
        percentualeField.setPromptText("Percentuale di sconto");
        percentualeField.getStyleClass().add("input");

        DatePicker dataFine = new DatePicker();
        dataFine.setPromptText("Data fine validità");
        dataFine.getStyleClass().add("date-picker");

        TextField maxPacchettiField = new TextField();
        maxPacchettiField.setPromptText("Numero massimo pacchetti");
        maxPacchettiField.getStyleClass().add("input");

        HBox row = new HBox(12, dataFine, maxPacchettiField);
        row.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(dataFine, Priority.ALWAYS);
        HBox.setHgrow(maxPacchettiField, Priority.ALWAYS);

        Button saveButton = new Button("Salva");
        saveButton.getStyleClass().add("primary-button");
        saveButton.setMaxWidth(Double.MAX_VALUE);
        saveButton.setOnAction(e -> {
            OffertaSpeciale offertaExists = te.getOffertaByPack(p);
            if (offertaExists != null){
                JOptionPane.showMessageDialog(null, "Questo pacchetto ha già una offerta inserita.", "ATTENZIONE", 2);
                return;
            }

            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String percentuale = percentualeField.getText();
            String dataFineValue = dataFine.getValue() == null ? "" : dataFine.getValue().format(dateFormat);
            String maxPacchetti = maxPacchettiField.getText();

            float percentualeF = p.validazionePercentunaleNuovaOfferta(percentuale);
            if (percentualeF == -1.0F){
                JOptionPane.showMessageDialog(null, "Il campo percentuale non può essere vuoto.", "ATTENZIONE", 2);
                return;
            } else if (percentualeF == -2.0F){
                JOptionPane.showMessageDialog(null, "La percentuale deve essere compresa fra 0 e 100", "ATTENZIONE", 2);
                return;
            } else if (percentualeF == -3.0F){
                JOptionPane.showMessageDialog(null, "Formato percentuale non valido.", "ERRORE", 0);
                return;
            }

            if (!p.validazioneDataInserimentoOfferta(dataFineValue, p.getDataPartenza())){
                JOptionPane.showMessageDialog(null, "Data inserita non valida.", "ERRORE", 0);
                return;
            }

            int maxPacchettiI = p.validazioneNumeroPacchettiNuovaOfferta(maxPacchetti);
            if (maxPacchettiI == -1){
                JOptionPane.showMessageDialog(null, "Il campo Numero Massimo Pacchetti non può essere vuoto.", "ATTENZIONE", 2);
                return;
            } else if (maxPacchettiI == -2){
                JOptionPane.showMessageDialog(null, "Il campo Numero Massimo Pacchetti deve essere maggiore o uguale di 0.", "ATTENZIONE", 2);
                return;
            } else if (maxPacchettiI == -3.0F){
                JOptionPane.showMessageDialog(null, "Formato Numero Massimo Pacchetti non valido.", "ERRORE", 0);
                return;
            }

            OffertaSpeciale newOffertaSpeciale = p.createNuovaOfferta(percentualeF, dataFineValue, maxPacchettiI);

            if (newOffertaSpeciale == null){
                JOptionPane.showMessageDialog(null, "Inserimento nuova offerta fallito!", "ERRORE", 0);
            } else {
                te.aggiornaOfferte(newOffertaSpeciale);
                if (this.onOffertaInserita != null) {
                    this.onOffertaInserita.run();
                }
                JOptionPane.showMessageDialog(null, "Inserimento nuova offerta avvenuto con successo!", "INFO", 1);
            }
        });

        VBox form = new VBox(12, percentualeField, row, saveButton);
        root = new VBox(12, sectionTitle, form);
        root.getStyleClass().add("search-card");
        root.setPadding(new Insets(20));
    }

    public VBox getRoot() {
        return root;
    }
}

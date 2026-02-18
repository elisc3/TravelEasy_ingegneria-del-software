package it.traveleasy;

import javax.swing.JOptionPane;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ModuloRecensioniView {
    private final ScrollPane root;
    private int nRiferimenti = 3;

    public ModuloRecensioniView(TravelEasy te, int idCliente, Prenotazione prenotazione) {
        this(te, idCliente, prenotazione, null, false);
    }

    public ModuloRecensioniView(TravelEasy te, int idCliente, Prenotazione prenotazione, Recensione[] recensione, boolean solaLettura) {
        Cliente cliente = te.getClienteById(idCliente);

        VBox content = new VBox(16);
        content.setPadding(new Insets(24, 24, 32, 24));

        Label title = new Label("Inserisci recensioni");
        title.getStyleClass().add("section-title");

        Label subtitle = new Label("Compila una recensione per ciascuna sezione.");
        subtitle.getStyleClass().add("package-meta");
        
        SectionControls sezioneAgenzia = buildSection("Recensione Agenzia");
        SectionControls sezioneAlloggio = buildSection("Recensione Alloggio");
        SectionControls sezioneTrasporto = buildSection("Recensione Compagnia Trasporto");

        VBox list = new VBox(14,
            sezioneAgenzia.section,
            sezioneAlloggio.section,
            sezioneTrasporto.section
        );
        list.getStyleClass().add("package-list");
        
        Button confermaButton = new Button("Conferma");
        confermaButton.getStyleClass().add("primary-button");
        
        Button annullaButton = new Button("Annulla");
        annullaButton.getStyleClass().add("secondary-button");
        annullaButton.setOnAction(e -> {
            Stage stage = (Stage) annullaButton.getScene().getWindow();
            stage.close();
        });

        if (solaLettura && recensione != null) {
            popolaRecensioneInSolaLettura(recensione, sezioneAgenzia, sezioneTrasporto, sezioneAlloggio);
            annullaButton.setText("Chiudi");
            confermaButton.setVisible(false);
            confermaButton.setManaged(false);
        } else {
            confermaButton.setDisable(false);
            confermaButton.setOnAction(e -> {
                int stelleAgenzia = sezioneAgenzia.stelleSelector.getValue();
                String commentoAgenzia = sezioneAgenzia.commentArea.getText();
                int stelleTrasporto = sezioneTrasporto.stelleSelector.getValue();
                String commentoTrasporto = sezioneTrasporto.commentArea.getText();
                int stelleAlloggio = sezioneAlloggio.stelleSelector.getValue();
                String commentoAlloggio = sezioneAlloggio.commentArea.getText();

                if (!te.validaDatiNuovaRecensione(commentoAgenzia, commentoTrasporto, commentoAlloggio)){
                    JOptionPane.showMessageDialog(null, "Hai dimenticato qualche campo.", "ATTENZIONE", 2);
                    return;
                }
                
                if (!te.inserisciRecensione(cliente, prenotazione, commentoAgenzia, stelleAgenzia, "Agenzia")){
                    JOptionPane.showMessageDialog(null, "Impossibile effettuare l'inserimento. Prego riprovare.", "ERRORE", 0);
                    return;
                }

                if (!te.inserisciRecensione(cliente, prenotazione, commentoTrasporto, stelleTrasporto, "Trasporto")){
                    JOptionPane.showMessageDialog(null, "Impossibile effettuare l'inserimento. Prego riprovare.", "ERRORE", 0);
                    return;
                }

                if (!te.inserisciRecensione(cliente, prenotazione, commentoAlloggio, stelleAlloggio, "Alloggio")){
                    JOptionPane.showMessageDialog(null, "Impossibile effettuare l'inserimento. Prego riprovare.", "ERRORE", 0);
                    return;
                }

                JOptionPane.showMessageDialog(null, "Recensione inserita con successo.", "INFO", 1);
            });
        }


        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox actions = new HBox(10, spacer, annullaButton, confermaButton);
        actions.setAlignment(Pos.CENTER_LEFT);

        content.getChildren().addAll(title, subtitle, list, actions);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("package-scroll");
        root = scrollPane;
    }

    public ScrollPane getRoot() {
        return root;
    }

    private void popolaRecensioneInSolaLettura(Recensione[] recensione, SectionControls sezioneAgenzia, SectionControls sezioneTrasporto, SectionControls sezioneAlloggio) {
        for (int i = 0; i < nRiferimenti; i++){
            SectionControls target = null;
            String riferimento = recensione[i].getRiferimento();
            if ("Agenzia".equalsIgnoreCase(riferimento)) {
                target = sezioneAgenzia;
            } else if ("Trasporto".equalsIgnoreCase(riferimento)) {
                target = sezioneTrasporto;
            } else if ("Alloggio".equalsIgnoreCase(riferimento)) {
                target = sezioneAlloggio;
            }

            if (target != null) {
                target.stelleSelector.getValueFactory().setValue(recensione[i].getStelle());
                target.commentArea.setText(recensione[i].getCommento());
            }

            setSectionReadOnly(sezioneAgenzia);
            setSectionReadOnly(sezioneTrasporto);
            setSectionReadOnly(sezioneAlloggio);
            }
    }

    private void setSectionReadOnly(SectionControls section) {
        section.stelleSelector.setDisable(true);
        section.commentArea.setEditable(false);
        section.commentArea.setFocusTraversable(false);
    }

    private SectionControls buildSection(String sectionTitleText) {
        Label sectionTitle = new Label(sectionTitleText);
        sectionTitle.getStyleClass().add("package-title");

        Label starsLabel = new Label("Stelle (1-5):");
        starsLabel.getStyleClass().add("package-meta");

        Spinner<Integer> stelleSelector = new Spinner<>(1, 5, 1);
        stelleSelector.setEditable(false);
        stelleSelector.setMaxWidth(100);
        stelleSelector.getStyleClass().add("date-picker");

        HBox starsRow = new HBox(10, starsLabel, stelleSelector);
        starsRow.setAlignment(Pos.CENTER_LEFT);

        Label commentLabel = new Label("Commento:");
        commentLabel.getStyleClass().add("package-meta");

        TextArea commentArea = new TextArea();
        commentArea.setPromptText("Scrivi qui la tua recensione...");
        commentArea.setPrefRowCount(4);
        commentArea.getStyleClass().add("input");

        VBox section = new VBox(10, sectionTitle, starsRow, commentLabel, commentArea);
        section.getStyleClass().add("package-card");
        section.setPadding(new Insets(16, 18, 16, 18));
        return new SectionControls(section, commentArea, stelleSelector);
    }

    private static class SectionControls {
        private final VBox section;
        private final TextArea commentArea;
        private final Spinner<Integer> stelleSelector;

        private SectionControls(VBox section, TextArea commentArea, Spinner<Integer> stelleSelector) {
            this.section = section;
            this.commentArea = commentArea;
            this.stelleSelector = stelleSelector;
        }
    }
}

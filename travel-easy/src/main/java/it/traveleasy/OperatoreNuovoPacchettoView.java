package it.traveleasy;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.net.ConnectException;
import java.time.format.DateTimeFormatter;
import java.sql.Connection;

import javax.swing.JOptionPane;
import java.util.Map;

public class OperatoreNuovoPacchettoView {
    private final VBox root;
    private Connection conn;
    private TravelEasy te;

    public OperatoreNuovoPacchettoView(Connection conn, TravelEasy te) {
        Label sectionTitle = new Label("Inserisci un nuovo pacchetto viaggio");
        sectionTitle.getStyleClass().add("section-title");

        TextField titoloField = new TextField();
        titoloField.setPromptText("Titolo");
        titoloField.getStyleClass().add("input");

        TextField codiceField = new TextField();
        codiceField.setPromptText("Codice pacchetto");
        codiceField.getStyleClass().add("input");

        TextField cittaField = new TextField();
        cittaField.setPromptText("Città");
        cittaField.getStyleClass().add("input");

        TextField nazioneField = new TextField();
        nazioneField.setPromptText("Nazione");
        nazioneField.getStyleClass().add("input");

        DatePicker dataAndata = new DatePicker();
        dataAndata.setPromptText("Data andata");
        dataAndata.getStyleClass().add("date-picker");

        DatePicker dataRitorno = new DatePicker();
        dataRitorno.setPromptText("Data ritorno");
        dataRitorno.getStyleClass().add("date-picker");

        HBox dateRow = new HBox(12, dataAndata, dataRitorno);
        dateRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(dataAndata, Priority.ALWAYS);
        HBox.setHgrow(dataRitorno, Priority.ALWAYS);

        TextArea descrizioneField = new TextArea();
        descrizioneField.setPromptText("Descrizione");
        descrizioneField.getStyleClass().add("input");
        descrizioneField.setWrapText(true);
        descrizioneField.setPrefRowCount(4);

        CheckBox visibilitaCheck = new CheckBox("Visibile");

        TextField prezzoField = new TextField();
        prezzoField.setPromptText("Prezzo");
        prezzoField.getStyleClass().add("input");

        ComboBox<String> compagniaCombo = new ComboBox<>();
        compagniaCombo.setPromptText("Compagnia di trasporto");

        Map<Integer, CompagniaTrasporto> elencoCompagnie = te.recuperaCompagnie();
        for (CompagniaTrasporto c : elencoCompagnie.values())
            compagniaCombo.getItems().add(c.getNome());

        //compagniaCombo.getItems().addAll("Trenitalia", "SkyWays", "BusLine");
        compagniaCombo.getStyleClass().add("input");

        ComboBox<String> alloggioCombo = new ComboBox<>();
        alloggioCombo.setPromptText("Alloggio");

        Map<Integer, Alloggio> elencoAlloggi = te.recuperaAlloggi();
        for (Alloggio a : elencoAlloggi.values())
            alloggioCombo.getItems().add(a.getNome());
        

        //alloggioCombo.getItems().addAll("Hotel Marina Palace", "B&B Centro", "Resort Blu");
        alloggioCombo.getStyleClass().add("input");

        HBox comboRow = new HBox(12, compagniaCombo, alloggioCombo);
        comboRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(compagniaCombo, Priority.ALWAYS);
        HBox.setHgrow(alloggioCombo, Priority.ALWAYS);

        Button saveButton = new Button("Salva");
        saveButton.getStyleClass().add("primary-button");
        saveButton.setMaxWidth(Double.MAX_VALUE);
        saveButton.setOnAction(e -> {
            String codice = codiceField.getText();
            String titolo = titoloField.getText();
            String citta = cittaField.getText();
            String nazione = nazioneField.getText();
            String descrizione = descrizioneField.getText();
            boolean visibile = visibilitaCheck.isSelected();
            int visibilità;
            if (visibile)
                visibilità = 1;
            else
                visibilità = 0;
            String prezzo = prezzoField.getText();
            String compagnia = compagniaCombo.getValue();
            String alloggio = alloggioCombo.getValue();
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String dataPartenzaValue = dataAndata.getValue() == null ? "" : dataAndata.getValue().format(dateFormat);
            String dataRitornoValue = dataRitorno.getValue() == null ? "" : dataRitorno.getValue().format(dateFormat);

            if (codice == null || codice.isBlank()) {
                JOptionPane.showMessageDialog(null, "Hai dimenticato il codice pacchetto", "ATTENZIONE", 2);
                return;
            }

            float prezzoF = te.validazioneDatiNuovoPacchetto(titolo, citta, nazione, descrizione, prezzo, compagnia, alloggio, dataPartenzaValue, dataRitornoValue);
            if (prezzoF == 0.0F){
                JOptionPane.showMessageDialog(null, "Prezzo inserito non valido", "ERRORE", 0);
                return;
            }
            if (prezzoF == -1.0F){
                JOptionPane.showMessageDialog(null, "Hai dimenticato qualche campo", "ATTENZIONE", 2);
                return;
            }

            

            if (te.nuovoPacchetto(conn, codice, titolo, citta, nazione, descrizione, prezzoF, visibilità, compagnia, alloggio, dataPartenzaValue, dataRitornoValue))
                JOptionPane.showMessageDialog(null, "Nuovo pacchetto inserito con successo!", "INFO", 1);



            /*System.out.println("Salva pacchetto:");
            System.out.println("Titolo=" + titolo);
            System.out.println("Citta=" + citta);
            System.out.println("Nazione=" + nazione);
            System.out.println("DataAndata=" + dataPartenza);
            System.out.println("DataRitorno=" + dataRitornoValue);
            System.out.println("Descrizione=" + descrizione);
            System.out.println("Visibile=" + visibile);
            System.out.println("Prezzo=" + prezzo);
            System.out.println("Compagnia=" + compagnia);
            System.out.println("Alloggio=" + alloggio);*/
        });

        VBox form = new VBox(
            12,
            titoloField,
            codiceField,
            cittaField,
            nazioneField,
            dateRow,
            descrizioneField,
            visibilitaCheck,
            prezzoField,
            comboRow,
            saveButton
        );

        root = new VBox(12, sectionTitle, form);
        root.getStyleClass().add("search-card");
        root.setPadding(new Insets(20));
    }

    

    public VBox getRoot() {
        return root;
    }
}

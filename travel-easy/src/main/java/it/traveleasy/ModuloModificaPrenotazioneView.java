package it.traveleasy;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.List;
import javax.swing.JOptionPane;

public class ModuloModificaPrenotazioneView {
    private final ScrollPane root;

    public ModuloModificaPrenotazioneView(Prenotazione prenotazione, TravelEasy te, Connection conn) {
        VBox content = new VBox(16);
        content.setPadding(new Insets(24, 24, 32, 24));

        Label title = new Label("Modifica prenotazione");
        title.getStyleClass().add("section-title");

        Label subtitle = new Label("Dati attuali prenotazione");
        subtitle.getStyleClass().add("package-title");

                
        PacchettoViaggio pacchetto = prenotazione.getPacchetto();
        CompagniaTrasporto c = pacchetto.getCompagniaTrasporto();
        Alloggio a = pacchetto.getAlloggio();

        DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MM-uuuu")
                .withResolverStyle(ResolverStyle.STRICT);

        LocalDate partenza = LocalDate.parse(pacchetto.getDataPartenza(), FMT);
        LocalDate ritorno  = LocalDate.parse(pacchetto.getDataRitorno(), FMT);

        DatePicker dataPartenzaField = new DatePicker(partenza);
        dataPartenzaField.getStyleClass().add("date-picker");

        DatePicker dataRitornoField = new DatePicker(ritorno);
        dataRitornoField.getStyleClass().add("date-picker");

        String citta = pacchetto.getCittà();
        VBox detailsCard = new VBox(
            8,
            buildRow("Titolo", pacchetto.getTitolo()),
            buildRow("Destinazione", citta+", "+pacchetto.getNazione()),
            buildRow("Descrizione", pacchetto.getDescrizione()),
            buildEditableRow("Data partenza", dataPartenzaField),
            buildEditableRow("Data ritorno", dataRitornoField),
            buildRow("Totale", "EUR "+prenotazione.getPrezzoTotale()),
            buildRow("CompagniaTrasporto", c.getTipo()+ ", " + c.getNome()),
            buildRow("Alloggio", a.getTipo() + ", "+a.getNome())
        );
        detailsCard.getStyleClass().add("package-card");
        detailsCard.setPadding(new Insets(16, 20, 16, 20));

        Button cercaPacchettiButton = new Button("Cerca pacchetti");
        cercaPacchettiButton.getStyleClass().add("primary-button");
        cercaPacchettiButton.setMaxWidth(Double.MAX_VALUE);

        VBox formWrapper = new VBox(12, subtitle, detailsCard, cercaPacchettiButton);

        VBox searchResultsWrapper = new VBox(12);
        searchResultsWrapper.setFillWidth(true);
        searchResultsWrapper.setVisible(false);
        searchResultsWrapper.setManaged(false);

        cercaPacchettiButton.setOnAction(e -> {
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String startDateText = dataPartenzaField.getValue() == null ? "" : dataPartenzaField.getValue().format(dateFormat);
            String endDateText = dataRitornoField.getValue() == null ? "" : dataRitornoField.getValue().format(dateFormat);

            if (!te.coerenzaDate(startDateText, endDateText)){
                JOptionPane.showMessageDialog(null, "Date inserite non valide. Prego ricontrollare.", "ERRORE", 0);
                return;
            }

            List<PacchettoViaggio> pacchetti = te.ricercaPacchetti(citta, startDateText, endDateText, Float.MAX_VALUE);
            if (pacchetti.size() == 0){
                JOptionPane.showMessageDialog(null, "Nessun pacchetto vacanza a disposizione con queste caratteristiche.", "ATTENZIONE", 2);
                return;
            } else {
                searchResultsWrapper.getChildren().clear();

                Label sectionTitle = new Label("Pacchetti consigliati secondo le vostre richieste:");
                sectionTitle.getStyleClass().add("section-title");

                VBox list = new VBox(16);
                list.getStyleClass().add("package-list");

                int idUtente = prenotazione.getCliente().getId();
                for (PacchettoViaggio pack : pacchetti) {
                    OffertaSpeciale o = te.getOffertaByPack(pack);
                    PacchettoVacanzaCard card;
                    if (o == null) {
                        card = new PacchettoVacanzaModificaCard(
                            pack,
                            te,
                            idUtente,
                            conn,
                            prenotazione,
                            () -> {
                                searchResultsWrapper.getChildren().clear();
                                searchResultsWrapper.setVisible(false);
                                searchResultsWrapper.setManaged(false);
                                formWrapper.setVisible(true);
                                formWrapper.setManaged(true);
                            }
                        );
                    } else {
                        card = new PacchettoVacanzaModificaCard(
                            pack,
                            te,
                            idUtente,
                            o.getScontoPercentuale(),
                            o.getPrezzoScontato(),
                            conn,
                            prenotazione,
                            () -> {
                                searchResultsWrapper.getChildren().clear();
                                searchResultsWrapper.setVisible(false);
                                searchResultsWrapper.setManaged(false);
                                formWrapper.setVisible(true);
                                formWrapper.setManaged(true);
                            }
                        );
                    }
                    list.getChildren().add(card.getRoot());
                }

                ScrollPane resultsScrollPane = new ScrollPane(list);
                resultsScrollPane.setFitToWidth(true);
                resultsScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                resultsScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                resultsScrollPane.getStyleClass().add("package-scroll");
                resultsScrollPane.setPrefViewportHeight(300);

                Button closeResultsButton = new Button("X");
                closeResultsButton.getStyleClass().add("secondary-button");
                closeResultsButton.setOnAction(ev -> {
                    searchResultsWrapper.getChildren().clear();
                    searchResultsWrapper.setVisible(false);
                    searchResultsWrapper.setManaged(false);
                    formWrapper.setVisible(true);
                    formWrapper.setManaged(true);
                });

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                HBox resultsHeader = new HBox(12, sectionTitle, spacer, closeResultsButton);
                resultsHeader.setAlignment(Pos.CENTER_LEFT);

                searchResultsWrapper.getChildren().addAll(resultsHeader, resultsScrollPane);
                formWrapper.setVisible(false);
                formWrapper.setManaged(false);
                searchResultsWrapper.setVisible(true);
                searchResultsWrapper.setManaged(true);
            }
            
        });

        content.getChildren().addAll(title, formWrapper, searchResultsWrapper);

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

    private HBox buildRow(String label, String value) {
        Label key = new Label(label + ":");
        key.getStyleClass().add("package-meta");

        Label val = new Label(value == null ? "" : value);
        val.getStyleClass().add("package-description");
        val.setWrapText(true);

        HBox row = new HBox(12, key, val);
        row.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(val, Priority.ALWAYS);
        return row;
    }

    private HBox buildEditableRow(String label, DatePicker field) {
        Label key = new Label(label + ":");
        key.getStyleClass().add("package-meta");

        HBox row = new HBox(12, key, field);
        row.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(field, Priority.ALWAYS);
        return row;
    }
}

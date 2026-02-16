package it.traveleasy;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class OperatorePrenotazioniView {
    private final VBox root;
    private final TravelEasy te;
    private List<Prenotazione> elencoPrenotazioni;

    public OperatorePrenotazioniView(List<Prenotazione> elencoPrenotazioni, TravelEasy te) {
        this.elencoPrenotazioni = elencoPrenotazioni;
        this.te = te;
        root = new VBox(12, buildHeader(), buildList());
    }

    public VBox getRoot() {
        return root;
    }

    private HBox buildHeader() {
        Label sectionTitle = new Label("Prenotazioni effettuate");
        sectionTitle.getStyleClass().add("section-title");

        HBox header = new HBox(12, sectionTitle);
        header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }

    private ScrollPane buildList() {
        VBox list = new VBox(16);
        list.getStyleClass().add("package-list");



        /*list.getChildren().addAll(
            buildPrenotazioneCard("Prenotazione #1024", "Roma, Italia", "15-04-2026 → 20-04-2026", "2 adulti, 1 bambino", "€ 780,00"),
            buildPrenotazioneCard("Prenotazione #1025", "Parigi, Francia", "02-05-2026 → 06-05-2026", "2 adulti", "€ 920,00"),
            buildPrenotazioneCard("Prenotazione #1026", "Lisbona, Portogallo", "18-06-2026 → 25-06-2026", "1 adulto", "€ 640,00")
        );*/

       for (Prenotazione p: elencoPrenotazioni){
            PacchettoViaggio pacchetto = p.getPacchetto();
            Cliente cliente = p.getCliente();
            list.getChildren().add(buildPrenotazioneCard("Prenotazione #"+pacchetto.getCodice(), pacchetto.getCittà()+", "+pacchetto.getNazione(), pacchetto.getDataPartenza() + " → "+pacchetto.getDataRitorno(), "Effettuata da: "+cliente.getNome() + " " + cliente.getCognome() + "\nNumero di telefono: "+cliente.getTelefono()+"\nData prenotazione: "+p.getDataPrenotazione(), "EUR "+p.getPrezzoTotale(), p));
        }
        

        ScrollPane scrollPane = new ScrollPane(list);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("package-scroll");
        return scrollPane;
    }

    private void openPackageWindow(Prenotazione p) {
        Stage stage = new Stage();
        PacchettoViaggio pacchetto = p.getPacchetto();
        OperatorePacchettoPrenotazioneView view = new OperatorePacchettoPrenotazioneView(pacchetto, te, p);
        Scene scene = new Scene(view.getRoot(), 720, 680);
        scene.getStylesheets().add(App.class.getResource(App.STYLESHEET).toExternalForm());
        stage.setTitle("Travel Easy - Dettagli Pacchetto Prenotazione");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private void openViaggiatoriWindow(Prenotazione prenotazione) {
        Stage stage = new Stage();
        ViaggiatoriView view = new ViaggiatoriView(prenotazione.getElencoViaggiatori());
        Scene scene = new Scene(view.getRoot(), 640, 560);
        scene.getStylesheets().add(App.class.getResource(App.STYLESHEET).toExternalForm());
        stage.setTitle("Travel Easy - Viaggiatori");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private VBox buildPrenotazioneCard(String titolo, String destinazione, String date, String partecipanti, String prezzo, Prenotazione prenotazione) {
        Label title = new Label(titolo);
        title.getStyleClass().add("package-title");

        Label destination = new Label(destinazione);
        destination.getStyleClass().add("package-destination");

        Label duration = new Label(date);
        duration.getStyleClass().add("package-meta");

        Label description = new Label(partecipanti);
        description.getStyleClass().add("package-description");
        description.setWrapText(true);

        Label price = new Label(prezzo);
        price.getStyleClass().add("package-price");

        Button detailsButton = new Button("Dettagli Pacchetto");
        detailsButton.getStyleClass().add("secondary-button");
        detailsButton.setOnAction(e -> openPackageWindow(prenotazione));

        Button viewButton = new Button("Visualizza Viaggiatori");
        viewButton.getStyleClass().add("primary-button");
        viewButton.setPrefWidth(180);
        viewButton.setOnAction(e -> openViaggiatoriWindow(prenotazione));

        VBox info = new VBox(6, title, destination, duration, description);
        info.setAlignment(Pos.CENTER_LEFT);

        VBox actions = new VBox(10, price, detailsButton, viewButton);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.setPrefWidth(190);

        HBox cardContent = new HBox(20, info, actions);
        cardContent.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(info, Priority.ALWAYS);

        VBox card = new VBox(cardContent);
        card.getStyleClass().add("package-card");
        card.setPadding(new Insets(16, 20, 16, 20));
        return card;
    }
}

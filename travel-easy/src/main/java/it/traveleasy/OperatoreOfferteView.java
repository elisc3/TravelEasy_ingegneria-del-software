package it.traveleasy;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

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

public class OperatoreOfferteView implements OffertaObserver  {
    private final VBox root;
    private TravelEasy te;
    private Map<Integer, PacchettoViaggio> elencoPacchetti;
    private final Map<Integer, Button> bottoneOffertaByPacchetto = new HashMap<>();
    private final Map<Integer, VBox> priceBoxByPacchetto = new HashMap<>();
    private final Map<Integer, PacchettoViaggio> pacchettoById = new HashMap<>();

    @Override
    public void onOffertaCreata(OffertaSpeciale offerta) {
        javafx.application.Platform.runLater(() -> aggiornaCardOfferta(offerta));
    }

    @Override
    public void onOffertaEliminata(PacchettoViaggio pacchetto) {
        javafx.application.Platform.runLater(() -> ripristinaCardOfferta(pacchetto));
    }



    public OperatoreOfferteView(TravelEasy te) {
        Label sectionTitle = new Label("Elenco pacchetti viaggio disponibili");
        sectionTitle.getStyleClass().add("section-title");
        this.te = te;
        this.te.addOffertaObserver(this);
        VBox list = new VBox(16);
        list.getStyleClass().add("package-list");

        elencoPacchetti = te.getElencoPacchetti();
        for (PacchettoViaggio p : elencoPacchetti.values()){
            if (p.isVisibilità() == 1){
                list.getChildren().add(buildPacchettoCard(p));
            }
        }

        ScrollPane scrollPane = new ScrollPane(list);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("package-scroll");

        root = new VBox(12, sectionTitle, scrollPane);
    }

    public VBox getRoot() {
        return root;
    }

    private VBox buildPacchettoCard(PacchettoViaggio p) {
        String destinazione = p.getCittà() + ", " + p.getNazione();
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate dataPartenza = LocalDate.parse(p.getDataPartenza(), dateFormat);
        LocalDate dataRitorno = LocalDate.parse(p.getDataRitorno(), dateFormat);

        int durataGiorni = (int)ChronoUnit.DAYS.between(dataPartenza, dataRitorno);
        String durata = String.valueOf(durataGiorni)+ " giorni.";

        OffertaSpeciale o = te.getOffertaByPack(p);
        float discountPercent = 0;
        float prezzoScontato = p.getPrezzo();
        if (o != null) {
            discountPercent = o.getScontoPercentuale();
            prezzoScontato = o.getPrezzoScontato();
        }

        Label title = new Label(p.getTitolo());
        title.getStyleClass().add("package-title");

        Label code = new Label("Codice: " + p.getCodice());
        code.getStyleClass().add("package-meta");

        Label destination = new Label(destinazione);
        destination.getStyleClass().add("package-destination");

        Label duration = new Label(durata);
        duration.getStyleClass().add("package-meta");

        Label description = new Label(p.getDescrizione());
        description.getStyleClass().add("package-description");
        description.setWrapText(true);

        VBox priceBox = buildPriceBox(p.getPrezzo(), discountPercent, prezzoScontato);

        Button detailsButton = new Button("Dettagli");
        detailsButton.getStyleClass().add("secondary-button");
        detailsButton.setOnAction(e -> {

            openPackageDetails(p, te);
        });

        Button viewButton = new Button("Inserisci offerta");
        viewButton.getStyleClass().add("primary-button");
        if (o != null) {
            viewButton.setDisable(true);
        } else {
            viewButton.setOnAction(e -> {
                openOffertaWindow(p);
            });
        }

        int idPacchetto = p.getId();
        bottoneOffertaByPacchetto.put(idPacchetto, viewButton);
        priceBoxByPacchetto.put(idPacchetto, priceBox);
        pacchettoById.put(idPacchetto, p);

        VBox info = new VBox(6, title, code, destination, duration, description);
        info.setAlignment(Pos.CENTER_LEFT);
        
        VBox actions = new VBox(10, priceBox, detailsButton, viewButton);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.setPrefWidth(140);

        HBox content = new HBox(20, info, actions);
        content.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(info, Priority.ALWAYS);

        VBox card = new VBox(content);
        card.getStyleClass().add("package-card");
        card.setPadding(new Insets(16, 20, 16, 20));
        return card;
    }

    private VBox buildPriceBox(float prezzo, float discountPercent, float discounted) {
        VBox box = new VBox(4);

        if (discountPercent > 0) {
            float original = prezzo;
            
            float savings = original - discounted;

            Label discountLabel = new Label("-" + discountPercent + "%");
            discountLabel.getStyleClass().add("package-meta");

            Label originalPrice = new Label(String.format("EUR %.2f", original));
            originalPrice.getStyleClass().add("package-meta");

            Label discountedPrice = new Label(String.format("EUR %.2f", discounted));
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

    private void openOffertaWindow(PacchettoViaggio p) {
        Stage stage = new Stage();
        ModuloOffertaView view = new ModuloOffertaView(te, p);
        Scene scene = new Scene(view.getRoot(), 520, 420);
        scene.getStylesheets().add(App.class.getResource(App.STYLESHEET).toExternalForm());
        stage.setTitle("Travel Easy - Inserisci Offerta");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private void aggiornaCardOfferta(OffertaSpeciale o) {
        int idPacchetto = o.getPacchetto().getId();
        Button btn = bottoneOffertaByPacchetto.get(idPacchetto);
        VBox priceBox = priceBoxByPacchetto.get(idPacchetto);
        PacchettoViaggio p = pacchettoById.get(idPacchetto);
        if (btn == null || priceBox == null || p == null) return;

        btn.setDisable(true);
        priceBox.getChildren().setAll(
            buildPriceBox(p.getPrezzo(), o.getScontoPercentuale(), o.getPrezzoScontato()).getChildren()
        );
    }

    private void ripristinaCardOfferta(PacchettoViaggio pacchetto) {
        if (pacchetto == null) return;
        int idPacchetto = pacchetto.getId();
        Button btn = bottoneOffertaByPacchetto.get(idPacchetto);
        VBox priceBox = priceBoxByPacchetto.get(idPacchetto);
        PacchettoViaggio p = pacchettoById.get(idPacchetto);
        if (btn == null || priceBox == null || p == null) return;

        btn.setDisable(false);
        btn.setOnAction(e -> openOffertaWindow(p));
        priceBox.getChildren().setAll(
            buildPriceBox(p.getPrezzo(), 0, p.getPrezzo()).getChildren()
        );
    }

    public void dispose() {
        te.removeOffertaObserver(this);
    }

    private void refresh() {
    elencoPacchetti = te.getElencoPacchetti();

    VBox newList = new VBox(16);
    newList.getStyleClass().add("package-list");

    bottoneOffertaByPacchetto.clear();
    priceBoxByPacchetto.clear();
    pacchettoById.clear();

    for (PacchettoViaggio p : elencoPacchetti.values()) {
        if (p.isVisibilità() == 1) {
            newList.getChildren().add(buildPacchettoCard(p));
        }
    }

    ScrollPane scrollPane = new ScrollPane(newList);
    scrollPane.setFitToWidth(true);
    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    scrollPane.getStyleClass().add("package-scroll");

    root.getChildren().setAll(
        new VBox(12, new Label("Elenco pacchetti viaggio disponibili"), scrollPane)
    );
}



}

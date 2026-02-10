package it.traveleasy;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.sql.Connection;



public class PacchettoView {
    private final ScrollPane root;
    private TravelEasy te;
    private PacchettoViaggio pacchetto;
    

    public PacchettoView(PacchettoViaggio pacchetto, TravelEasy te) {
        this.te = te;
        this.pacchetto = pacchetto;
        
        VBox content = new VBox(16);
        content.getChildren().addAll(
            buildHeader(),
            buildDetailsSection()
        );

        VBox offertaSection = buildOffertaSection();
        if (offertaSection != null) {
            content.getChildren().add(offertaSection);
        }

        content.getChildren().addAll(
            buildTransportSection(),
            buildAlloggioSection()
        );
        content.setPadding(new Insets(24, 24, 32, 24));

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

    private VBox buildHeader() {
        Label title = new Label("Dettagli pacchetto");
        title.getStyleClass().add("section-title");

        Label subtitle = new Label(pacchetto.getTitolo());
        subtitle.getStyleClass().add("package-title");

        VBox header = new VBox(6, title, subtitle);
        return header;
    }

    private VBox buildDetailsSection() {
        Label sectionTitle = new Label("Informazioni viaggio");
        sectionTitle.getStyleClass().add("section-title");

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate dataPartenza = LocalDate.parse(pacchetto.getDataPartenza(), dateFormat);
        LocalDate dataRitorno = LocalDate.parse(pacchetto.getDataRitorno(), dateFormat);
        long durataGiorni = ChronoUnit.DAYS.between(dataPartenza, dataRitorno);

        VBox info = new VBox(8,
            buildRow("Città", pacchetto.getCittà()),
            buildRow("Titolo", pacchetto.getTitolo()),
            buildRow("Codice", pacchetto.getCodice()),
            buildRow("Nazione", pacchetto.getNazione()),
            buildRow("Data partenza", pacchetto.getDataPartenza()),
            buildRow("Data ritorno", pacchetto.getDataRitorno()),
            buildRow("Durata", durataGiorni + " giorni"),
            buildRow("Descrizione", pacchetto.getDescrizione()),
            buildRow("Prezzo", String.format("€ %.2f", pacchetto.getPrezzo()))
        );
        info.getStyleClass().add("package-card");
        info.setPadding(new Insets(16, 20, 16, 20));

        VBox wrapper = new VBox(12, sectionTitle, info);
        return wrapper;
    }

    private VBox buildTransportSection() {
        Label sectionTitle = new Label("Compagnia di trasporto");
        sectionTitle.getStyleClass().add("section-title");

        CompagniaTrasporto c = te.getCompagniaTrasportoByPacchetto(pacchetto.getIdCompagniaTrasporto());

        VBox info = new VBox(8,
            buildRow("Nome", c.getNome()),
            buildRow("Tipo", c.getTipo())
        );
        info.getStyleClass().add("package-card");
        info.setPadding(new Insets(16, 20, 16, 20));

        return new VBox(12, sectionTitle, info);
    }

    private VBox buildAlloggioSection() {
        Label sectionTitle = new Label("Alloggio");
        sectionTitle.getStyleClass().add("section-title");

        Alloggio a = te.getAlloggioByPacchetto(pacchetto.getIdAlloggio());

        VBox info = new VBox(8,
            buildRow("Nome", a.getNome()),
            buildRow("Indirizzo", a.getIndirizzo()),
            buildRow("Tipo", a.getTipo()),
            buildRow("Stelle", String.valueOf(a.getStelle()))
        );
        info.getStyleClass().add("package-card");
        info.setPadding(new Insets(16, 20, 16, 20));

        return new VBox(12, sectionTitle, info);
    }

    private VBox buildOffertaSection() {
        OffertaSpeciale o = te.getOffertaByPack(pacchetto);
        if (o == null) {
            return null;
        }

        Label sectionTitle = new Label("Offerta speciale");
        sectionTitle.getStyleClass().add("section-title");

        float original = pacchetto.getPrezzo();
        float discounted = o.getPrezzoScontato();
        float savings = original - discounted;

        Label badge = new Label("-" + o.getScontoPercentuale() + "%");
        badge.getStyleClass().add("package-meta");
        badge.setStyle("-fx-background-color: #1d4ed8; -fx-text-fill: white; -fx-padding: 4 10; -fx-background-radius: 999; -fx-font-weight: bold;");

        String dataFinePretty = formatDataOfferta(o.getDataFine());
        Label slogan = new Label("Solo se prenoti entro il " + dataFinePretty);
        slogan.getStyleClass().add("package-description");
        slogan.setStyle("-fx-text-fill: #0f172a; -fx-font-weight: bold;");

        Label originalPrice = new Label(String.format("EUR %.2f", original));
        originalPrice.getStyleClass().add("package-meta");

        Label discountedPrice = new Label(String.format("EUR %.2f", discounted));
        discountedPrice.getStyleClass().add("package-price");

        Label savingsLabel = new Label(String.format("Risparmi EUR %.2f", savings));
        savingsLabel.getStyleClass().add("package-meta");

        VBox prices = new VBox(6, badge, slogan, originalPrice, discountedPrice, savingsLabel);
        prices.setAlignment(Pos.CENTER_LEFT);

        VBox info = new VBox(8, prices);
        info.getStyleClass().add("package-card");
        info.setPadding(new Insets(16, 20, 16, 20));

        return new VBox(12, sectionTitle, info);
    }

    private String formatDataOfferta(String dataFine) {
        if (dataFine == null || dataFine.isBlank()) {
            return "";
        }
        DateTimeFormatter input = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter output = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ITALIAN);
        try {
            LocalDate date = LocalDate.parse(dataFine, input);
            return date.format(output);
        } catch (Exception e) {
            return dataFine;
        }
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
}


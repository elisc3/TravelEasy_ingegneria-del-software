package it.traveleasy;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class OperatorePacchettoPrenotazioneView extends PacchettoView {
    private Prenotazione prenotazione;

    public OperatorePacchettoPrenotazioneView(PacchettoViaggio pacchetto, TravelEasy te, Prenotazione p) {
        super(pacchetto, te);
        this.prenotazione = p;
        addScontiSection();
    }

    private void addScontiSection() {
        ScrollPane root = getRoot();
        Node contentNode = root.getContent();
        if (!(contentNode instanceof VBox)) {
            return;
        }

        VBox content = (VBox) contentNode;
        VBox scontiSection = buildScontiSection();

        int insertIndex = Math.max(2, content.getChildren().size() - 2);
        content.getChildren().add(insertIndex, scontiSection);
    }

    private VBox buildScontiSection() {
        Label sectionTitle = new Label("Sconti applicati alla prenotazione");
        sectionTitle.getStyleClass().add("section-title");
        float sconto = prenotazione.getScontoApplicato();
        float offertaApplicata = prenotazione.getOffertaApplicata();
        VBox info = new VBox(8,
            buildRow("Sconto fedelta applicato per questa prenotazione", String.valueOf(sconto) + "%"),
            buildRow("Percentuale offerta applicata", String.valueOf(offertaApplicata) + "%")
        );
        info.getStyleClass().add("package-card");
        info.setPadding(new Insets(16, 20, 16, 20));

        return new VBox(12, sectionTitle, info);
    }

    private HBox buildRow(String label, String value) {
        Label key = new Label(label + ":");
        key.getStyleClass().add("package-meta");

        Label val = new Label(value == null ? "" : value);
        val.getStyleClass().add("package-description");
        val.setWrapText(true);

        HBox row = new HBox(12, key, val);
        HBox.setHgrow(val, Priority.ALWAYS);
        return row;
    }
}

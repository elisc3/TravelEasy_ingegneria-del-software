package it.traveleasy;

import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ViaggiatoriView {
    private final ScrollPane root;

    public ViaggiatoriView(List<Viaggiatore> viaggiatori) {
        VBox content = new VBox(16);
        content.setPadding(new Insets(24, 24, 32, 24));
        content.getChildren().add(buildHeader());

        VBox list = new VBox(12);
        list.getStyleClass().add("package-list");

        if (viaggiatori == null || viaggiatori.isEmpty()) {
            Label empty = new Label("Nessun viaggiatore disponibile.");
            empty.getStyleClass().add("package-description");
            list.getChildren().add(empty);
        } else {
            int index = 1;
            for (Viaggiatore v : viaggiatori) {
                list.getChildren().add(buildViaggiatoreCard(v, index));
                index++;
            }
        }

        content.getChildren().add(list);

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
        Label title = new Label("Viaggiatori");
        title.getStyleClass().add("section-title");
        return new VBox(title);
    }

    private VBox buildViaggiatoreCard(Viaggiatore viaggiatore, int index) {
        Label title = new Label("Viaggiatore " + index);
        title.getStyleClass().add("package-title");

        VBox info = new VBox(8,
            buildRow("Nome", viaggiatore.getNome()),
            buildRow("Cognome", viaggiatore.getCognome()),
            buildRow("Data di nascita", viaggiatore.getDataNascita()),
            buildRow("Tipo di documento", viaggiatore.getTipoDocumento()),
            buildRow("Codice documento", viaggiatore.getCodiceDocumento())
        );
        info.getStyleClass().add("package-card");
        info.setPadding(new Insets(16, 20, 16, 20));

        return new VBox(8, title, info);
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

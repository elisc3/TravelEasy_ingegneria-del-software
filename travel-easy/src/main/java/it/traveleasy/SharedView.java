package it.traveleasy;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class SharedView {

    private SharedView() {
    }

    public static VBox buildBrandPanel() {
        VBox brand = new VBox(12);
        brand.getStyleClass().add("brand-panel");

        Label logo = new Label("TravelEasy");
        logo.getStyleClass().add("brand-title");

        Label tagline = new Label("La tua agenzia viaggi digitale\nsemplice, veloce, affidabile.");
        tagline.getStyleClass().add("brand-tagline");

        VBox highlights = new VBox(10,
            buildHighlight("Prenota in 3 minuti"),
            buildHighlight("Supporto clienti dedicato"),
            buildHighlight("Offerte personalizzate")
        );

        brand.getChildren().addAll(logo, tagline, highlights);
        brand.setAlignment(Pos.CENTER_LEFT);
        brand.setPadding(new Insets(40));
        VBox.setVgrow(highlights, Priority.ALWAYS);

        return brand;
    }

    private static HBox buildHighlight(String text) {
        Label bullet = new Label("•");
        bullet.getStyleClass().add("bullet");

        Label label = new Label(text);
        label.getStyleClass().add("highlight");

        HBox row = new HBox(8, bullet, label);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }
}

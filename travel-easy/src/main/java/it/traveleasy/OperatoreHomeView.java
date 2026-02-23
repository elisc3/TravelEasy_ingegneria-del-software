package it.traveleasy;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import java.sql.Connection;

public class OperatoreHomeView {
    private final BorderPane root;
    private final VBox content = new VBox(24);
    private Connection conn;
    private TravelEasy te;
    private OperatoreOfferteView activeOfferteView;

    public OperatoreHomeView(Connection conn, TravelEasy te) {
        this.conn = conn;
        this.te = te;
        this.root = new BorderPane();
        root.setTop(buildHeader());
        root.setCenter(buildContent());
        BorderPane.setMargin(root.getCenter(), new Insets(24, 40, 40, 40));

        te.eliminaOfferte();
    }

    public BorderPane getRoot() {
        return root;
    }

    private HBox buildHeader() {
        Label title = new Label("Travel Easy - Operatore");
        title.getStyleClass().add("app-title");

        Button profileButton = new Button("Profilo");
        profileButton.getStyleClass().add("profile-button");
        profileButton.setOnAction(e -> {
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(16, title, spacer, profileButton);
        header.getStyleClass().add("top-bar");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(16, 40, 16, 40));
        return header;
    }

    private VBox buildContent() {
        content.getChildren().addAll(buildMenuBar(), new OperatorePrenotazioniView(te.getPrenotazioni(), te).getRoot());
        return content;
    }

    private HBox buildMenuBar() {
        Button newPackageButton = new Button("Nuovo pacchetto");
        newPackageButton.getStyleClass().add("secondary-button");

        Button offersButton = new Button("Offerte speciali");
        offersButton.getStyleClass().add("secondary-button");

        Button bookingsButton = new Button("Prenotazioni");
        bookingsButton.getStyleClass().add("primary-button");

        Button reviewsButton = new Button("Recensioni");
        reviewsButton.getStyleClass().add("secondary-button");

        newPackageButton.setOnAction(e -> {
            setActiveMenuButton(newPackageButton, offersButton, bookingsButton, reviewsButton);
            showContent(new OperatoreNuovoPacchettoView(conn, te).getRoot());
        });

        offersButton.setOnAction(e -> {
            setActiveMenuButton(offersButton, newPackageButton, bookingsButton, reviewsButton);
            OperatoreOfferteView offersView = new OperatoreOfferteView(te);
            showContent(offersView.getRoot());
            activeOfferteView = offersView;
        });

        bookingsButton.setOnAction(e -> {
            setActiveMenuButton(bookingsButton, newPackageButton, offersButton, reviewsButton);
            showContent(new OperatorePrenotazioniView(te.getPrenotazioni(), te).getRoot());
        });

        reviewsButton.setOnAction(e -> {
            setActiveMenuButton(reviewsButton, newPackageButton, offersButton, bookingsButton);
            showContent(new OperatoreRecensioniView(te, te.getRecensioni()).getRoot());
        });

        HBox menu = new HBox(12, newPackageButton, offersButton, bookingsButton, reviewsButton);
        menu.getStyleClass().add("search-row");
        menu.setAlignment(Pos.CENTER_LEFT);
        return menu;
    }

    private void showContent(VBox view) {
        if (activeOfferteView != null) {
            activeOfferteView.dispose();
            activeOfferteView = null;
        }

        if (content.getChildren().size() > 1) {
            content.getChildren().remove(1);
        }
        content.getChildren().add(view);
    }

    private void setActiveMenuButton(Button active, Button... others) {
        active.getStyleClass().remove("secondary-button");
        if (!active.getStyleClass().contains("primary-button")) {
            active.getStyleClass().add("primary-button");
        }

        for (Button other : others) {
            other.getStyleClass().remove("primary-button");
            if (!other.getStyleClass().contains("secondary-button")) {
                other.getStyleClass().add("secondary-button");
            }
        }
    }

}

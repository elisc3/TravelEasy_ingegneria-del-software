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

        newPackageButton.setOnAction(e -> {
            setActiveMenuButton(newPackageButton, offersButton, bookingsButton);
            showContent(new OperatoreNuovoPacchettoView(conn, te).getRoot());
        });

        offersButton.setOnAction(e -> {
            setActiveMenuButton(offersButton, newPackageButton, bookingsButton);
            OperatoreOfferteView offersView = new OperatoreOfferteView(te);
            showContent(offersView.getRoot());
            activeOfferteView = offersView;
        });

        bookingsButton.setOnAction(e -> {
            setActiveMenuButton(bookingsButton, newPackageButton, offersButton);
            showContent(new OperatorePrenotazioniView(te.getPrenotazioni(), te).getRoot());
        });

        HBox menu = new HBox(12, newPackageButton, offersButton, bookingsButton);
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

    private void setActiveMenuButton(Button active, Button otherA, Button otherB) {
        active.getStyleClass().remove("secondary-button");
        if (!active.getStyleClass().contains("primary-button")) {
            active.getStyleClass().add("primary-button");
        }

        otherA.getStyleClass().remove("primary-button");
        if (!otherA.getStyleClass().contains("secondary-button")) {
            otherA.getStyleClass().add("secondary-button");
        }

        otherB.getStyleClass().remove("primary-button");
        if (!otherB.getStyleClass().contains("secondary-button")) {
            otherB.getStyleClass().add("secondary-button");
        }
    }

}

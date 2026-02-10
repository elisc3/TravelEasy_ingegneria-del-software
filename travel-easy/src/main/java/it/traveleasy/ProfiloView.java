package it.traveleasy;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ProfiloView {

    private final StackPane root;

    public ProfiloView() {
        root = new StackPane();
        root.getStyleClass().add("profile-root");
        root.setPadding(new Insets(24));
        root.getChildren().add(buildCard());
    }

    public StackPane getRoot() {
        return root;
    }

    private VBox buildCard() {
        HBox header = buildHeader();

        Label title = new Label("I miei dati");
        title.getStyleClass().add("profile-title");

        Label name = new Label("Nome");
        name.getStyleClass().add("profile-label");
        Label nameValue = new Label("Mario");
        nameValue.getStyleClass().add("profile-value");

        Label surname = new Label("Cognome");
        surname.getStyleClass().add("profile-label");
        Label surnameValue = new Label("Rossi");
        surnameValue.getStyleClass().add("profile-value");

        Label email = new Label("E-Mail");
        email.getStyleClass().add("profile-label");
        Label emailValue = new Label("mario.rossi@email.it");
        emailValue.getStyleClass().add("profile-value");

        Label phone = new Label("Numero telefono");
        phone.getStyleClass().add("profile-label");
        Label phoneValue = new Label("+39 333 1234567");
        phoneValue.getStyleClass().add("profile-value");

        Label balanceLabel = new Label("Saldo portafoglio");
        balanceLabel.getStyleClass().add("profile-label");
        Label balanceValue = new Label("€ 0.00");
        balanceValue.getStyleClass().add("profile-balance");

        VBox grid = new VBox(12,
            buildRow(name, nameValue),
            buildRow(surname, surnameValue),
            buildRow(email, emailValue),
            buildRow(phone, phoneValue),
            buildRow(balanceLabel, balanceValue)
        );

        VBox card = new VBox(18, header, title, grid);
        card.getStyleClass().add("profile-card");
        card.setPadding(new Insets(28, 32, 32, 32));
        card.setMaxWidth(560);
        card.setAlignment(Pos.TOP_LEFT);
        return card;
    }

    private HBox buildHeader() {
        Label title = new Label("Travel Easy");
        title.getStyleClass().add("app-title");

        HBox header = new HBox(title);
        header.getStyleClass().add("top-bar");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(10, 16, 10, 16));
        return header;
    }

    private HBox buildRow(Label label, Label value) {
        VBox left = new VBox(4, label, value);
        HBox row = new HBox(left);
        row.getStyleClass().add("profile-row");
        return row;
    }
}


package it.traveleasy;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ProfiloView {

    private final StackPane root;
    private Account account;

    public ProfiloView(Account a) {
        this.account = a;
        root = new StackPane();
        root.getStyleClass().add("profile-root");
        root.setPadding(new Insets(24));
        root.getChildren().add(buildCard());
    }

    public StackPane getRoot() {
        return root;
    }

    private VBox buildCard() {
        Cliente c = account.getCliente();
        String nome = c.getNome();
        String cognome = c.getCognome();
        String e_mail = account.getEmail();
        String numeroTelefono = c.getTelefono();
        double saldo = c.getPv().getSaldo();
        PortafoglioOre po = c.getPo();
        float oreAccumulate = po.getOre();
        float scontoAccumulato = po.getSconto();

        String saldoFormat = String.format(java.util.Locale.US, "%.2f", saldo);

        HBox header = buildHeader();

        Label title = new Label("I miei dati");
        title.getStyleClass().add("profile-title");

        Label name = new Label("Nome");
        name.getStyleClass().add("profile-label");
        Label nameValue = new Label(nome);
        nameValue.getStyleClass().add("profile-value");

        Label surname = new Label("Cognome");
        surname.getStyleClass().add("profile-label");
        Label surnameValue = new Label(cognome);
        surnameValue.getStyleClass().add("profile-value");

        Label email = new Label("E-Mail");
        email.getStyleClass().add("profile-label");
        Label emailValue = new Label(e_mail);
        emailValue.getStyleClass().add("profile-value");

        Label phone = new Label("Numero telefono");
        phone.getStyleClass().add("profile-label");
        Label phoneValue = new Label(numeroTelefono);
        phoneValue.getStyleClass().add("profile-value");

        Label balanceLabel = new Label("Saldo portafoglio");
        balanceLabel.getStyleClass().add("profile-label");
        Label balanceValue = new Label("€ "+saldoFormat);
        balanceValue.getStyleClass().add("profile-balance");

        Label accumulatedHoursLabel = new Label("Ore accumulate");
        accumulatedHoursLabel.getStyleClass().add("profile-label");
        Label accumulatedHoursValue = new Label(String.valueOf(oreAccumulate));
        accumulatedHoursValue.getStyleClass().add("profile-value");

        Label accumulatedDiscountLabel = new Label("Sconto accumulato");
        accumulatedDiscountLabel.getStyleClass().add("profile-label");
        Label accumulatedDiscountValue = new Label(String.valueOf(scontoAccumulato) + "%");
        accumulatedDiscountValue.getStyleClass().add("profile-value");

        VBox grid = new VBox(12,
            buildRow(name, nameValue),
            buildRow(surname, surnameValue),
            buildRow(email, emailValue),
            buildRow(phone, phoneValue),
            buildRow(balanceLabel, balanceValue),
            buildRow(accumulatedHoursLabel, accumulatedHoursValue),
            buildRow(accumulatedDiscountLabel, accumulatedDiscountValue)
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


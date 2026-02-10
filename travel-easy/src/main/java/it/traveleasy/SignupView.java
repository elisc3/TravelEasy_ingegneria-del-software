package it.traveleasy;

import java.sql.Connection;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

//import org.hibernate.sql.results.graph.EntityGraphTraversalState.TraversalResult;

public class SignupView {

    private final BorderPane root;
    
    private Connection conn;

    public SignupView(Stage stage, TravelEasy te, Connection conn) {
        this.root = new BorderPane();
        root.setLeft(SharedView.buildBrandPanel());
        this.conn = conn;

        VBox card = buildCard("Crea il tuo account", "Registrati in pochi secondi.");

        TextField nameField = new TextField();
        nameField.setPromptText("Nome");
        nameField.getStyleClass().add("input");

        TextField surnameField = new TextField();
        surnameField.setPromptText("Cognome");
        surnameField.getStyleClass().add("input");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.getStyleClass().add("input");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("input");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Conferma password");
        confirmPasswordField.getStyleClass().add("input");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Numero di telefono");
        phoneField.getStyleClass().add("input");

        Button signupButton = new Button("Registrati");
        signupButton.getStyleClass().add("primary-button");
        signupButton.setMaxWidth(Double.MAX_VALUE);


        HBox switchBox = new HBox();
        switchBox.setAlignment(Pos.CENTER);
        Label haveAccount = new Label("Hai già un account?");
        Hyperlink goLogin = new Hyperlink("Accedi");
        goLogin.getStyleClass().add("link");
        switchBox.getChildren().addAll(haveAccount, goLogin);
        switchBox.setSpacing(8);

        //goLogin.setOnAction(e -> App.showLogin(stage));

        VBox form = new VBox(14, nameField, surnameField, emailField, passwordField, confirmPasswordField, phoneField, signupButton, switchBox);
        form.setAlignment(Pos.CENTER_LEFT);
        form.setFillWidth(true);

        card.getChildren().add(form);
        root.setCenter(card);
        BorderPane.setMargin(card, new Insets(40));
    }

    public BorderPane getRoot() {
        return root;
    }

    private VBox buildCard(String titleText, String subtitleText) {
        VBox card = new VBox(18);
        card.getStyleClass().add("card");

        Text title = new Text(titleText);
        title.getStyleClass().add("title");

        Text subtitle = new Text(subtitleText);
        subtitle.getStyleClass().add("subtitle");

        VBox header = new VBox(6, title, subtitle);

        card.getChildren().add(header);
        card.setPadding(new Insets(36));

        return card;
    }
}

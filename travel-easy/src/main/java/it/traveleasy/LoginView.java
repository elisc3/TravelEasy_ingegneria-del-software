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

public class LoginView {
    private Connection conn;
    private TravelEasy te;

    private final BorderPane root;

    public LoginView(Stage stage, Connection conn, TravelEasy te) {
        this.conn = conn;
        if (this.conn!=null){
            System.out.println("Sono la pagina di login e ho ricevuto la connessione al db");
        }
        this.te = te;

        this.root = new BorderPane();
        root.setLeft(SharedView.buildBrandPanel());

        VBox card = buildCard("Accedi a TravelEasy", "Bentornato! Inserisci le tue credenziali.");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.getStyleClass().add("input");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("input");

        Button loginButton = new Button("Accedi");
        loginButton.getStyleClass().add("primary-button");
        loginButton.setMaxWidth(Double.MAX_VALUE);   
        
        loginButton.setOnAction(e -> {
            String email = emailField.getText(); 
            String[] res = te.login(this.conn, email, passwordField.getText());
            if (!res[0].equals("errore")) {
                System.out.println("Login avvenuto con successo");
                //int idUtente = 
                if (res[1].equals("Cliente"))
                   App.showHome(stage, res[0]); //CONTINUARE DA QUA L'ASSEGNAZIONE DI UN RIFERIMENTO ALL'UTENTE ALLA HOMEVIEW;
                else
                    App.showOperatoreHomeView(stage);
            }
            else
                System.out.println("Ricontrollare i dati inseriti");        
        });

        Hyperlink forgotLink = new Hyperlink("Password dimenticata?");
        forgotLink.getStyleClass().add("link");

        HBox switchBox = new HBox();
        switchBox.setAlignment(Pos.CENTER);
        Label noAccount = new Label("Non hai un account?");
        Hyperlink goSignup = new Hyperlink("Registrati");
        goSignup.getStyleClass().add("link");
        switchBox.getChildren().addAll(noAccount, goSignup);
        switchBox.setSpacing(8);

        goSignup.setOnAction(e -> App.showSignup(stage));

        VBox form = new VBox(16, emailField, passwordField, loginButton, forgotLink, switchBox);
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

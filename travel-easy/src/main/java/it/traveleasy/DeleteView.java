package it.traveleasy;

import java.sql.Connection;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class DeleteView {
    private VBox root;
    private TravelEasy te;
    private Connection conn;
    private String email;

    public DeleteView(TravelEasy te, Connection conn, String email) {
        this.te = te;
        this.conn = conn;
        this.email = email;

        buildUI();
    }

    public VBox getRoot() {
        return root;
    }

    private void buildUI() {

        Label title = new Label("Eliminazione Account");
        title.getStyleClass().add("profile-title");

        Label warning = new Label(
            "Attenzione.\nQuesta operazione è irreversibile.\n\n" +
            "Per confermare scrivi la tua password nel campo sotto."
        );
        warning.setWrapText(true);

        TextField confirmField = new TextField();
        confirmField.setPromptText("Scrivi la tua password per confermare");

        Button deleteButton = new Button("Elimina definitivamente");
        deleteButton.getStyleClass().add("danger-button");

        deleteButton.setOnAction(e -> {
            te.eliminaAccount(conn, email, confirmField.getText());
            Platform.exit();
        });

        root = new VBox(20, title, warning, confirmField, deleteButton);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("profile-card");
    }
}

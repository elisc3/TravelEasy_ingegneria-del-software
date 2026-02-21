package it.traveleasy;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import java.sql.Connection;

import javax.swing.JOptionPane;
 
public class RicaricaView {
    private TravelEasy te;
    private Cliente cliente;
    private final Runnable onRicaricaCompletata;

    private final StackPane root;

    public RicaricaView(TravelEasy te, int idUtente) {
        this(te, idUtente, null);
    }

    public RicaricaView(TravelEasy te, int idUtente, Runnable onRicaricaCompletata) {
        this.te = te;
        this.cliente = te.getClienteById(idUtente);
        this.onRicaricaCompletata = onRicaricaCompletata;


        root = new StackPane();
        root.getStyleClass().add("payment-root");
        root.setPadding(new Insets(24));
        root.getChildren().add(buildCard());
    }

    public StackPane getRoot() {
        return root;
    }

    private VBox buildCard() {
        Label title = new Label("Ricarica portafoglio");
        title.getStyleClass().add("section-title");

        TextField cardNumber = new TextField();
        cardNumber.setPromptText("Numero carta");
        cardNumber.getStyleClass().add("input");

        TextField expiry = new TextField();
        expiry.setPromptText("Scadenza (MM/AA)");
        expiry.getStyleClass().add("input");

        TextField cvv = new TextField();
        cvv.setPromptText("CVV");
        cvv.getStyleClass().add("input");
        cvv.setMaxWidth(140);
        cvv.setPrefWidth(140);

        HBox row = new HBox(12, expiry, cvv);
        row.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(expiry, Priority.ALWAYS);

        ComboBox<String> circuit = new ComboBox<>();
        circuit.getItems().addAll("Visa", "Mastercard");
        circuit.setPromptText("Circuito");
        circuit.getStyleClass().add("input");

        TextField amount = new TextField();
        amount.setPromptText("Importo ricarica (€)");
        amount.getStyleClass().add("input");

        Button confirm = new Button("Conferma ricarica");
        confirm.getStyleClass().add("primary-button");

        CartaCredito cc = cliente.getCc();
        if (cc != null && cc.getNumeroCarta() != null && !cc.getNumeroCarta().isEmpty()){
            cardNumber.setText(cc.getNumeroCarta());
            cardNumber.setEditable(false);

            expiry.setText(cc.getScadenza());
            expiry.setEditable(false);

            if (cc.getCircuito() != null && !cc.getCircuito().isBlank()) {
                circuit.getSelectionModel().select(cc.getCircuito());
            }
        }

        confirm.setOnAction(e -> {
            CartaCredito ccOnConfirm = cliente.getCc();
            //int idUtente = cliente.getId();
            
            if (ccOnConfirm != null && ccOnConfirm.getNumeroCarta() != null && !ccOnConfirm.getNumeroCarta().isEmpty()){
                cardNumber.setText(ccOnConfirm.getNumeroCarta());
                cardNumber.setEditable(false);

                expiry.setText(ccOnConfirm.getScadenza());
                expiry.setEditable(false);

                if (ccOnConfirm.getCircuito() != null && !ccOnConfirm.getCircuito().isBlank()) {
                    circuit.getSelectionModel().select(ccOnConfirm.getCircuito());
                }

                String cvvInserito = cvv.getText();
                float esitoValidazioneDati = te.validazioneDatiNuovaRicarica(cardNumber.getText(), expiry.getText(), cvvInserito, amount.getText(), cliente);
                if (esitoValidazioneDati == -1.0F){
                    JOptionPane.showMessageDialog(null, "Hai dimenticato qualche campo.", "ATTENZIONE", 2);
                    return;
                } else if (esitoValidazioneDati == -2.0F){
                    JOptionPane.showMessageDialog(null, "Il cvv deve essere di 3 cifre", "ATTENZIONE", 2);
                    return;
                } else if (esitoValidazioneDati == -3.0F){
                    JOptionPane.showMessageDialog(null, "Il cvv deve essere un numero intero di 3 cifre", "ERRORE", 0);
                    return;
                } else if (esitoValidazioneDati == -4.0F){
                    JOptionPane.showMessageDialog(null, "La scadenza deve essere del formato mm/AA.", "ATTENZIONE", 2);
                    return;
                } else if (esitoValidazioneDati == -5.0F){
                    JOptionPane.showMessageDialog(null, "Non può essere inserito un importo negativo.", "ATTENZIONE", 2);
                    return;
                } else if (esitoValidazioneDati == -6.0F){
                    JOptionPane.showMessageDialog(null, "Formato importo inserito non valido.", "ERRORE", 0);
                    return;
                } else if (esitoValidazioneDati == -7.0F){
                    JOptionPane.showMessageDialog(null, "Il cvv è errato. Prego riprovare", "ATTENZIONE", 0);
                    return;
                }

                //boolean esitoCvv = cc.controlCvv(cvvInserito);
                //if (esitoCvv){
                    
                    
                if (te.ricarica(cliente, esitoValidazioneDati)) {
                    JOptionPane.showMessageDialog(null, "Ricarica avvenuta con successo!", "INFO", 1);
                    te.ricaricaEffettuata(cliente.getId());
                    if (onRicaricaCompletata != null) {
                        onRicaricaCompletata.run();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Si è verificato un problema", "ERRORE", 2);
                }
                
            } else {
                float esitoValidazioneDati = te.validazioneDatiNuovaRicarica(cardNumber.getText(), expiry.getText(), cvv.getText(), amount.getText(), cliente);
                if (esitoValidazioneDati == -1.0F){
                    JOptionPane.showMessageDialog(null, "Hai dimenticato qualche campo.", "ATTENZIONE", 2);
                    return;
                } else if (esitoValidazioneDati == -2.0F){
                    JOptionPane.showMessageDialog(null, "Il cvv deve essere di 3 cifre", "ATTENZIONE", 2);
                    return;
                } else if (esitoValidazioneDati == -3.0F){
                    JOptionPane.showMessageDialog(null, "Il cvv deve essere un numero intero di 3 cifre", "ERRORE", 0);
                    return;
                } else if (esitoValidazioneDati == -4.0F){
                    JOptionPane.showMessageDialog(null, "La scadenza deve essere del formato mm/AA.", "ATTENZIONE", 2);
                    return;
                } else if (esitoValidazioneDati == -5.0F){
                    JOptionPane.showMessageDialog(null, "Non può essere inserito un importo negativo.", "ATTENZIONE", 2);
                    return;
                } else if (esitoValidazioneDati == -6.0F){
                    JOptionPane.showMessageDialog(null, "Formato importo inserito non valido.", "ERRORE", 0);
                    return;
                } 

                te.insertCartaCredito(cliente, cardNumber.getText(), expiry.getText(), cvv.getText(), circuit.getValue());
                //CartaCredito newCarta = te.getCartaCreditoByUtente(idUtente);
                if (te.ricarica(cliente, Float.parseFloat(amount.getText()))){
                    JOptionPane.showMessageDialog(null, "Ricarica avvenuta con successo!", "INFO", 1);
                    System.out.println("Ricarica avvenuta con sucecsso");
                    te.ricaricaEffettuata(cliente.getId());
                    if (onRicaricaCompletata != null) {
                        onRicaricaCompletata.run();
                    }
                }
                else{
                    JOptionPane.showMessageDialog(null, "Si è verificato un problema", "ERRORE", 0);
                    System.out.println("Errore ricarica");
                }
            }
        });

        VBox card = new VBox(14, title, cardNumber, row, circuit, amount, confirm);
        card.getStyleClass().add("payment-card");
        card.setPadding(new Insets(28, 32, 32, 32));
        card.setMaxWidth(520);
        card.setAlignment(Pos.TOP_LEFT);
        return card;
    }
}



package it.traveleasy;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
//import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.Scene;
import java.sql.Connection;
import java.util.List;
import javax.swing.JOptionPane;


public class PagamentoView implements RicaricaObserver {

    private final StackPane root;
    private TravelEasy te;
    private PacchettoViaggio pacchetto;
    private PacchettoViaggio pacchettoOriginale;
    private Cliente cliente;
    private List<Viaggiatore> elencoViaggiatori;
    private Connection conn;
    private Label balanceLabel;
    private Button confirmButton;
    private float totale;
    private boolean modificaPrenotazioneMode;

    public PagamentoView(TravelEasy te, int idUtente, PacchettoViaggio pacchetto, List<Viaggiatore> elencoViaggiatori,Connection conn) {
        this.te = te;
        this.cliente = this.te.getClienteById(idUtente);
        this.elencoViaggiatori = elencoViaggiatori;
        this.pacchetto = pacchetto;
        
        this.conn = conn;
        this.te.addRicaricaObserver(this);
        root = new StackPane();
        root.getStyleClass().add("payment-root");
        root.getChildren().add(buildCard(null));
    }

    public PagamentoView(TravelEasy te, Prenotazione prenotazione, PacchettoViaggio nuovoPacchetto, Connection conn) {
        this.te = te;
        this.cliente = prenotazione.getCliente();
        this.elencoViaggiatori = prenotazione.getElencoViaggiatori();
        this.pacchettoOriginale = prenotazione.getPacchetto();
        this.pacchetto = nuovoPacchetto;
        this.conn = conn;
        this.modificaPrenotazioneMode = true;
        this.te.addRicaricaObserver(this);
        root = new StackPane();
        root.getStyleClass().add("payment-root");
        root.getChildren().add(buildCard(prenotazione));
    }

    public StackPane getRoot() {
        return root;
    }

    private HBox buildHeader() {
        Label title = new Label("Travel Easy");
        title.getStyleClass().add("app-title");

        HBox header = new HBox(title);
        header.getStyleClass().add("top-bar");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(16, 40, 16, 40));
        return header;
    }

    private VBox buildContent(Prenotazione prenotazione) {
        if (modificaPrenotazioneMode) {
            return buildContentModificaPrenotazione(prenotazione);
        }

        Label sectionTitle = new Label("Pagamento");
        sectionTitle.getStyleClass().add("section-title");

        totale = te.getTotalePrenotazione(cliente, pacchetto, elencoViaggiatori);
        float scontoApplicato = cliente.getPo().getSconto();
        

        Label totalLabel = new Label("Totale dovuto: EUR " + totale);
        totalLabel.getStyleClass().add("package-price");

        double saldoPortafoglio = cliente.getPv().getSaldo();

        String saldoFormat = String.format(java.util.Locale.US, "%.2f", saldoPortafoglio);
        balanceLabel = new Label("Saldo attuale portafoglio: EUR " + saldoFormat);
        balanceLabel.getStyleClass().add("package-meta");

        Label fidelityTitle = new Label("Sconto fedelta");
        fidelityTitle.getStyleClass().add("section-subtitle");

        

        Label fidelityValue = new Label("Sconto disponibile: " + scontoApplicato + "%");
        fidelityValue.getStyleClass().add("package-meta");

        Label fidelityHint = new Label("E' stato applicato lo sconto nel riepilogo finale.");
        fidelityHint.getStyleClass().add("package-meta");

        VBox fidelityBox = new VBox(6, fidelityTitle, fidelityValue, fidelityHint);
        fidelityBox.getStyleClass().add("search-card");
        fidelityBox.setPadding(new Insets(12));
        fidelityBox.setMaxWidth(Double.MAX_VALUE);

        confirmButton = new Button("Conferma pagamento");
        confirmButton.getStyleClass().add("primary-button");
        
        if (totale > saldoPortafoglio)
            confirmButton.setDisable(true);
        else
            confirmButton.setDisable(false);

        confirmButton.setOnAction(e -> {
            if(!cliente.pagamentoOnPortafoglioDB(conn, totale)){
                JOptionPane.showMessageDialog(null, "Errore durante il pagamento, riprovare.", "ERRORE", 0);
                return;
            }
            OffertaSpeciale o = te.getOffertaByPack(pacchetto);
            float percentualeOfferta;
            if (o == null)
                percentualeOfferta = 0.0F;
            else
                percentualeOfferta = o.getScontoPercentuale();
            
            if(!te.registrazionePrenotazione(cliente, pacchetto, elencoViaggiatori, scontoApplicato, totale, percentualeOfferta)){
                JOptionPane.showMessageDialog(null, "La registrazione della prenotazione non è andata a buon fine, stiamo effetuando il rimborso.", "ERRORE", 0);
                if (!cliente.rimborsoOnPortafoglioDB(conn, totale)){
                    JOptionPane.showMessageDialog(null, "Rimborso fallito. Contattare l'assistenza.", "ERRORE", 0);
                    return;
                } else {
                    JOptionPane.showMessageDialog(null, "Il rimborso è stato effettuato", "INFO", 1);
                    return;
                }
            }

            JOptionPane.showMessageDialog(null, "Prenotazione avvenuta con successo!", "INFO", 1);
        });

        Button rechargeButton = new Button("Nuova ricarica portafoglio");
        rechargeButton.getStyleClass().add("secondary-button");
        rechargeButton.setOnAction(e -> { 
            openRechargeWindow();
        });

        VBox content = new VBox(16, sectionTitle, totalLabel, balanceLabel, fidelityBox, confirmButton, rechargeButton);
        content.setAlignment(Pos.CENTER_LEFT);
        return content;
    }

    private VBox buildContentModificaPrenotazione(Prenotazione prenotazione) {
        Label sectionTitle = new Label("Pagamento modifica prenotazione");
        sectionTitle.getStyleClass().add("section-title");

        float totaleOriginale = te.getTotalePrenotazione(cliente, pacchettoOriginale, elencoViaggiatori);
        totale = te.getTotalePrenotazione(cliente, pacchetto, elencoViaggiatori);

        Label oldTotalLabel = new Label(String.format("Totale attuale: EUR %.2f", totaleOriginale));
        oldTotalLabel.getStyleClass().add("package-meta");

        Label newTotalLabel = new Label(String.format("Nuovo totale: EUR %.2f", totale));
        newTotalLabel.getStyleClass().add("package-price");

        double saldoPortafoglio = cliente.getPv().getSaldo();
        String saldoFormat = String.format(java.util.Locale.US, "%.2f", saldoPortafoglio);
        balanceLabel = new Label("Saldo attuale portafoglio: EUR " + saldoFormat);
        balanceLabel.getStyleClass().add("package-meta");

        confirmButton = new Button("Conferma pagamento");
        confirmButton.getStyleClass().add("primary-button");
        float difference = totale - totaleOriginale;
        if (difference > 0 && totale > saldoPortafoglio) {
            confirmButton.setDisable(true);
        } else {
            confirmButton.setDisable(false);
        }
        // Placeholder UI-only: la logica business di conferma viene gestita successivamente.
        confirmButton.setOnAction(e -> {
            if (totaleOriginale > totale){
                float rimborso = totaleOriginale - totale;
                if (!cliente.rimborsoOnPortafoglioDB(conn, rimborso)){
                    JOptionPane.showMessageDialog(null, "Il rimborso non è andato a buon fine. Prego riprovare.", "ERRORE", 0);
                    return;
                }
            } else {
                float nuovoPagamento = totale - totaleOriginale;
                if (!cliente.pagamentoOnPortafoglioDB(conn, nuovoPagamento)){
                    JOptionPane.showMessageDialog(null, "Il pagamento non è andato a buon fine. Prego riprovare.", "ERRORE", 0);
                    return;
                }
            }

            if (!te.modificaPacchettoPrenotazione(prenotazione, pacchetto)){
                JOptionPane.showMessageDialog(null, "La modifica della prenotazione non è andata a buon fine. Prego riprovare.", "ERRORE", 0);
                return;   
            }
         });

        Button rechargeButton = new Button("Nuova ricarica portafoglio");
        rechargeButton.getStyleClass().add("secondary-button");
        rechargeButton.setOnAction(e -> openRechargeWindow());

        VBox content = new VBox(16, sectionTitle, oldTotalLabel, newTotalLabel, balanceLabel, confirmButton, rechargeButton);
        content.setAlignment(Pos.CENTER_LEFT);
        return content;
    }

    private VBox buildCard(Prenotazione prenotazione) {
        VBox card = new VBox(20, buildHeader(), buildContent(prenotazione));
        card.getStyleClass().add("payment-card");
        card.setPadding(new Insets(28, 32, 32, 32));
        card.setMaxWidth(520);
        card.setAlignment(Pos.TOP_LEFT);
        return card;
    }

    private void openRechargeWindow() {
        Stage stage = new Stage();
        RicaricaView view = new RicaricaView(te, cliente.getId());
        Scene scene = new Scene(view.getRoot(), 520, 460);
        scene.getStylesheets().add(App.class.getResource(App.STYLESHEET).toExternalForm());
        stage.setTitle("Travel Easy - Ricarica");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void onRicaricaEffettuata(int idUtente, double nuovoSaldo) {
        if (cliente == null || cliente.getId() != idUtente) {
            return;
        }

        Platform.runLater(() -> {
            balanceLabel.setText("Saldo attuale portafoglio: EUR " + nuovoSaldo);
            if (totale > nuovoSaldo) {
                confirmButton.setDisable(true);
            } else {
                confirmButton.setDisable(false);
            }
        });
    }
}



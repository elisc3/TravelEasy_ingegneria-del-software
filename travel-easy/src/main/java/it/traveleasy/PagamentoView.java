package it.traveleasy;

import java.sql.Connection;
import java.util.List;

import javax.swing.JOptionPane;
import java.applet.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Platform;


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
    private float prezzoAssistenzaSpeciale;
    private boolean modificaPrenotazioneMode;
    private boolean modificaAssistenzaSpecialeMode;
    private float vecchioPrezzoAssistenzaSpeciale;

    public PagamentoView(TravelEasy te, int idUtente, int idPrenotazione, PacchettoViaggio pacchetto, List<Viaggiatore> elencoViaggiatori,Connection conn) {
        this(te, idUtente, idPrenotazione, pacchetto, elencoViaggiatori, conn, 0.0F);
    }

    public PagamentoView(TravelEasy te, int idUtente, int idPrenotazione, PacchettoViaggio pacchetto, List<Viaggiatore> elencoViaggiatori,Connection conn, float prezzoAssistenzaSpeciale) {
        this.te = te;
        this.cliente = this.te.getClienteById(idUtente);
        this.elencoViaggiatori = elencoViaggiatori;
        this.pacchetto = pacchetto;
        this.prezzoAssistenzaSpeciale = prezzoAssistenzaSpeciale;
        this.te.addRicaricaObserver(this);
        this.conn = conn;
        root = new StackPane();
        root.getStyleClass().add("payment-root");
        root.getChildren().add(buildCard(this.te.getPrenotazioneById(idPrenotazione)));
    }

    public PagamentoView(TravelEasy te, Prenotazione prenotazione, PacchettoViaggio nuovoPacchetto, Connection conn) {
        this(te, prenotazione, nuovoPacchetto, conn, 0.0F);
    }

    public PagamentoView(TravelEasy te, Prenotazione prenotazione, PacchettoViaggio nuovoPacchetto, Connection conn, float prezzoAssistenzaSpeciale) {
        this.te = te;
        this.cliente = prenotazione.getCliente();
        this.elencoViaggiatori = prenotazione.getElencoViaggiatori();
        this.pacchettoOriginale = prenotazione.getPacchetto();
        this.pacchetto = nuovoPacchetto;
        this.conn = conn;
        this.prezzoAssistenzaSpeciale = prezzoAssistenzaSpeciale;
        this.modificaPrenotazioneMode = true;
        this.te.addRicaricaObserver(this);
        root = new StackPane();
        root.getStyleClass().add("payment-root");
        root.getChildren().add(buildCard(prenotazione));
    }

    public PagamentoView(TravelEasy te, Prenotazione prenotazione, float vecchioPrezzoAssistenzaSpeciale, Connection conn) {
        this.te = te;
        this.cliente = prenotazione.getCliente();
        this.elencoViaggiatori = prenotazione.getElencoViaggiatori();
        this.pacchettoOriginale = prenotazione.getPacchetto();
        this.pacchetto = prenotazione.getPacchetto();
        this.conn = conn;
        this.vecchioPrezzoAssistenzaSpeciale = vecchioPrezzoAssistenzaSpeciale;
        this.modificaAssistenzaSpecialeMode = true;
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
        if (modificaAssistenzaSpecialeMode) {
            return buildContentModificaAssistenzaSpeciale(prenotazione);
        }

        if (modificaPrenotazioneMode) {
            return buildContentModificaPrenotazione(prenotazione);
        }

        Label sectionTitle = new Label("Pagamento");
        sectionTitle.getStyleClass().add("section-title");

        float costoPrenotazione = te.getTotalePrenotazione(cliente, pacchetto, elencoViaggiatori);
        float costoAssistenzaSpeciale = this.prezzoAssistenzaSpeciale;
        if (costoAssistenzaSpeciale <= 0.0F) {
            costoAssistenzaSpeciale = calcolaPrezzoAssistenzaSpeciale(elencoViaggiatori);
        }
        final float prezzoAssistenzaSpecialeCalcolato = costoAssistenzaSpeciale;
        totale = costoPrenotazione + prezzoAssistenzaSpecialeCalcolato;
        final float scontoApplicato = (cliente.getPo() != null) ? cliente.getPo().getSconto() : 0.0F;

        Label bookingCostLabel = new Label(String.format(java.util.Locale.US, "Costo Prenotazione: EUR %.2f", costoPrenotazione));
        bookingCostLabel.getStyleClass().add("package-meta");

        Label assistenzaLabel = new Label(String.format(java.util.Locale.US, "Prezzo Assistenza Speciale: EUR %.2f", prezzoAssistenzaSpecialeCalcolato));
        assistenzaLabel.getStyleClass().add("package-meta");

        Label totalLabel = new Label(String.format(java.util.Locale.US, "Totale dovuto: EUR %.2f", totale));
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
            if (prenotazione == null) {
                JOptionPane.showMessageDialog(null, "Prenotazione non trovata. Riprovare.", "ERRORE", 0);
                return;
            }
            if(!te.pagamentoOnPortafoglioDB(totale, cliente)){
                JOptionPane.showMessageDialog(null, "Errore durante il pagamento, riprovare.", "ERRORE", 0);
                return;
            }
            OffertaSpeciale o = te.getOffertaByPack(pacchetto);
            float percentualeOfferta;
            if (o == null)
                percentualeOfferta = 0.0F;
            else
                percentualeOfferta = o.getScontoPercentuale();
            
            if(!te.registrazionePrenotazione(prenotazione.getId(), scontoApplicato, totale, percentualeOfferta, prezzoAssistenzaSpecialeCalcolato)){
                JOptionPane.showMessageDialog(null, "La registrazione della prenotazione non e andata a buon fine, stiamo effetuando il rimborso.", "ERRORE", 0);
                if (!cliente.rimborsoOnPortafoglioDB(conn, totale)){
                    JOptionPane.showMessageDialog(null, "Rimborso fallito. Contattare l'assistenza.", "ERRORE", 0);
                    return;
                } else {
                    if (!te.annullaPrenotazioneBozza(prenotazione.getId())) {
                        JOptionPane.showMessageDialog(null, "Il rimborso e stato effettuato ma la prenotazione bozza non e stata rimossa. Contattare l'assistenza.", "ERRORE", 0);
                        return;
                    }
                    JOptionPane.showMessageDialog(null, "Il rimborso e stato effettuato e la prenotazione bozza e stata annullata.", "INFO", 1);
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

        VBox content = new VBox(16, sectionTitle, bookingCostLabel, assistenzaLabel, totalLabel, balanceLabel, fidelityBox, confirmButton, rechargeButton);
        content.setAlignment(Pos.CENTER_LEFT);
        return content;
    }

    private VBox buildContentModificaAssistenzaSpeciale(Prenotazione prenotazione) {
        Label sectionTitle = new Label("Pagamento modifica assistenza speciale");
        sectionTitle.getStyleClass().add("section-title");

        float nuovoPrezzoAssistenza = prenotazione.getPrezzoAssistenzaSpeciale();
        float totaleOriginale = prenotazione.getPrezzoTotale();
        float nuovoTotale = totaleOriginale - vecchioPrezzoAssistenzaSpeciale + nuovoPrezzoAssistenza;

        Label oldTotalLabel = new Label(String.format("Totale attuale: EUR %.2f", totaleOriginale));
        oldTotalLabel.getStyleClass().add("package-meta");

        Label newTotalLabel = new Label(String.format("Nuovo totale: EUR %.2f", nuovoTotale));
        newTotalLabel.getStyleClass().add("package-price");

        double saldoPortafoglio = cliente.getPv().getSaldo();
        String saldoFormat = String.format(java.util.Locale.US, "%.2f", saldoPortafoglio);
        balanceLabel = new Label("Saldo attuale portafoglio: EUR " + saldoFormat);
        balanceLabel.getStyleClass().add("package-meta");

        float difference = nuovoTotale - totaleOriginale;
        confirmButton = new Button("Conferma pagamento");
        confirmButton.getStyleClass().add("primary-button");
        if (difference > 0) {
            confirmButton.setDisable(difference > saldoPortafoglio);
        } else {
            confirmButton.setDisable(false);
        }

        confirmButton.setOnAction(e -> {
            if (difference < 0) {
                float rimborso = -difference;
                if (!te.rimborsoOnPortafoglioDB(rimborso, cliente)) {
                    JOptionPane.showMessageDialog(null, "Il rimborso non e andato a buon fine. Prego riprovare.", "ERRORE", 0);
                    return;
                }
            } else if (difference > 0) {
                if (!te.pagamentoOnPortafoglioDB(difference, cliente)) {
                    JOptionPane.showMessageDialog(null, "Il pagamento non e andato a buon fine. Prego riprovare.", "ERRORE", 0);
                    return;
                }
            }
            
            te.setPrezzoAssistenzaSpeciale(nuovoPrezzoAssistenza, prenotazione);
            te.setPrezzoTotale(nuovoTotale, prenotazione);
            JOptionPane.showMessageDialog(null, "Modifica assistenza speciale effettuata!", "INFO", 1);
        });

        Button rechargeButton = new Button("Nuova ricarica portafoglio");
        rechargeButton.getStyleClass().add("secondary-button");
        rechargeButton.setOnAction(e -> openRechargeWindow());

        VBox content = new VBox(16, sectionTitle, oldTotalLabel, newTotalLabel, balanceLabel, confirmButton, rechargeButton);
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
        if (difference > 0) {
            if (totale > saldoPortafoglio)
                confirmButton.setDisable(true);
            else
                confirmButton.setDisable(false);
        } else {
            confirmButton.setDisable(false);
        }
        // Placeholder UI-only: la logica business di conferma viene gestita successivamente.
        confirmButton.setOnAction(e -> {
            if (totaleOriginale > totale){
                float rimborso = totaleOriginale - totale;
                if (!te.rimborsoOnPortafoglioDB(rimborso, cliente)){
                    JOptionPane.showMessageDialog(null, "Il rimborso non è andato a buon fine. Prego riprovare.", "ERRORE", 0);
                    return;
                }
            } else {
                float nuovoPagamento = totale - totaleOriginale;
                if (!te.pagamentoOnPortafoglioDB(nuovoPagamento, cliente)){
                    JOptionPane.showMessageDialog(null, "Il pagamento non è andato a buon fine. Prego riprovare.", "ERRORE", 0);
                    return;
                }
            }

            if (!te.modificaPacchettoPrenotazione(prenotazione, pacchetto)){
                JOptionPane.showMessageDialog(null, "La modifica della prenotazione non è andata a buon fine. Prego riprovare.", "ERRORE", 0);
                return;   
            }

            JOptionPane.showMessageDialog(null, "Modifica prenotazione effettuata!", "INFO", 1);
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

    private float calcolaPrezzoAssistenzaSpeciale(List<Viaggiatore> viaggiatori) {
        if (viaggiatori == null) {
            return 0.0F;
        }

        float totaleAssistenza = 0.0F;
        for (Viaggiatore v : viaggiatori) {
            if (v.isSediaRotelle()) {
                totaleAssistenza += 35.0F;
            }
            if (v.isCecita()) {
                totaleAssistenza += 25.0F;
            }
        }
        return totaleAssistenza;
    }

    private void refreshAfterRecharge() {
        if (cliente == null || cliente.getPv() == null || balanceLabel == null || confirmButton == null) {
            return;
        }

        double nuovoSaldo = cliente.getPv().getSaldo();
        String saldoFormat = String.format(java.util.Locale.US, "%.2f", nuovoSaldo);
        balanceLabel.setText("Saldo attuale portafoglio: EUR " + saldoFormat);
        if (totale > nuovoSaldo) {
            confirmButton.setDisable(true);
        } else {
            confirmButton.setDisable(false);
        }
    }

    @Override
    public void onRicaricaEffettuata(int idUtente, double nuovoSaldo) {
        if (cliente != null && cliente.getId() == idUtente) {
            if (cliente.getPv() != null) {
                cliente.getPv().setSaldo(nuovoSaldo);
            }
            Platform.runLater(this::refreshAfterRecharge);
        }
    }
}


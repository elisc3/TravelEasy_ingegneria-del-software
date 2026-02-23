package it.traveleasy;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.Map;


public class OperatoreRecensioniView {
    private final VBox root;
    private TravelEasy te;
    private Map<Integer, Recensione> elencoRecensioni;
    private int nRiferimenti = 3;

    public OperatoreRecensioniView(TravelEasy te, Map<Integer, Recensione> elencoRecensioni) {
        this.te = te;
        this.elencoRecensioni =elencoRecensioni;
        Label sectionTitle = new Label("Recensioni ricevute");
        sectionTitle.getStyleClass().add("section-title");

        HBox metricBar = buildMetricBar();

        VBox list = new VBox(16);
        list.getStyleClass().add("package-list");
        Recensione[] recensione = new Recensione[nRiferimenti];
        int count = 0;
        for (Recensione r: elencoRecensioni.values()) {
            recensione[count++] = r;
            if (count == 3){
                Recensione[] recensioneCard = recensione.clone();
                list.getChildren().add(buildRecensioneCard(recensioneCard));
                recensione = new Recensione[nRiferimenti];
                count = 0;
            }
        }

        ScrollPane scrollPane = new ScrollPane(list);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("package-scroll");

        root = new VBox(12, sectionTitle, metricBar, scrollPane);
    }

    public VBox getRoot() {
        return root;
    }

    private HBox buildMetricBar() {

        float mediaAgenziaF = te.getMediaRecensioni("Agenzia");
        float mediaTrasportoF = te.getMediaRecensioni("Trasporto");
        float mediaAlloggioF = te.getMediaRecensioni("Alloggio");

        VBox mediaAgenzia = buildMetricBox("Media Agenzia", String.format("%.1f/5", mediaAgenziaF));
        VBox mediaTrasporto = buildMetricBox("Media Trasporto", String.format("%.1f/5", mediaTrasportoF));
        VBox mediaAlloggio = buildMetricBox("Media Alloggio", String.format("%.1f/5", mediaAlloggioF));
        VBox totaleRecensioni = buildMetricBox("Totale Recensioni", String.valueOf(te.getNTotaleRecensioni()));

        HBox bar = new HBox(12, mediaAgenzia, mediaTrasporto, mediaAlloggio, totaleRecensioni);
        bar.setAlignment(Pos.CENTER_LEFT);
        return bar;
    }

    private VBox buildMetricBox(String titolo, String valore) {
        Label title = new Label(titolo);
        title.getStyleClass().add("package-meta");

        Label value = new Label(valore);
        value.getStyleClass().add("package-price");

        VBox box = new VBox(4, title, value);
        box.getStyleClass().add("package-card");
        box.setPadding(new Insets(12, 16, 12, 16));
        HBox.setHgrow(box, Priority.ALWAYS);
        return box;
    }

    private VBox buildRecensioneCard(Recensione[] recensione) {
        Cliente clienteC = recensione[0].getCliente();
        PacchettoViaggio pacchetto = recensione[0].getPrenotazione().getPacchetto();
        Label cliente = new Label(clienteC.getNome() + " " + clienteC.getCognome());
        cliente.getStyleClass().add("package-title");

        Label dataInvio = new Label("Data invio: " + recensione[0].getData());
        dataInvio.getStyleClass().add("package-meta");

        Label prenotazione = new Label("Prenotazione: " + pacchetto.getCodice() + " - " + pacchetto.getCittà() + ", " + pacchetto.getNazione());
        prenotazione.getStyleClass().add("package-destination");

        float stelleAgenziaF = recensione[0].getStelle();
        float stelleTrasportoF = recensione[1].getStelle();
        float stelleAlloggioF = recensione[2].getStelle();

        
        
        Label stelleAgenzia = new Label("Punteggio agenzia: " + stelleAgenziaF + "/5");
        stelleAgenzia.getStyleClass().add("package-meta");

        Label stelleTrasporto = new Label("Punteggio trasporto: " +stelleTrasportoF + "/5");
        stelleTrasporto.getStyleClass().add("package-meta");

        Label stelleAlloggio = new Label("Punteggio alloggio: " + stelleAlloggioF + "/5");
        stelleAlloggio.getStyleClass().add("package-meta");

        VBox info = new VBox(6, cliente, dataInvio, prenotazione, stelleAgenzia, stelleTrasporto, stelleAlloggio);
        info.setAlignment(Pos.CENTER_LEFT);

        Button dettagliPacchettoButton = new Button("Dettagli Pacchetto");
        dettagliPacchettoButton.getStyleClass().add("secondary-button");
        dettagliPacchettoButton.setOnAction(e -> {
            openPackageWindow(pacchetto);
        });

        Button dettagliRecensioneButton = new Button("Dettagli Recensione");
        dettagliRecensioneButton.getStyleClass().add("primary-button");
        dettagliRecensioneButton.setOnAction(e -> {
            openRecensioneWindow(recensione);
        });

        VBox actions = new VBox(10, dettagliPacchettoButton, dettagliRecensioneButton);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.setPrefWidth(190);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox content = new HBox(20, info, spacer, actions);
        content.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(info, Priority.ALWAYS);

        VBox card = new VBox(content);
        card.getStyleClass().add("package-card");
        card.setPadding(new Insets(16, 20, 16, 20));
        return card;
    }

    /*private List<RecensioneOperatoreMock> buildMockRecensioni() {
        List<RecensioneOperatoreMock> list = new ArrayList<>();
        list.add(new RecensioneOperatoreMock(
            "12-02-2026",
            "Giulia Bianchi",
            "TE-2451",
            "Lisbona, Portogallo",
            5, 4, 5,
            "Pacchetto Premium, 6 notti, volo diretto, hotel 4 stelle in centro.",
            "Agenzia: ottima assistenza pre-partenza. Trasporto: check-in rapido. Alloggio: camera pulita e posizione eccellente."
        ));
        list.add(new RecensioneOperatoreMock(
            "09-02-2026",
            "Marco De Santis",
            "TE-2388",
            "Atene, Grecia",
            4, 4, 4,
            "Pacchetto City Break, 4 notti, trasferimenti inclusi, colazione inclusa.",
            "Agenzia: comunicazione chiara. Trasporto: lievi ritardi al rientro. Alloggio: struttura buona e personale disponibile."
        ));
        list.add(new RecensioneOperatoreMock(
            "05-02-2026",
            "Elena Rossi",
            "TE-2310",
            "Parigi, Francia",
            5, 5, 4,
            "Pacchetto Weekend, 3 notti, hotel 3 stelle, tour guidato opzionale.",
            "Agenzia: supporto veloce via telefono. Trasporto: servizio puntuale. Alloggio: stanza piccola ma confortevole."
        ));
        list.add(new RecensioneOperatoreMock(
            "01-02-2026",
            "Luca Moretti",
            "TE-2284",
            "Vienna, Austria",
            4, 5, 5,
            "Pacchetto Cultura, 5 notti, museum pass incluso, navetta aeroporto.",
            "Agenzia: documentazione completa. Trasporto: ottimo. Alloggio: colazione eccellente e zona tranquilla."
        ));
        return list;
    }*/

    private void openPackageWindow(PacchettoViaggio pacchetto) {
        if (pacchetto == null) {
            return;
        }
        Stage stage = new Stage();
        PacchettoView view = new PacchettoView(pacchetto, te);
        Scene scene = new Scene(view.getRoot(), 720, 680);
        scene.getStylesheets().add(App.class.getResource(App.STYLESHEET).toExternalForm());
        stage.setTitle("Travel Easy - Dettagli Pacchetto");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private void openRecensioneWindow(Recensione[] recensione) {
        if (recensione == null || recensione[0] == null || recensione[0].getPrenotazione() == null) {
            return;
        }
        Stage stage = new Stage();
        Prenotazione prenotazione = recensione[0].getPrenotazione();
        int idCliente = prenotazione.getCliente().getId();
        ModuloRecensioniView view = new ModuloRecensioniView(te, idCliente, prenotazione, recensione, true);
        Scene scene = new Scene(view.getRoot(), 780, 700);
        scene.getStylesheets().add(App.class.getResource(App.STYLESHEET).toExternalForm());
        stage.setTitle("Travel Easy - Visualizza Recensione");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    /*private static class RecensioneOperatoreMock {
        private final String dataInvio;
        private final String nomeCliente;
        private final String codicePrenotazione;
        private final String destinazione;
        private final int stelleAgenzia;
        private final int stelleTrasporto;
        private final int stelleAlloggio;
        private final String dettagliPacchetto;
        private final String dettagliRecensione;

        private RecensioneOperatoreMock(
            String dataInvio,
            String nomeCliente,
            String codicePrenotazione,
            String destinazione,
            int stelleAgenzia,
            int stelleTrasporto,
            int stelleAlloggio,
            String dettagliPacchetto,
            String dettagliRecensione
        ) {
            this.dataInvio = dataInvio;
            this.nomeCliente = nomeCliente;
            this.codicePrenotazione = codicePrenotazione;
            this.destinazione = destinazione;
            this.stelleAgenzia = stelleAgenzia;
            this.stelleTrasporto = stelleTrasporto;
            this.stelleAlloggio = stelleAlloggio;
            this.dettagliPacchetto = dettagliPacchetto;
            this.dettagliRecensione = dettagliRecensione;
        }
    }*/
}

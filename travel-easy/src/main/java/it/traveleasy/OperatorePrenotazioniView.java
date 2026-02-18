package it.traveleasy;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Map;

import javax.swing.JOptionPane;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class OperatorePrenotazioniView implements PrenotazioneObserver, RecensioneObserver {
    private final VBox root;
    private final TravelEasy te;
    private final boolean showModificaPrenotazioneButton;
    private final Connection conn;
    private VBox listContainer;
    private Map<Integer, Prenotazione> elencoPrenotazioni;

    public OperatorePrenotazioniView(Map<Integer, Prenotazione> elencoPrenotazioni, TravelEasy te) {
        this(elencoPrenotazioni, te, false, null);
    }

    public OperatorePrenotazioniView(Map<Integer, Prenotazione> elencoPrenotazioni, TravelEasy te, boolean showModificaPrenotazioneButton) {
        this(elencoPrenotazioni, te, showModificaPrenotazioneButton, null);
    }

    public OperatorePrenotazioniView(Map<Integer, Prenotazione> elencoPrenotazioni, TravelEasy te, boolean showModificaPrenotazioneButton, Connection conn) {
        this.elencoPrenotazioni = elencoPrenotazioni;
        this.te = te;
        this.showModificaPrenotazioneButton = showModificaPrenotazioneButton;
        this.conn = conn;
        if (showModificaPrenotazioneButton) {
            this.te.addPrenotazioneObserver(this);
            this.te.addRecensioneObserver(this);
        }
        root = new VBox(12, buildHeader(), buildList());
    }

    public VBox getRoot() {
        return root;
    }

    private HBox buildHeader() {
        Label sectionTitle = new Label("Prenotazioni effettuate");
        sectionTitle.getStyleClass().add("section-title");

        HBox header = new HBox(12, sectionTitle);
        header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }

    private ScrollPane buildList() {
        listContainer = new VBox(16);
        listContainer.getStyleClass().add("package-list");
        refreshPrenotazioniList();

        ScrollPane scrollPane = new ScrollPane(listContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("package-scroll");
        return scrollPane;
    }

    private void refreshPrenotazioniList() {
        if (listContainer == null) {
            return;
        }

        listContainer.getChildren().clear();
        for (Prenotazione p : elencoPrenotazioni.values()) {
            PacchettoViaggio pacchetto = p.getPacchetto();
            Cliente cliente = p.getCliente();
            listContainer.getChildren().add(buildPrenotazioneCard(
                "Prenotazione #" + pacchetto.getCodice(),
                pacchetto.getCittà() + ", " + pacchetto.getNazione(),
                pacchetto.getDataPartenza() + " -> " + pacchetto.getDataRitorno(),
                "Effettuata da: " + cliente.getNome() + " " + cliente.getCognome() + "\nNumero di telefono: " + cliente.getTelefono() + "\nData prenotazione: " + p.getDataPrenotazione(),
                "EUR " + p.getPrezzoTotale(),
                p
            ));
        }
    }

    private void openPackageWindow(Prenotazione p) {
        Stage stage = new Stage();
        PacchettoViaggio pacchetto = p.getPacchetto();
        OperatorePacchettoPrenotazioneView view = new OperatorePacchettoPrenotazioneView(pacchetto, te, p);
        Scene scene = new Scene(view.getRoot(), 720, 680);
        scene.getStylesheets().add(App.class.getResource(App.STYLESHEET).toExternalForm());
        stage.setTitle("Travel Easy - Dettagli Pacchetto Prenotazione");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private void openViaggiatoriWindow(Prenotazione prenotazione) {
        Stage stage = new Stage();
        ViaggiatoriView view = new ViaggiatoriView(prenotazione.getElencoViaggiatori());
        Scene scene = new Scene(view.getRoot(), 640, 560);
        scene.getStylesheets().add(App.class.getResource(App.STYLESHEET).toExternalForm());
        stage.setTitle("Travel Easy - Viaggiatori");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private void openModificaPrenotazioneWindow(Prenotazione prenotazione) {
        Stage stage = new Stage();
        ModuloModificaPrenotazioneView view = new ModuloModificaPrenotazioneView(prenotazione, te, conn);
        Scene scene = new Scene(view.getRoot(), 720, 600);
        scene.getStylesheets().add(App.class.getResource(App.STYLESHEET).toExternalForm());
        stage.setTitle("Travel Easy - Modifica Prenotazione");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private void openRecensioneWindow(Prenotazione prenotazione) {
        openRecensioneWindow(prenotazione, null, false);
    }

    private void openRecensioneWindow(Prenotazione prenotazione, Recensione[] recensione, boolean solaLettura) {
        Stage stage = new Stage();
        int idCliente = prenotazione.getCliente().getId();
        ModuloRecensioniView view = new ModuloRecensioniView(te, idCliente, prenotazione, recensione, solaLettura);
        Scene scene = new Scene(view.getRoot(), 780, 700);
        scene.getStylesheets().add(App.class.getResource(App.STYLESHEET).toExternalForm());
        stage.setTitle(solaLettura ? "Travel Easy - Visualizza Recensione" : "Travel Easy - Inserisci Recensione");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private boolean almeno7GiorniDopoOggi(String data) {
        if (data == null || data.isBlank()) return false;

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-uuuu")
                .withResolverStyle(ResolverStyle.STRICT);

        try {
            LocalDate d = LocalDate.parse(data, fmt);
            return !d.isBefore(LocalDate.now().plusDays(7));
        } catch (Exception e) {
            return false;
        }
    }

    private boolean prenotazioneConclusa(String dataRitorno) {
        if (dataRitorno == null || dataRitorno.isBlank()) return false;

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-uuuu")
                .withResolverStyle(ResolverStyle.STRICT);

        try {
            LocalDate d = LocalDate.parse(dataRitorno, fmt);
            return !d.isAfter(LocalDate.now());
        } catch (Exception e) {
            return false;
        }
    }

    private VBox buildPrenotazioneCard(String titolo, String destinazione, String date, String partecipanti, String prezzo, Prenotazione prenotazione) {
        Label title = new Label(titolo);
        title.getStyleClass().add("package-title");

        Label destination = new Label(destinazione);
        destination.getStyleClass().add("package-destination");

        Label duration = new Label(date);
        duration.getStyleClass().add("package-meta");

        Label description = new Label(partecipanti);
        description.getStyleClass().add("package-description");
        description.setWrapText(true);

        Label price = new Label(prezzo);
        price.getStyleClass().add("package-price");

        Button detailsButton = new Button("Dettagli Pacchetto");
        detailsButton.getStyleClass().add("secondary-button");
        detailsButton.setPrefWidth(180);
        detailsButton.setOnAction(e -> openPackageWindow(prenotazione));

        Button viewButton = new Button("Visualizza Viaggiatori");
        viewButton.getStyleClass().add("primary-button");
        viewButton.setPrefWidth(180);
        viewButton.setOnAction(e -> openViaggiatoriWindow(prenotazione));

        VBox info = new VBox(6, title, destination, duration, description);
        info.setAlignment(Pos.CENTER_LEFT);

        VBox actions = new VBox(10, price, detailsButton);
        if (showModificaPrenotazioneButton) {
            Button editButton = new Button("Modifica Prenotazione");
            editButton.getStyleClass().add("secondary-button");
            editButton.setPrefWidth(180);
            PacchettoViaggio pacchetto = prenotazione.getPacchetto();
            String dataPartenza = pacchetto.getDataPartenza();
            editButton.setDisable(!almeno7GiorniDopoOggi(dataPartenza));
            editButton.setOnAction(e -> openModificaPrenotazioneWindow(prenotazione));

            Button reviewButton = new Button("Inserisci Recensione");
            reviewButton.getStyleClass().add("secondary-button");
            reviewButton.setPrefWidth(180);
            Recensione[] recensione = prenotazione.getCliente().getRecensioneByPrenotazione(prenotazione.getId());
            // = 
            boolean conclusa = prenotazioneConclusa(prenotazione.getPacchetto().getDataRitorno());
            if (!conclusa) {
                reviewButton.setDisable(true);
            } else if (recensione != null) {
                reviewButton.setText("Visualizza Recensione");
                reviewButton.setOnAction(e -> openRecensioneWindow(prenotazione, recensione, true));
            } else {
                reviewButton.setOnAction(e -> openRecensioneWindow(prenotazione));
            }

            actions.getChildren().add(editButton);
            actions.getChildren().add(reviewButton);
        } else {
            Button checkInButton = new Button("Effettua check-in");
            checkInButton.getStyleClass().add("secondary-button");
            checkInButton.setPrefWidth(180);
            checkInButton.setOnAction(e -> {
                    if(te.effettuaCheckIn(prenotazione))
                        JOptionPane.showMessageDialog(null, "Check-in effettuato con successo.", "INFO", 1);
                }
            );
            actions.getChildren().add(checkInButton);
        }
        actions.getChildren().add(viewButton);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.setPrefWidth(190);

        HBox cardContent = new HBox(20, info, actions);
        cardContent.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(info, Priority.ALWAYS);

        VBox card = new VBox(cardContent);
        card.getStyleClass().add("package-card");
        card.setPadding(new Insets(16, 20, 16, 20));
        return card;
    }

    @Override
    public void onPrenotazioneModificata(Prenotazione prenotazione) {
        if (!showModificaPrenotazioneButton || prenotazione == null) {
            return;
        }
        if (!elencoPrenotazioni.containsKey(prenotazione.getId())) {
            return;
        }

        Platform.runLater(this::refreshPrenotazioniList);
    }

    public void dispose() {
        if (showModificaPrenotazioneButton) {
            te.removePrenotazioneObserver(this);
            te.removeRecensioneObserver(this);
        }
    }

    @Override
    public void onRecensioneCreata(Recensione recensione) {
        if (!showModificaPrenotazioneButton || recensione == null) {
            return;
        }

        Platform.runLater(this::refreshPrenotazioniList);
    }
}

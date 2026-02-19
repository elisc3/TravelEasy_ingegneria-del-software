package it.traveleasy;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
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

    private void openModificaViaggiatoriWindow(Prenotazione prenotazione) {
        VBox content = new VBox(12);
        content.setPadding(new Insets(16));
        content.getStyleClass().add("booking-content");

        Label title = new Label("Modifica viaggiatori");
        title.getStyleClass().add("section-title");

        VBox formList = new VBox(12);
        formList.getStyleClass().add("package-list");
        List<TextField> nomeFields = new ArrayList<>();
        List<TextField> cognomeFields = new ArrayList<>();
        List<TextField> dataNascitaFields = new ArrayList<>();
        List<ComboBox<String>> tipoDocumentoFields = new ArrayList<>();
        List<TextField> codiceDocumentoFields = new ArrayList<>();
        List<CheckBox> sediaRotelleChecks = new ArrayList<>();
        List<CheckBox> cecitaChecks = new ArrayList<>();

        int idx = 1;
        for (Viaggiatore v : prenotazione.getElencoViaggiatori()) {
            VBox card = new VBox(8);
            card.getStyleClass().add("package-card");
            card.setPadding(new Insets(12));

            Label travelerTitle = new Label("Viaggiatore " + idx);
            travelerTitle.getStyleClass().add("package-title");

            TextField nomeField = new TextField(v.getNome());
            nomeField.setPromptText("Nome");
            nomeField.getStyleClass().add("input");

            TextField cognomeField = new TextField(v.getCognome());
            cognomeField.setPromptText("Cognome");
            cognomeField.getStyleClass().add("input");

            TextField dataNascitaField = new TextField(v.getDataNascita());
            dataNascitaField.setPromptText("Data di nascita (dd-MM-uuuu)");
            dataNascitaField.getStyleClass().add("input");

            ComboBox<String> tipoDocumentoField = new ComboBox<>();
            tipoDocumentoField.getItems().addAll("Carta d'identita", "Patente di guida", "Passaporto");
            tipoDocumentoField.setPromptText("Tipo di documento");
            tipoDocumentoField.getStyleClass().add("input");
            String tipoDoc = v.getTipoDocumento();
            if (tipoDoc != null && !tipoDoc.isBlank()) {
                if (!tipoDocumentoField.getItems().contains(tipoDoc)) {
                    tipoDocumentoField.getItems().add(tipoDoc);
                }
                tipoDocumentoField.setValue(tipoDoc);
            }

            TextField codiceDocumentoField = new TextField(v.getCodiceDocumento());
            codiceDocumentoField.setPromptText("Codice documento");
            codiceDocumentoField.getStyleClass().add("input");

            CheckBox sediaRotelleCheck = new CheckBox();
            sediaRotelleCheck.setSelected(v.isSediaRotelle());
            Label sediaRotelleLabel = new Label("Sedia rotelle");
            HBox sediaRotelleRow = new HBox(8, sediaRotelleCheck, sediaRotelleLabel);
            sediaRotelleRow.setAlignment(Pos.CENTER_LEFT);

            CheckBox cecitaCheck = new CheckBox();
            cecitaCheck.setSelected(v.isCecita());
            Label cecitaLabel = new Label("Cecita");
            HBox cecitaRow = new HBox(8, cecitaCheck, cecitaLabel);
            cecitaRow.setAlignment(Pos.CENTER_LEFT);

            nomeFields.add(nomeField);
            cognomeFields.add(cognomeField);
            dataNascitaFields.add(dataNascitaField);
            tipoDocumentoFields.add(tipoDocumentoField);
            codiceDocumentoFields.add(codiceDocumentoField);
            sediaRotelleChecks.add(sediaRotelleCheck);
            cecitaChecks.add(cecitaCheck);

            card.getChildren().addAll(
                travelerTitle,
                nomeField,
                cognomeField,
                dataNascitaField,
                tipoDocumentoField,
                codiceDocumentoField,
                sediaRotelleRow,
                cecitaRow
            );

            formList.getChildren().add(card);
            idx++;
        }

        ScrollPane scrollPane = new ScrollPane(formList);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("package-scroll");

        Button confermaButton = new Button("Conferma");
        confermaButton.getStyleClass().add("primary-button");
        confermaButton.setOnAction(e -> {
            List<Viaggiatore> nuoviViaggiatori = new ArrayList<>();

            for (int i = 0; i < nomeFields.size(); i++) {
                Viaggiatore nuovo = new Viaggiatore(
                    nomeFields.get(i).getText(),
                    cognomeFields.get(i).getText(),
                    dataNascitaFields.get(i).getText(),
                    tipoDocumentoFields.get(i).getValue(),
                    codiceDocumentoFields.get(i).getText()
                );
                nuovo.setSediaRotelle(sediaRotelleChecks.get(i).isSelected());
                nuovo.setCecita(cecitaChecks.get(i).isSelected());
                nuoviViaggiatori.add(nuovo);
            }

            /*for (int i = 0; i < nuoviViaggiatori.size(); i++) {
                Viaggiatore v = nuoviViaggiatori.get(i);
                int pos = i + 1;
                int esitoValidazioneDati;

                try {
                    esitoValidazioneDati = v.validazioneDatiPrenotazione(v);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Data inserita non valida in persona " + pos + ".", "ERRORE", 0);
                    return;
                }

                if (esitoValidazioneDati == -1) {
                    JOptionPane.showMessageDialog(null, "Hai dimenticato qualche campo in persona " + pos + ".", "ATTENZIONE", 2);
                    return;
                } else if (esitoValidazioneDati == -2) {
                    JOptionPane.showMessageDialog(null, "Data inserita non valida in persona " + pos + ".", "ERRORE", 0);
                    return;
                } else if (esitoValidazioneDati == -3) {
                    JOptionPane.showMessageDialog(null, "Codice del documento non valido in persona " + pos + ".", "ERRORE", 0);
                    return;
                } else if (esitoValidazioneDati == -4) {
                    JOptionPane.showMessageDialog(null, "Codice del documento non valido in persona " + pos + ".", "ERRORE", 0);
                    return;
                } else if (esitoValidazioneDati == -5) {
                    JOptionPane.showMessageDialog(null, "Codice del documento non valido in persona " + pos + ".", "ERRORE", 0);
                    return;
                }
            }*/

            if(te.modificaViaggiatori(prenotazione, nuoviViaggiatori))
                JOptionPane.showMessageDialog(null, "Modifica dei viaggiatori effettuata!", "INFO", 1);
            else
                JOptionPane.showMessageDialog(null, "Modifica dei viaggiatori fallita. Riprovare", "ERRORE", 0);
            
        });

        content.getChildren().addAll(title, scrollPane, confermaButton);

        Stage stage = new Stage();
        Scene scene = new Scene(content, 720, 620);
        scene.getStylesheets().add(App.class.getResource(App.STYLESHEET).toExternalForm());
        stage.setTitle("Travel Easy - Modifica Viaggiatori");
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

    private void eliminaPrenotazioneCliente(Prenotazione prenotazione) {
        float rimborso = te.getRimborsoEliminazionePrenotazione(prenotazione);
        if (rimborso < 0.0F) {
            JOptionPane.showMessageDialog(null, "Prenotazione non eliminabile: partenza entro 2 giorni.", "INFO", 1);
            return;
        }

        String rimborsoFormatted = String.format(java.util.Locale.US, "%.2f", rimborso);
        int conferma = JOptionPane.showConfirmDialog(
            null,
            "Confermi l'eliminazione della prenotazione?\nRimborso previsto: EUR " + rimborsoFormatted,
            "Conferma eliminazione",
            JOptionPane.YES_NO_OPTION
        );
        if (conferma != JOptionPane.YES_OPTION) {
            return;
        }

        int esitoEliminazione = te.eliminaPrenotazione(prenotazione);
        if (esitoEliminazione == -1) {
            JOptionPane.showMessageDialog(null, "Rimborso fallito: la prenotazione rimane valida.", "ERRORE", 0);
            return;
        }
        if (esitoEliminazione == -2) {
            JOptionPane.showMessageDialog(null, "Eliminazione prenotazione non riuscita. Riprovare.", "ERRORE", 0);
            return;
        }
        if (esitoEliminazione == -3) {
            JOptionPane.showMessageDialog(null, "Prenotazione non eliminabile: partenza entro 2 giorni.", "INFO", 1);
            return;
        }

        JOptionPane.showMessageDialog(null, "Somma del rimborso: EUR " + rimborsoFormatted, "INFO", 1);
        JOptionPane.showMessageDialog(null, "Rimborso confermato.", "INFO", 1);
        refreshPrenotazioniList();
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

            Button editTravelersButton = new Button("Modifica Viaggiatori");
            editTravelersButton.getStyleClass().add("secondary-button");
            editTravelersButton.setPrefWidth(180);
            editTravelersButton.setOnAction(e -> openModificaViaggiatoriWindow(prenotazione));

            Button deleteButton = new Button("Elimina Prenotazione");
            deleteButton.getStyleClass().add("secondary-button");
            deleteButton.setPrefWidth(180);
            deleteButton.setOnAction(e -> eliminaPrenotazioneCliente(prenotazione));

            actions.getChildren().add(editButton);
            actions.getChildren().add(reviewButton);
            actions.getChildren().add(editTravelersButton);
            actions.getChildren().add(deleteButton);
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

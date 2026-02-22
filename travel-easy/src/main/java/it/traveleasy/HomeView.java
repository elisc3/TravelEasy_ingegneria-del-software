package it.traveleasy;

import java.sql.Connection;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.JOptionPane;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;


public class HomeView implements OffertaObserver {
    VBox content = new VBox(24);

    private final BorderPane root;
    private TextField destinationField;
    private TextField maxPriceField;
    private DatePicker startDate;
    private DatePicker endDate;

    private TravelEasy te;
    private Connection conn;

    private String emailUtente;
    private int idUtente;

    public HomeView(TravelEasy te, Connection conn, String emailUtente) {
        this.te = te;
        this.conn = conn;

        this.emailUtente = emailUtente;
        int idAccount = this.getIdAccountByEmail(emailUtente);
        this.idUtente = this.getIdUtenteByAccount(idAccount);
        
        this.root = new BorderPane();
        root.setTop(buildHeader());
        root.setCenter(buildContent());
        BorderPane.setMargin(root.getCenter(), new Insets(24, 40, 40, 40));
        this.te.addOffertaObserver(this);

        te.eliminaOfferte();
    }

    private int getIdAccountByEmail(String email){
        return AccountDao.INSTANCE.findIdByEmail(conn, email);
    }

    private int getIdUtenteByAccount(int idAccount){
        return UtenteDao.INSTANCE.findIdByAccount(conn, idAccount);
    }

    public String getEmailUtente() {
        return emailUtente;
    }

    public BorderPane getRoot() {
        return root;
    }

    private HBox buildHeader() {
        Label title = new Label("Travel Easy");
        title.getStyleClass().add("app-title");

        Button bookingsButton = new Button("Visualizza le mie prenotazioni");
        bookingsButton.getStyleClass().add("secondary-button");
        bookingsButton.setOnAction(e -> openBookingsWindow());

        Button rechargeButton = new Button("Nuova ricarica portafoglio");
        rechargeButton.getStyleClass().add("secondary-button");
        rechargeButton.setOnAction(e -> openRechargeWindow());

        Button profileButton = new Button("Profilo");
        profileButton.getStyleClass().add("profile-button");
        profileButton.setOnAction(e -> openProfileWindow());

        Button deleteButton = new Button("Elimina Account");
        deleteButton.getStyleClass().add("danger-button");
        deleteButton.setOnAction(e -> openDeleteView());


        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(16, title, spacer, bookingsButton, rechargeButton, profileButton, deleteButton);
        header.getStyleClass().add("top-bar");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(16, 40, 16, 40));
        return header;
    }

    private void openProfileWindow() {
        Stage stage = new Stage();
        ProfiloView view = new ProfiloView(te.getAccountToHomeView(emailUtente));
        Scene scene = new Scene(view.getRoot(), 600, 700);
        scene.getStylesheets().add(App.class.getResource(App.STYLESHEET).toExternalForm());
        stage.setTitle("Travel Easy - Profilo");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private void openBookingsWindow() {
        Stage stage = new Stage();
        Cliente cliente = te.getAccountToHomeView(emailUtente).getCliente();
        OperatorePrenotazioniView view = new OperatorePrenotazioniView(cliente.getElencoPrenotazioniEffettuate(), te, true, conn);
        StackPane wrapper = new StackPane(view.getRoot());
        wrapper.setPadding(new Insets(20, 24, 20, 24));
        double maxAvailableHeight = Screen.getPrimary().getVisualBounds().getHeight() - 80;
        double windowHeight = Math.min(760, maxAvailableHeight);
        Scene scene = new Scene(wrapper, 1260, windowHeight);
        scene.getStylesheets().add(App.class.getResource(App.STYLESHEET).toExternalForm());
        stage.setTitle("Travel Easy - Le mie prenotazioni");
        stage.setResizable(false);
        stage.setOnCloseRequest(e -> view.dispose());
        stage.setScene(scene);
        stage.show();
    }

    private void openDeleteView() {
        Stage stage = new Stage();
        DeleteView view = new DeleteView(te, conn, emailUtente);
        Scene scene = new Scene(view.getRoot(), 450, 300);
        scene.getStylesheets().add(App.class.getResource(App.STYLESHEET).toExternalForm());
        stage.setTitle("Travel Easy - Elimina Account");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private void openRechargeWindow() {
        Stage stage = new Stage();
        RicaricaView view = new RicaricaView(te, idUtente);
        Scene scene = new Scene(view.getRoot(), 520, 460);
        scene.getStylesheets().add(App.class.getResource(App.STYLESHEET).toExternalForm());
        stage.setTitle("Travel Easy - Ricarica");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private VBox buildContent() {
        
        //content.getChildren().addAll(buildSearchBar(), buildPackageList());
        content.getChildren().addAll(buildSearchBar());
        return content;
    }

    private VBox buildSearchBar() {
        Label sectionTitle = new Label("Cerca il tuo prossimo viaggio");
        sectionTitle.getStyleClass().add("section-title");

        destinationField = new TextField();
        destinationField.setPromptText("Destinazione (città):");
        destinationField.getStyleClass().addAll("input", "search-input");

        startDate = new DatePicker();
        startDate.getStyleClass().add("date-picker");
        startDate.setPromptText("Data andata");

        endDate = new DatePicker();
        endDate.getStyleClass().add("date-picker");
        endDate.setPromptText("Data ritorno");

        maxPriceField = new TextField();
        maxPriceField.setPromptText("Prezzo massimo");
        maxPriceField.getStyleClass().addAll("input", "search-input");

        Button searchButton = new Button("Cerca");
        searchButton.getStyleClass().add("primary-button");
        searchButton.setOnAction(e -> {
            
            if (content.getChildren().size() != 1)
                content.getChildren().remove(1);
            VBox box = buildPackageList();
            if (box != null)
                content.getChildren().add(buildPackageList());

        });

        HBox searchRow = new HBox(12, destinationField, startDate, endDate, maxPriceField, searchButton);
        searchRow.getStyleClass().add("search-row");
        searchRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(destinationField, Priority.ALWAYS);

        VBox wrapper = new VBox(12, sectionTitle, searchRow);
        wrapper.getStyleClass().add("search-card");
        wrapper.setPadding(new Insets(20));
        return wrapper;
    }



    private VBox buildPackageList() {
        Label sectionTitle = new Label("Pacchetti consigliati secondo le vostre richieste:");
        sectionTitle.getStyleClass().add("section-title");

        Button closeButton = new Button("Chiudi elenco");
        closeButton.getStyleClass().add("secondary-button");
        closeButton.setOnAction(e -> {
            content.getChildren().remove(1);
        });

        HBox header = new HBox(12, sectionTitle, closeButton);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox list = new VBox(16);
        list.getStyleClass().add("package-list");

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String startDateText = startDate.getValue() == null ? "" : startDate.getValue().format(dateFormat);
        String endDateText = endDate.getValue() == null ? "" : endDate.getValue().format(dateFormat);

        float maxPrice = Float.MAX_VALUE;
        if (!maxPriceField.getText().isBlank()) {
            maxPrice = Float.parseFloat(maxPriceField.getText());
        }

        if (!te.coerenzaDate(startDateText, endDateText)){
            JOptionPane.showMessageDialog(null, "Date inserite non valide. Prego ricontrollare.", "ERRORE", 0);
            return null;
        }

        List<PacchettoViaggio> packages = te.ricercaPacchetti(destinationField.getText(), startDateText, endDateText, maxPrice);

        if (packages.size() == 0){
            sectionTitle.setText("Nessun pacchetto viaggio trovato secondo le vostre esigenze");
            closeButton.setVisible(false);
        }

        for (PacchettoViaggio pack : packages) {
            System.out.println("Risultati ottenuti da getPacchettiByFilters: "+pack.getCittà());
            OffertaSpeciale o = te.getOffertaByPack(pack);
            PacchettoVacanzaCard card;
            if (o == null) 
                card = new PacchettoVacanzaCard(pack, te, idUtente, conn);
            else 
                card = new PacchettoVacanzaCard(pack, te, idUtente, o.getScontoPercentuale(), o.getPrezzoScontato(), conn);
            
            list.getChildren().add(card.getRoot());
        }

        ScrollPane scrollPane = new ScrollPane(list);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("package-scroll");

        VBox wrapper = new VBox(12, header, scrollPane);
        return wrapper;
    }

    @Override
    public void onOffertaCreata(OffertaSpeciale offerta) {
        refreshPackageListIfVisible();
    }

    @Override
    public void onOffertaEliminata(PacchettoViaggio pacchetto) {
        refreshPackageListIfVisible();
    }

    private void refreshPackageListIfVisible() {
        Platform.runLater(() -> {
            if (content.getChildren().size() <= 1) {
                return;
            }
            content.getChildren().remove(1);
            VBox updated = buildPackageList();
            if (updated != null) {
                content.getChildren().add(updated);
            }
        });
    }

    public void dispose() {
        te.removeOffertaObserver(this);
    }
}

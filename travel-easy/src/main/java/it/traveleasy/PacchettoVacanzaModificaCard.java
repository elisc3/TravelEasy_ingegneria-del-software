package it.traveleasy;

import java.sql.Connection;

import javax.swing.JOptionPane;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class PacchettoVacanzaModificaCard extends PacchettoVacanzaCard {
    private final Prenotazione prenotazioneDaModificare;
    private final Runnable onModificaCompletata;

    public PacchettoVacanzaModificaCard(
        PacchettoViaggio pacchetto,
        TravelEasy te,
        int idUtente,
        Connection conn,
        Prenotazione prenotazioneDaModificare,
        Runnable onModificaCompletata
    ) {
        super(pacchetto, te, idUtente, conn);
        this.prenotazioneDaModificare = prenotazioneDaModificare;
        this.onModificaCompletata = onModificaCompletata;
    }

    public PacchettoVacanzaModificaCard(
        PacchettoViaggio pacchetto,
        TravelEasy te,
        int idUtente,
        float discountPercent,
        float prezzoScontato,
        Connection conn,
        Prenotazione prenotazioneDaModificare,
        Runnable onModificaCompletata
    ) {
        super(pacchetto, te, idUtente, discountPercent, prezzoScontato, conn);
        this.prenotazioneDaModificare = prenotazioneDaModificare;
        this.onModificaCompletata = onModificaCompletata;
    }

    @Override
    protected void onPrenotaAction() {
        PacchettoViaggio nuovoPacchetto = getPacchetto();
        PacchettoViaggio pacchettoAttuale = prenotazioneDaModificare.getPacchetto();

        if (pacchettoAttuale != null && pacchettoAttuale.getId() == nuovoPacchetto.getId()) {
            JOptionPane.showMessageDialog(null, "Hai selezionato lo stesso pacchetto della prenotazione attuale.", "ATTENZIONE", 2);
            return;
        }

        Stage stage = new Stage();
        PagamentoView view = new PagamentoView(getTravelEasy(), prenotazioneDaModificare, nuovoPacchetto, getConnection());
        Scene scene = new Scene(view.getRoot(), App.WIDTH, App.HEIGHT);
        scene.getStylesheets().add(App.class.getResource(App.STYLESHEET).toExternalForm());
        stage.setTitle("Travel Easy - Pagamento modifica prenotazione");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
}

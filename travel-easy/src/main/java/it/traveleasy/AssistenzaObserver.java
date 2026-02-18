package it.traveleasy;

public interface AssistenzaObserver {
    void onAssistenzaChanged(Prenotazione prenotazione, Viaggiatore viaggiatore, String tipoAssistenza, boolean valore);
}


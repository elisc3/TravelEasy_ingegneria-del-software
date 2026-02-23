package it.traveleasy;

public interface OffertaObserver {
    void onOffertaCreata(OffertaSpeciale offerta);
    void onOffertaEliminata(PacchettoViaggio pacchetto);
}


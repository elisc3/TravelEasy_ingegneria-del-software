INSERT INTO Account (id, Email, Password, Ruolo) VALUES
    (1, 'cliente@example.com', 'pwd123', 'Cliente'),
    (2, 'operatore@example.com', 'admin123', 'Operatore');

INSERT INTO Utenti (id, Nome, Cognome, Telefono, Ruolo, Account) VALUES
    (1, 'Mario', 'Rossi', '333111222', 'Cliente', 1),
    (2, 'Anna', 'Bianchi', '333222333', 'Operatore', 2);

INSERT INTO PortafoglioVirtuale (id, Utente, Saldo) VALUES
    (1, 1, 120.00),
    (2, 2, 0.00);

INSERT INTO CartaCredito (id, Utente, NumeroCarta, Scadenza, cvv, Circuito, PortafoglioVirtuale) VALUES
    (1, 1, '4111111111111111', '12/29', '123', 'VISA', 1),
    (2, 2, '', '', '', '', 2);

INSERT INTO PortafoglioOre (id, proprietario, Utente, Ore, Sconto) VALUES
    (1, 1, 1, 15.0, 3),
    (2, 2, 2, 0.0, 0);

INSERT INTO CompagniaTrasporto (id, Nome, TIPO) VALUES
    (1, 'SkyItalia', 'Aereo'),
    (2, 'RailFast', 'Treno');

INSERT INTO Alloggio (id, Nome, Indirizzo, TIPO, Stelle) VALUES
    (1, 'Hotel Laguna', 'Via Mare 10', 'Hotel', 4),
    (2, 'City Apartments', 'Corso Centro 22', 'Appartamento', 3);

INSERT INTO PacchettiViaggio
    (id, Codice, Titolo, "Città", Nazione, DataPartenza, DataRitorno, Descrizione, Prezzo, OreViaggio, "Visibilità", CompagniaTrasporto, Alloggio)
VALUES
    (1, 'PKG1001', 'Amsterdam Smart', 'Amsterdam', 'Paesi Bassi', '17-04-2026', '20-04-2026', 'Weekend ad Amsterdam', 1200.00, 3.5, 1, 1, 1),
    (2, 'PKG1002', 'Roma Budget', 'Roma', 'Italia', '10-05-2026', '14-05-2026', 'Roma economica', 480.00, 1.5, 1, 2, 2),
    (3, 'PKG1003', 'Tokyo Hidden', 'Tokyo', 'Giappone', '01-06-2026', '10-06-2026', 'Tour Giappone', 1900.00, 12.0, 0, 1, 1);

INSERT INTO OffertaSpeciale (id, Pacchetto, ScontoPercentuale, PrezzoScontato, DataFine, "Disponibilità") VALUES
    (1, 1, 25.0, 900.0, '16-04-2026', 8);

INSERT INTO Prenotazioni (id, Utente, Pacchetto, DataPrenotazione, PrezzoTotale, ScontoApplicato, OffertaSpeciale, PrezzoAssistenzaSpeciale, CheckIn) VALUES
    (1, 1, 1, '16-02-2026', 900.0, 3.0, 25.0, 0.0, 0);

INSERT INTO Viaggiatore (id, Nome, Cognome, DataNascita, TipoDocumento, CodiceDocumento, Prenotazione, SediaRotelle, "Cecità") VALUES
    (1, 'Mario', 'Rossi', '01-01-1990', 'Passaporto', 'AA1234567', 1, 0, 0),
    (2, 'Luigi', 'Verdi', '12-07-1988', 'Patente di guida', 'AB12345678', 1, 0, 0);

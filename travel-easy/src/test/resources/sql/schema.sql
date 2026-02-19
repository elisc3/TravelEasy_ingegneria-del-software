PRAGMA foreign_keys = OFF;

DROP TABLE IF EXISTS Viaggiatore;
DROP TABLE IF EXISTS Prenotazioni;
DROP TABLE IF EXISTS Recensione;
DROP TABLE IF EXISTS OffertaSpeciale;
DROP TABLE IF EXISTS PacchettiViaggio;
DROP TABLE IF EXISTS Alloggio;
DROP TABLE IF EXISTS CompagniaTrasporto;
DROP TABLE IF EXISTS CartaCredito;
DROP TABLE IF EXISTS PortafoglioVirtuale;
DROP TABLE IF EXISTS PortafoglioOre;
DROP TABLE IF EXISTS Utenti;
DROP TABLE IF EXISTS Account;

CREATE TABLE Account (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    Email TEXT NOT NULL UNIQUE,
    Password TEXT NOT NULL,
    Ruolo TEXT NOT NULL
);

CREATE TABLE Utenti (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    Nome TEXT NOT NULL,
    Cognome TEXT NOT NULL,
    Telefono TEXT NOT NULL,
    Ruolo TEXT NOT NULL,
    Account INTEGER NOT NULL
);

CREATE TABLE PortafoglioVirtuale (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    Utente INTEGER NOT NULL,
    Saldo REAL NOT NULL DEFAULT 0.0
);

CREATE TABLE CartaCredito (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    Utente INTEGER NOT NULL,
    NumeroCarta TEXT DEFAULT '',
    Scadenza TEXT DEFAULT '',
    cvv TEXT DEFAULT '',
    Circuito TEXT DEFAULT '',
    PortafoglioVirtuale INTEGER NOT NULL
);

CREATE TABLE PortafoglioOre (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    proprietario INTEGER,
    Utente INTEGER,
    Ore REAL NOT NULL DEFAULT 0.0,
    Sconto INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE CompagniaTrasporto (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    Nome TEXT NOT NULL,
    TIPO TEXT NOT NULL
);

CREATE TABLE Alloggio (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    Nome TEXT NOT NULL,
    Indirizzo TEXT NOT NULL,
    TIPO TEXT NOT NULL,
    Stelle INTEGER NOT NULL
);

CREATE TABLE PacchettiViaggio (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    Codice TEXT NOT NULL UNIQUE,
    Titolo TEXT NOT NULL,
    "Città" TEXT NOT NULL,
    Nazione TEXT NOT NULL,
    DataPartenza TEXT NOT NULL,
    DataRitorno TEXT NOT NULL,
    Descrizione TEXT NOT NULL,
    Prezzo REAL NOT NULL,
    OreViaggio REAL NOT NULL DEFAULT 0.0,
    "Visibilità" INTEGER NOT NULL DEFAULT 1,
    CompagniaTrasporto INTEGER NOT NULL,
    Alloggio INTEGER NOT NULL
);

CREATE TABLE OffertaSpeciale (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    Pacchetto INTEGER NOT NULL,
    ScontoPercentuale REAL NOT NULL,
    PrezzoScontato REAL NOT NULL,
    DataFine TEXT NOT NULL,
    "Disponibilità" INTEGER NOT NULL
);

CREATE TABLE Prenotazioni (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    Utente INTEGER NOT NULL,
    Pacchetto INTEGER NOT NULL,
    DataPrenotazione TEXT NOT NULL,
    PrezzoTotale REAL NOT NULL DEFAULT 0.0,
    ScontoApplicato REAL NOT NULL DEFAULT 0.0,
    OffertaSpeciale REAL NOT NULL DEFAULT 0.0,
    PrezzoAssistenzaSpeciale REAL NOT NULL DEFAULT 0.0,
    CheckIn INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE Viaggiatore (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    Nome TEXT NOT NULL,
    Cognome TEXT NOT NULL,
    DataNascita TEXT NOT NULL,
    TipoDocumento TEXT NOT NULL,
    CodiceDocumento TEXT NOT NULL,
    Prenotazione INTEGER NOT NULL,
    SediaRotelle INTEGER NOT NULL DEFAULT 0,
    "Cecità" INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE Recensione (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    Riferimento TEXT NOT NULL,
    Stelle INTEGER NOT NULL,
    Commento TEXT NOT NULL,
    Cliente INTEGER NOT NULL,
    Prenotazione INTEGER NOT NULL,
    DataRecensione TEXT NOT NULL
);

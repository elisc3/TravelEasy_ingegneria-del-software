package it.traveleasy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.Iterator;


import javax.swing.JOptionPane;


public class TravelEasy {
    private Map<String, Account> elencoAccount;
    //private Map<Integer, PortafoglioVirtuale> mappaPortafogli;
    private Connection conn;
    private Map<Integer, CompagniaTrasporto> elencoCompagnie;
    private Map<Integer, Alloggio> elencoAlloggi;
    private Map<Integer, CartaCredito> elencoCarte;
    private Map<Integer, PacchettoViaggio> elencoPacchetti;
    private Map<PacchettoViaggio, OffertaSpeciale> elencoOfferte;

    public TravelEasy(Connection conn){
        this.conn = conn;

        
    }
}

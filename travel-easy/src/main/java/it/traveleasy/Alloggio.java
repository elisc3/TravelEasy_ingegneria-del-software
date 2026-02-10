package it.traveleasy;

public class Alloggio {
    private int id;
    private String nome;
    private String indirizzo;
    private String tipo;
    private int stelle;

    public Alloggio(int id, String nome, String indirizzo, String tipo, int stelle) {
        this.id = id;
        this.nome = nome;
        this.indirizzo = indirizzo;
        this.tipo = tipo;
        this.stelle = stelle;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getStelle() {
        return stelle;
    }

    public void setStelle(int stelle) {
        this.stelle = stelle;
    }
}


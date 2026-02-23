package it.traveleasy;

public class PortafoglioVirtuale {
    private int id;
    private int utenteId;
    private double saldo;

    public PortafoglioVirtuale(int id, int utenteId, double saldo) {
        this.id = id;
        this.utenteId = utenteId;
        this.saldo = saldo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUtenteId() {
        return utenteId;
    }

    public void setUtenteId(int utenteId) {
        this.utenteId = utenteId;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public void incrementaSaldo(float importo){
        this.saldo += importo;
    }

    public void decrementaSaldo(float importo){
        this.saldo -= importo;
    }
}


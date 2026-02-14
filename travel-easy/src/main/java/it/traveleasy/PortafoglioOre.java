package it.traveleasy;

public class PortafoglioOre {
    private int id;
    private int utenteId;
    private float  ore;
    private int sconto;

    public PortafoglioOre(int id, int utenteId, float ore, int sconto) {
        this.id = id;
        this.utenteId = utenteId;
        this.ore = ore;
        this.sconto = sconto;
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

    public float getOre() {
        return ore;
    }

    public void setOre(float ore) {
        this.ore = ore;
    }

    public int getSconto() {
        return sconto;
    }

    public void setSconto(int sconto) {
        this.sconto = sconto;
    }
}

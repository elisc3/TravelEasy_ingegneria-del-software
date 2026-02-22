package it.traveleasy;
import java.sql.Connection;

public class PortafoglioOre {
    private int id;
    private int utenteId;
    private float  ore;
    private float sconto;

    public PortafoglioOre(int id, int utenteId, float ore, float sconto) {
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

    public float getSconto() {
        return sconto;
    }

    public void setSconto(float sconto) {
        this.sconto = sconto;
    }

    public boolean incrementaOre(Connection conn, float ore) {
        this.ore += ore;
        aggiornaSconto();

        if(!applicaScontoDB(conn)) {
            return false;
        }
        
        return true;
    }

    public boolean aggiornaSconto() {
        if(this.ore >= 10) {
            int n = (int) (this.ore / 10);
            this.sconto = n * 3;
            this.ore -= n*10;
        }

        return true;
    }

    public boolean decrementaOre(Connection conn, float ore) {
        float oreSconto = this.sconto / 3 * 10;
        this.ore += oreSconto;
        this.ore -= ore;
        if (ore < 0)
            this.ore = 0;
        aggiornaSconto();

        if(!applicaScontoDB(conn)) {
            return false;
        }
        
        return true;
    }

    public boolean applicaScontoDB(Connection conn){
        return PortafoglioOreDao.INSTANCE.update(conn, this.id, this.ore, this.sconto);
    }   
}

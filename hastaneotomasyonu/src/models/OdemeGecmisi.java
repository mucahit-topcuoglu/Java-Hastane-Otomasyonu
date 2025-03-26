package models;

import java.math.BigDecimal;
import java.util.Date;

public class OdemeGecmisi extends Model {
    
    private int faturaId;
    private BigDecimal odemeMiktari;
    private Date odemeTarihi;

    public OdemeGecmisi(int id, int faturaId, BigDecimal odemeMiktari, Date odemeTarihi) {
        super(id);
        this.faturaId = faturaId;
        this.odemeMiktari = odemeMiktari;
        this.odemeTarihi = odemeTarihi;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getFaturaId() { return faturaId; }
    public void setFaturaId(int faturaId) { this.faturaId = faturaId; }

    public BigDecimal getOdemeMiktari() { return odemeMiktari; }
    public void setOdemeMiktari(BigDecimal odemeMiktari) { this.odemeMiktari = odemeMiktari; }

    public Date getOdemeTarihi() { return odemeTarihi; }
    public void setOdemeTarihi(Date odemeTarihi) { this.odemeTarihi = odemeTarihi; }
} 
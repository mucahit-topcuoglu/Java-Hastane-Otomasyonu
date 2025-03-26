package models;

import java.util.Date;
import java.sql.Time;

public class Randevu extends Model {
    private int hastaId;
    private int doktorId;
    private Date tarih;
    private Time saat;
    private String durum;

    public Randevu(int id, int hastaId, int doktorId, Date tarih, Time saat) {
        super(id);
        this.hastaId = hastaId;
        this.doktorId = doktorId;
        this.tarih = tarih;
        this.saat = saat;
    }

    public int getHastaId() { return hastaId; }
    public void setHastaId(int hastaId) { this.hastaId = hastaId; }

    public int getDoktorId() { return doktorId; }
    public void setDoktorId(int doktorId) { this.doktorId = doktorId; }

    public Date getTarih() { return tarih; }
    public void setTarih(Date tarih) { this.tarih = tarih; }

    public Time getSaat() { return saat; }
    public void setSaat(Time saat) { this.saat = saat; }

    public String getDurum() { return durum; }
    public void setDurum(String durum) { this.durum = durum; }
} 
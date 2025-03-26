package models;

import java.util.Date;

public class Recete extends Model {
    private int hastaId;
    private int doktorId;
    private int ilacId;
    private int adet;
    private Integer labSonucId;
    private String notlar;
    private Date tarih;

    public Recete(int id, int hastaId, int doktorId, int ilacId, int adet, String notlar, Date tarih) {
        super(id);
        this.hastaId = hastaId;
        this.doktorId = doktorId;
        this.ilacId = ilacId;
        this.adet = adet;
        this.notlar = notlar;
        this.tarih = tarih;
    }

    public int getHastaId() { return hastaId; }
    public void setHastaId(int hastaId) { this.hastaId = hastaId; }

    public int getDoktorId() { return doktorId; }
    public void setDoktorId(int doktorId) { this.doktorId = doktorId; }

    public int getIlacId() { return ilacId; }
    public void setIlacId(int ilacId) { this.ilacId = ilacId; }

    public int getAdet() { return adet; }
    public void setAdet(int adet) { this.adet = adet; }

    public Integer getLabSonucId() { return labSonucId; }
    public void setLabSonucId(Integer labSonucId) { this.labSonucId = labSonucId; }

    public String getNotlar() { return notlar; }
    public void setNotlar(String notlar) { this.notlar = notlar; }

    public Date getTarih() { return tarih; }
    public void setTarih(Date tarih) { this.tarih = tarih; }
} 
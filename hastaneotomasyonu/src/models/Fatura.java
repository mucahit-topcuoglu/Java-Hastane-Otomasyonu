package models;

import java.util.Date;

public class Fatura extends Model {
    
    private int hastaId;
    private String tedaviBilgileri;
    private double masraf;
    private String odemeDurumu;
    private Date tarih;

    public Fatura(int id, int hastaId, String tedaviBilgileri, double masraf) {
        super(id);
        this.hastaId = hastaId;
        this.tedaviBilgileri = tedaviBilgileri;
        this.masraf = masraf;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getHastaId() { return hastaId; }
    public void setHastaId(int hastaId) { this.hastaId = hastaId; }
    
    public String getTedaviBilgileri() { return tedaviBilgileri; }
    public void setTedaviBilgileri(String tedaviBilgileri) { this.tedaviBilgileri = tedaviBilgileri; }
    
    public double getMasraf() { return masraf; }
    public void setMasraf(double masraf) { this.masraf = masraf; }
    
    public String getOdemeDurumu() { return odemeDurumu; }
    public void setOdemeDurumu(String odemeDurumu) { this.odemeDurumu = odemeDurumu; }
    
    public Date getTarih() { return tarih; }
    public void setTarih(Date tarih) { this.tarih = tarih; }
} 
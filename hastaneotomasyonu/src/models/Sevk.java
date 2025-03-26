package models;

import java.util.Date;

public class Sevk  extends Model{
   
    private int hastaId;
    private String sevkNedeni;
    private String hedefHastane;
    private Date tarih;

    public Sevk(int id, int hastaId, String sevkNedeni, String hedefHastane, Date tarih) {
        super(id);
        this.hastaId = hastaId;
        this.sevkNedeni = sevkNedeni;
        this.hedefHastane = hedefHastane;
        this.tarih = tarih;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getHastaId() { return hastaId; }
    public void setHastaId(int hastaId) { this.hastaId = hastaId; }

    public String getSevkNedeni() { return sevkNedeni; }
    public void setSevkNedeni(String sevkNedeni) { this.sevkNedeni = sevkNedeni; }

    public String getHedefHastane() { return hedefHastane; }
    public void setHedefHastane(String hedefHastane) { this.hedefHastane = hedefHastane; }

    public Date getTarih() { return tarih; }
    public void setTarih(Date tarih) { this.tarih = tarih; }
} 
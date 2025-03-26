package models;

import java.util.Date;

public class LabSonuc extends Model {
   
    private int hastaId;
    private String testTuru;
    private String sonuc;
    private Date tarih;
    private Integer doktorId; // nullable

    public LabSonuc(int id, int hastaId, String testTuru, String sonuc, Date tarih) {
        super(id);
        this.id = id;
        this.hastaId = hastaId;
        this.testTuru = testTuru;
        this.sonuc = sonuc;
        this.tarih = tarih;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getHastaId() { return hastaId; }
    public void setHastaId(int hastaId) { this.hastaId = hastaId; }

    public String getTestTuru() { return testTuru; }
    public void setTestTuru(String testTuru) { this.testTuru = testTuru; }

    public String getSonuc() { return sonuc; }
    public void setSonuc(String sonuc) { this.sonuc = sonuc; }

    public Date getTarih() { return tarih; }
    public void setTarih(Date tarih) { this.tarih = tarih; }

    public Integer getDoktorId() { return doktorId; }
    public void setDoktorId(Integer doktorId) { this.doktorId = doktorId; }
} 
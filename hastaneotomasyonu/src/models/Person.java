package models;

public abstract class Person extends Model {
    protected String tcno;
    protected String sifre;
    protected String adSoyad;

    public Person(int id, String tcno, String sifre, String adSoyad) {
        super(id);
        this.tcno = tcno;
        this.sifre = sifre;
        this.adSoyad = adSoyad;
    }

    public String getTcno() { return tcno; }
    public void setTcno(String tcno) { this.tcno = tcno; }

    public String getSifre() { return sifre; }
    public void setSifre(String sifre) { this.sifre = sifre; }

    public String getAdSoyad() { return adSoyad; }
    public void setAdSoyad(String adSoyad) { this.adSoyad = adSoyad; }

    // Abstract metodlar
    public abstract String getRol();
} 
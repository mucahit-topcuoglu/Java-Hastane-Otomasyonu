package models;

import java.util.Date;

public class Hasta extends Person {
    private Date dogumTarihi;
    private String iletisimBilgileri;

    public Hasta(int id, String tcno, String sifre, String adSoyad, Date dogumTarihi, String iletisimBilgileri) {
        super(id, tcno, sifre, adSoyad);
        this.dogumTarihi = dogumTarihi;
        this.iletisimBilgileri = iletisimBilgileri;
    }

    public Date getDogumTarihi() { return dogumTarihi; }
    public void setDogumTarihi(Date dogumTarihi) { this.dogumTarihi = dogumTarihi; }

    public String getIletisimBilgileri() { return iletisimBilgileri; }
    public void setIletisimBilgileri(String iletisimBilgileri) { this.iletisimBilgileri = iletisimBilgileri; }

    @Override
    public String getRol() {
        return "HASTA";
    }
} 
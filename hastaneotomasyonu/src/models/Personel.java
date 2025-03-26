package models;

public class Personel extends Person {
    private String departman;
    private String sifre;

    public Personel(int id, String tckno, String ad, String soyad) {
        super(id, tckno, ad, soyad);
    }

    public String getDepartman() {
        return departman;
    }

    public void setDepartman(String departman) {
        this.departman = departman;
    }

    public String getSifre() {
        return sifre;
    }

    public void setSifre(String sifre) {
        this.sifre = sifre;
    }

    @Override
    public String getRol() {
        return "Personel";
    }
} 
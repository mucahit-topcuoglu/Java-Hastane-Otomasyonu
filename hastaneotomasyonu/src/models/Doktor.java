package models;

public class Doktor extends Person {
    private String uzmanlikAlani;

    public Doktor(int id, String tcno, String sifre, String adSoyad) {
        super(id, tcno, sifre, adSoyad);
    }

    public String getUzmanlikAlani() { return uzmanlikAlani; }
    public void setUzmanlikAlani(String uzmanlikAlani) { this.uzmanlikAlani = uzmanlikAlani; }

    @Override
    public String getRol() {
        return "DOKTOR";
    }
    
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return adSoyad;
    }
} 
package models;


public class Ilac {
    private int id;
    private String ilacAdi;
    private int stok;
    private double fiyat;

    public Ilac(int id, String ilacAdi, int stok, Double fiyat) {
        this.id = id;
        this.ilacAdi = ilacAdi;
        this.stok = stok;
        this.fiyat = fiyat;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getIlacAdi() { return ilacAdi; }
    public void setIlacAdi(String ilacAdi) { this.ilacAdi = ilacAdi; }

    public int getStok() { return stok; }
    public void setStok(int stok) { this.stok = stok; }

    public double getFiyat() { return fiyat; }
    public void setFiyat(Double fiyat) { this.fiyat = fiyat; }
    @Override
    public String toString() {
        
        return ilacAdi;
    }
} 
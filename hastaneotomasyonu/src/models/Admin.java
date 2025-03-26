package models;

public class Admin extends Person {
    public Admin(int id, String tcno, String sifre, String adSoyad) {
        super(id, tcno, sifre, adSoyad);
    }

    @Override
    public String getRol() {
        return "ADMIN";
    }
} 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

 class VeritabaniBaglantisi {
    public static Connection baglan() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/hastane";
        String kullaniciAdi = "root";
        String sifre = "1234";


        return DriverManager.getConnection(url, kullaniciAdi, sifre);
    }
}
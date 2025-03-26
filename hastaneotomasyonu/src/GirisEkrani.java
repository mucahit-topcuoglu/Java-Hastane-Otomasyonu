import javax.swing.*;
import java.awt.*;

public class GirisEkrani {
    public GirisEkrani() {
        JFrame pencere = new JFrame("Giriş Ekranı");
        UIStyle.styleFrame(pencere);
        pencere.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        UIStyle.stylePanel(panel);

        JButton hastaGirisButonu = UIStyle.createStyledButton("Hasta Girişi");
        JButton doktorGirisButonu = UIStyle.createStyledButton("Doktor Girişi");
        JButton adminGirisButonu = UIStyle.createStyledButton("Admin Girişi");
        JButton personelGirisButonu = UIStyle.createStyledButton("Personel Girişi");

        hastaGirisButonu.addActionListener(e -> {
            pencere.dispose();
            new HastaGiris();
        });
        personelGirisButonu.addActionListener(e -> {
            pencere.dispose();
            new PersonelGiris();
        });

        doktorGirisButonu.addActionListener(e -> {
            pencere.dispose();
            new DoktorGiris();
        });

        adminGirisButonu.addActionListener(e -> {
            pencere.dispose();
            new AdminGiris();
        });

        panel.add(hastaGirisButonu);
        panel.add(doktorGirisButonu);
        panel.add(adminGirisButonu);
        panel.add(personelGirisButonu);
        
        pencere.add(panel);
        pencere.setVisible(true);
    }
}

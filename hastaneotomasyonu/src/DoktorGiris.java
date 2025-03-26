import javax.swing.*;
import java.awt.*;
import java.sql.*;
import models.Doktor;

public class DoktorGiris {
    private JFrame pencere;
    private JTextField tcknoAlani;
    private JPasswordField sifreAlani;

    public DoktorGiris() {
        pencere = new JFrame("Doktor Girişi");
        UIStyle.styleFrame(pencere);
        pencere.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pencere.setLayout(new BorderLayout(20, 20));
        pencere.setMinimumSize(new Dimension(400, 300));

        JPanel panel = new JPanel(new GridLayout(3, 2, 15, 15));
        UIStyle.stylePanel(panel);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        panel.add(new JLabel("TCKNO:"));
        tcknoAlani = new JTextField();
        tcknoAlani.setPreferredSize(new Dimension(200, 30));
        panel.add(tcknoAlani);

        panel.add(new JLabel("Şifre:"));
        sifreAlani = new JPasswordField();
        sifreAlani.setPreferredSize(new Dimension(200, 30));
        panel.add(sifreAlani);

        JButton girisButon = UIStyle.createStyledButton("Giriş");
        girisButon.addActionListener(e -> girisYap());
        panel.add(new JLabel());
        panel.add(girisButon);

        JPanel butonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        UIStyle.stylePanel(butonPanel);
        
        JButton geriButonu = UIStyle.createStyledButton("Geri Dön");
        geriButonu.addActionListener(e -> {
            new GirisEkrani();
            pencere.dispose();
        });

        butonPanel.add(geriButonu);
        pencere.add(panel, BorderLayout.CENTER);
        pencere.add(butonPanel, BorderLayout.SOUTH);
        pencere.pack();
        pencere.setLocationRelativeTo(null);
        pencere.setVisible(true);
    }

    private void girisYap() {
        String tckno = tcknoAlani.getText().trim();
        String sifre = new String(sifreAlani.getPassword());

        if (tckno.isEmpty() || sifre.isEmpty()) {
            JOptionPane.showMessageDialog(pencere, 
                "TCKNO ve Şifre alanları boş bırakılamaz.",
                "Uyarı", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
            String sorgu = "SELECT * FROM doktorlar WHERE tckno = ? AND sifre = ?";
            PreparedStatement stmt = baglanti.prepareStatement(sorgu);
            stmt.setString(1, tckno);
            stmt.setString(2, sifre);
            ResultSet sonuc = stmt.executeQuery();

            if (sonuc.next()) {
                Doktor doktor = new Doktor(
                    sonuc.getInt("id"),
                    sonuc.getString("tckno"),
                    sonuc.getString("sifre"),
                    sonuc.getString("ad_soyad")
                );
                doktor.setUzmanlikAlani(sonuc.getString("uzmanlik"));

                JOptionPane.showMessageDialog(pencere, 
                    "Hoş geldiniz, Dr. " + doktor.getAdSoyad());
                pencere.dispose();
                new DoktorPanel(doktor);
            } else {
                JOptionPane.showMessageDialog(pencere, 
                    "Geçersiz TCKNO veya Şifre.", 
                    "Hata", 
                    JOptionPane.ERROR_MESSAGE);
                sifreAlani.setText("");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(pencere, 
                "Veritabanı hatası: " + e.getMessage(), 
                "Hata", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}

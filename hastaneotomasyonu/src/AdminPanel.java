import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import models.Admin;
import models.Hasta;
import models.Doktor;
import models.Ilac;

public class AdminPanel {
    private final JFrame pencere;
    private JTable hastaTablosu, doktorTablosu, ilacTablosu;
    private DefaultTableModel hastaModel, doktorModel, ilacModel;
    private final Admin admin;

    public AdminPanel(Admin admin) {
        this.admin = admin;
        this.pencere = new JFrame("Admin Paneli - " + admin.getAdSoyad());
        UIStyle.styleFrame(pencere);
        pencere.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pencere.setSize(800, 600);

        initComponents();
        loadData();
    }

    private void initComponents() {
        JTabbedPane sekmeler = new JTabbedPane();
        UIStyle.styleTabbedPane(sekmeler);

        JPanel hastalarPaneli = new JPanel(new BorderLayout(10, 10));
        UIStyle.stylePanel(hastalarPaneli);
        hastalarPaneli.add(hastaTablosuOlustur(), BorderLayout.CENTER);

        JPanel hastaButonPaneli = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        UIStyle.stylePanel(hastaButonPaneli);
        JButton hastaEkleButonu = UIStyle.createStyledButton("Hasta Ekle");
        JButton hastaDuzenleButonu = UIStyle.createStyledButton("Hasta Düzenle");
        JButton hastaSilButonu = UIStyle.createStyledButton("Hasta Sil");

        hastaButonPaneli.add(hastaEkleButonu);
        hastaButonPaneli.add(hastaDuzenleButonu);
        hastaButonPaneli.add(hastaSilButonu);
        hastalarPaneli.add(hastaButonPaneli, BorderLayout.SOUTH);
        sekmeler.addTab("Hastalar", hastalarPaneli);

        JPanel doktorlarPaneli = new JPanel(new BorderLayout(10, 10));
        UIStyle.stylePanel(doktorlarPaneli);
        doktorlarPaneli.add(doktorTablosuOlustur(), BorderLayout.CENTER);

        JPanel doktorButonPaneli = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        UIStyle.stylePanel(doktorButonPaneli);
        JButton doktorEkleButonu = UIStyle.createStyledButton("Doktor Ekle");
        JButton doktorDuzenleButonu = UIStyle.createStyledButton("Doktor Düzenle");
        JButton doktorSilButonu = UIStyle.createStyledButton("Doktor Sil");

        doktorButonPaneli.add(doktorEkleButonu);
        doktorButonPaneli.add(doktorDuzenleButonu);
        doktorButonPaneli.add(doktorSilButonu);
        doktorlarPaneli.add(doktorButonPaneli, BorderLayout.SOUTH);
        sekmeler.addTab("Doktorlar", doktorlarPaneli);

        JPanel ilaclarPaneli = new JPanel(new BorderLayout(10, 10));
        UIStyle.stylePanel(ilaclarPaneli);
        ilaclarPaneli.add(ilacTablosuOlustur(), BorderLayout.CENTER);

        JPanel ilacButonPaneli = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        UIStyle.stylePanel(ilacButonPaneli);
        JButton ilacEkleButonu = UIStyle.createStyledButton("İlaç Ekle");
        JButton ilacDuzenleButonu = UIStyle.createStyledButton("İlaç Düzenle");
        JButton ilacSilButonu = UIStyle.createStyledButton("İlaç Sil");

        ilacButonPaneli.add(ilacEkleButonu);
        ilacButonPaneli.add(ilacDuzenleButonu);
        ilacButonPaneli.add(ilacSilButonu);
        ilaclarPaneli.add(ilacButonPaneli, BorderLayout.SOUTH);
        sekmeler.addTab("İlaçlar", ilaclarPaneli);

        pencere.add(sekmeler, BorderLayout.CENTER);

        JPanel altPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        UIStyle.stylePanel(altPanel);
        JButton geriButonu = UIStyle.createStyledButton("Ana Sayfaya Dön");
        geriButonu.addActionListener(e -> {
            new GirisEkrani();
            pencere.dispose();
        });
        altPanel.add(geriButonu);
        pencere.add(altPanel, BorderLayout.SOUTH);

        hastaEkleButonu.addActionListener(e -> hastaEkle());
        hastaDuzenleButonu.addActionListener(e -> hastaDuzenle());
        hastaSilButonu.addActionListener(e -> hastaSil());

        doktorEkleButonu.addActionListener(e -> doktorEkle());
        doktorDuzenleButonu.addActionListener(e -> doktorDuzenle());
        doktorSilButonu.addActionListener(e -> doktorSil());

        ilacEkleButonu.addActionListener(e -> ilacEkle());
        ilacDuzenleButonu.addActionListener(e -> ilacDuzenle());
        ilacSilButonu.addActionListener(e -> ilacSil());

        pencere.setLocationRelativeTo(null);
        pencere.setVisible(true);
    }

    private void loadData() {
        tablolariGuncelle();
    }

    private void tablolariGuncelle() {
        hastaTablosunuGuncelle();
        doktorTablosunuGuncelle();
        ilacTablosunuGuncelle();
    }

    private void hastaTablosunuGuncelle() {
        hastaModel.setRowCount(0);
        try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
            String sorgu = "SELECT * FROM hastalar ORDER BY ad, soyad";
            ResultSet sonuc = baglanti.createStatement().executeQuery(sorgu);
            
            while (sonuc.next()) {
                Hasta hasta = new Hasta(
                    sonuc.getInt("id"),
                    sonuc.getString("tckno"),
                    sonuc.getString("sifre"),
                    sonuc.getString("ad") + " " + sonuc.getString("soyad"),
                    sonuc.getDate("dogum_tarihi"),
                    sonuc.getString("iletisim_bilgileri")
                );

                hastaModel.addRow(new Object[]{
                    hasta.getId(),
                    hasta.getTcno(),
                    hasta.getAdSoyad().split(" ")[0],
                    hasta.getAdSoyad().split(" ")[1],
                    hasta.getDogumTarihi(),
                    hasta.getIletisimBilgileri()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(pencere, 
                "Hasta bilgileri yüklenirken hata: " + e.getMessage(),
                "Hata", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doktorTablosunuGuncelle() {
        doktorModel.setRowCount(0);
        try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
            String sorgu = "SELECT * FROM doktorlar ORDER BY ad_soyad";
            PreparedStatement stmt = baglanti.prepareStatement(sorgu);
            ResultSet sonuc = stmt.executeQuery();

            while (sonuc.next()) {
                Doktor doktor = new Doktor(
                    sonuc.getInt("id"),
                    sonuc.getString("tckno"),
                    sonuc.getString("sifre"),
                    sonuc.getString("ad_soyad")
                );
                doktor.setUzmanlikAlani(sonuc.getString("uzmanlik"));

                doktorModel.addRow(new Object[]{
                    doktor.getId(),
                    doktor.getTcno(),
                    doktor.getAdSoyad(),
                    doktor.getUzmanlikAlani()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(pencere, 
                "Doktor bilgileri yüklenirken hata: " + e.getMessage(),
                "Hata", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ilacTablosunuGuncelle() {
        ilacModel.setRowCount(0);
        try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
            String sorgu = "SELECT * FROM ilaclar ORDER BY ilac_adi";
            ResultSet sonuc = baglanti.createStatement().executeQuery(sorgu);
            
            while (sonuc.next()) {
                Ilac ilac = new Ilac(
                    sonuc.getInt("id"),
                    sonuc.getString("ilac_adi"),
                    sonuc.getInt("stok"),
                    sonuc.getDouble("fiyat")
                );

                ilacModel.addRow(new Object[]{
                    ilac.getId(),
                    ilac.getIlacAdi(),
                    ilac.getStok(),
                    ilac.getFiyat()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(pencere, 
                "İlaç bilgileri yüklenirken hata: " + e.getMessage(),
                "Hata", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hastaEkle() {
        JDialog dialog = new JDialog(pencere, "Yeni Hasta Ekle", true);
        dialog.setLayout(new GridLayout(7, 2, 10, 10));
        dialog.setSize(400, 300);

        JTextField tcknoAlani = new JTextField();
        JTextField adAlani = new JTextField();
        JTextField soyadAlani = new JTextField();
        JTextField dogumTarihiAlani = new JTextField();
        JTextField iletisimAlani = new JTextField();
        JPasswordField sifreAlani = new JPasswordField();

        dialog.add(new JLabel("TCKNO:"));
        dialog.add(tcknoAlani);
        dialog.add(new JLabel("Ad:"));
        dialog.add(adAlani);
        dialog.add(new JLabel("Soyad:"));
        dialog.add(soyadAlani);
        dialog.add(new JLabel("Doğum Tarihi (YYYY-MM-DD):"));
        dialog.add(dogumTarihiAlani);
        dialog.add(new JLabel("İletişim Bilgileri:"));
        dialog.add(iletisimAlani);
        dialog.add(new JLabel("Şifre:"));
        dialog.add(sifreAlani);

        JButton kaydetButon = UIStyle.createStyledButton("Kaydet");
        kaydetButon.addActionListener(e -> {
            try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
                String sorgu = """
                    INSERT INTO hastalar (tckno, ad, soyad, dogum_tarihi, iletisim_bilgileri, sifre)
                    VALUES (?, ?, ?, ?, ?, ?)
                    """;
                PreparedStatement stmt = baglanti.prepareStatement(sorgu);
                stmt.setString(1, tcknoAlani.getText());
                stmt.setString(2, adAlani.getText());
                stmt.setString(3, soyadAlani.getText());
                stmt.setString(4, dogumTarihiAlani.getText());
                stmt.setString(5, iletisimAlani.getText());
                stmt.setString(6, new String(sifreAlani.getPassword()));
                
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(dialog, "Hasta başarıyla eklendi.");
                dialog.dispose();
                tablolariGuncelle();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Hata: " + ex.getMessage());
            }
        });

        dialog.add(kaydetButon);
        dialog.setLocationRelativeTo(pencere);
        dialog.setVisible(true);
    }

    private void hastaDuzenle() {
        int secilenSatir = hastaTablosu.getSelectedRow();
        if (secilenSatir == -1) {
            JOptionPane.showMessageDialog(pencere, "Lütfen düzenlenecek hastayı seçin.");
            return;
        }

        int hastaId = (int) hastaTablosu.getValueAt(secilenSatir, 0);
        
        try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
            String sorgu = "SELECT * FROM hastalar WHERE id = ?";
            PreparedStatement stmt = baglanti.prepareStatement(sorgu);
            stmt.setInt(1, hastaId);
            ResultSet sonuc = stmt.executeQuery();

            if (sonuc.next()) {
                JDialog dialog = new JDialog(pencere, "Hasta Düzenle", true);
                dialog.setLayout(new GridLayout(7, 2, 10, 10));
                dialog.setSize(400, 300);

                JTextField tcknoAlani = new JTextField(sonuc.getString("tckno"));
                JTextField adAlani = new JTextField(sonuc.getString("ad"));
                JTextField soyadAlani = new JTextField(sonuc.getString("soyad"));
                JTextField dogumTarihiAlani = new JTextField(sonuc.getString("dogum_tarihi"));
                JTextField iletisimAlani = new JTextField(sonuc.getString("iletisim_bilgileri"));
                JPasswordField sifreAlani = new JPasswordField();

                dialog.add(new JLabel("TCKNO:"));
                dialog.add(tcknoAlani);
                dialog.add(new JLabel("Ad:"));
                dialog.add(adAlani);
                dialog.add(new JLabel("Soyad:"));
                dialog.add(soyadAlani);
                dialog.add(new JLabel("Doğum Tarihi (YYYY-MM-DD):"));
                dialog.add(dogumTarihiAlani);
                dialog.add(new JLabel("İletişim Bilgileri:"));
                dialog.add(iletisimAlani);
                dialog.add(new JLabel("Yeni Şifre (Opsiyonel):"));
                dialog.add(sifreAlani);

                JButton kaydetButon = UIStyle.createStyledButton("Güncelle");
                kaydetButon.addActionListener(e -> {
                    try {
                        String guncellemeSorgusu = """
                            UPDATE hastalar 
                            SET tckno = ?, ad = ?, soyad = ?, 
                                dogum_tarihi = ?, iletisim_bilgileri = ?
                            WHERE id = ?
                            """;
                        PreparedStatement guncelle = baglanti.prepareStatement(guncellemeSorgusu);
                        guncelle.setString(1, tcknoAlani.getText());
                        guncelle.setString(2, adAlani.getText());
                        guncelle.setString(3, soyadAlani.getText());
                        guncelle.setString(4, dogumTarihiAlani.getText());
                        guncelle.setString(5, iletisimAlani.getText());
                        guncelle.setInt(6, hastaId);
                        
                        guncelle.executeUpdate();

                        String sifre = new String(sifreAlani.getPassword());
                        if (!sifre.isEmpty()) {
                            String sifreSorgusu = "UPDATE hastalar SET sifre = ? WHERE id = ?";
                            PreparedStatement sifreGuncelle = baglanti.prepareStatement(sifreSorgusu);
                            sifreGuncelle.setString(1, sifre);
                            sifreGuncelle.setInt(2, hastaId);
                            sifreGuncelle.executeUpdate();
                        }

                        JOptionPane.showMessageDialog(dialog, "Hasta bilgileri güncellendi.");
                        dialog.dispose();
                        tablolariGuncelle();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(dialog, "Hata: " + ex.getMessage());
                    }
                });

                dialog.add(kaydetButon);
                dialog.setLocationRelativeTo(pencere);
                dialog.setVisible(true);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(pencere, "Hata: " + ex.getMessage());
        }
    }

    private void hastaSil() {
        int secilenSatir = hastaTablosu.getSelectedRow();
        if (secilenSatir == -1) {
            JOptionPane.showMessageDialog(pencere, "Lütfen silinecek hastayı seçin.");
            return;
        }

        int hastaId = (int) hastaTablosu.getValueAt(secilenSatir, 0);
        String hastaAd = hastaTablosu.getValueAt(secilenSatir, 2).toString();
        String hastaSoyad = hastaTablosu.getValueAt(secilenSatir, 3).toString();

        int secim = JOptionPane.showConfirmDialog(
            pencere,
            hastaAd + " " + hastaSoyad + " isimli hastayı silmek istediğinizden emin misiniz?",
            "Hasta Sil",
            JOptionPane.YES_NO_OPTION
        );

        if (secim == JOptionPane.YES_OPTION) {
            try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
                String sorgu = "DELETE FROM hastalar WHERE id = ?";
                PreparedStatement stmt = baglanti.prepareStatement(sorgu);
                stmt.setInt(1, hastaId);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(pencere, "Hasta başarıyla silindi.");
                tablolariGuncelle();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(pencere, "Hata: " + e.getMessage());
            }
        }
    }

    private void doktorEkle() {
        JDialog dialog = new JDialog(pencere, "Yeni Doktor Ekle", true);
        dialog.setLayout(new GridLayout(6, 2, 10, 10));
        dialog.setSize(400, 300);

        JTextField tcknoAlani = new JTextField();
        JTextField adSoyadAlani = new JTextField();
        JTextField uzmanlikAlani = new JTextField();
        JPasswordField sifreAlani = new JPasswordField();

        dialog.add(new JLabel("TCKNO:"));
        dialog.add(tcknoAlani);
        dialog.add(new JLabel("Ad Soyad:"));
        dialog.add(adSoyadAlani);
        dialog.add(new JLabel("Uzmanlık:"));
        dialog.add(uzmanlikAlani);
        dialog.add(new JLabel("Şifre:"));
        dialog.add(sifreAlani);

        JButton kaydetButon = UIStyle.createStyledButton("Kaydet");
        kaydetButon.addActionListener(e -> {
            try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
                String sorgu = """
                    INSERT INTO doktorlar (tckno, ad_soyad, uzmanlik, sifre)
                    VALUES (?, ?, ?, ?)
                    """;
                PreparedStatement stmt = baglanti.prepareStatement(sorgu);
                stmt.setString(1, tcknoAlani.getText());
                stmt.setString(2, adSoyadAlani.getText());
                stmt.setString(3, uzmanlikAlani.getText());
                stmt.setString(4, new String(sifreAlani.getPassword()));
                
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(dialog, "Doktor başarıyla eklendi.");
                dialog.dispose();
                tablolariGuncelle();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Hata: " + ex.getMessage());
            }
        });

        dialog.add(kaydetButon);
        dialog.setLocationRelativeTo(pencere);
        dialog.setVisible(true);
    }

    private void doktorDuzenle() {
        int secilenSatir = doktorTablosu.getSelectedRow();
        if (secilenSatir == -1) {
            JOptionPane.showMessageDialog(pencere, "Lütfen düzenlenecek doktoru seçin.");
            return;
        }

        int doktorId = (int) doktorTablosu.getValueAt(secilenSatir, 0);
        
        try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
            String sorgu = "SELECT * FROM doktorlar WHERE id = ?";
            PreparedStatement stmt = baglanti.prepareStatement(sorgu);
            stmt.setInt(1, doktorId);
            ResultSet sonuc = stmt.executeQuery();

            if (sonuc.next()) {
                JDialog dialog = new JDialog(pencere, "Doktor Düzenle", true);
                dialog.setLayout(new GridLayout(6, 2, 10, 10));
                dialog.setSize(400, 300);

                JTextField tcknoAlani = new JTextField(sonuc.getString("tckno"));
                JTextField adSoyadAlani = new JTextField(sonuc.getString("ad_soyad"));
                JTextField uzmanlikAlani = new JTextField(sonuc.getString("uzmanlik"));
                JPasswordField sifreAlani = new JPasswordField();

                dialog.add(new JLabel("TCKNO:"));
                dialog.add(tcknoAlani);
                dialog.add(new JLabel("Ad Soyad:"));
                dialog.add(adSoyadAlani);
                dialog.add(new JLabel("Uzmanlık:"));
                dialog.add(uzmanlikAlani);
                dialog.add(new JLabel("Yeni Şifre (Opsiyonel):"));
                dialog.add(sifreAlani);

                JButton kaydetButon = UIStyle.createStyledButton("Güncelle");
                kaydetButon.addActionListener(e -> {
                    try {
                        String guncellemeSorgusu = """
                            UPDATE doktorlar 
                            SET tckno = ?, ad_soyad = ?, uzmanlik = ?
                            WHERE id = ?
                            """;
                        PreparedStatement guncelle = baglanti.prepareStatement(guncellemeSorgusu);
                        guncelle.setString(1, tcknoAlani.getText());
                        guncelle.setString(2, adSoyadAlani.getText());
                        guncelle.setString(3, uzmanlikAlani.getText());
                        guncelle.setInt(4, doktorId);
                        
                        guncelle.executeUpdate();

                        String sifre = new String(sifreAlani.getPassword());
                        if (!sifre.isEmpty()) {
                            String sifreSorgusu = "UPDATE doktorlar SET sifre = ? WHERE id = ?";
                            PreparedStatement sifreGuncelle = baglanti.prepareStatement(sifreSorgusu);
                            sifreGuncelle.setString(1, sifre);
                            sifreGuncelle.setInt(2, doktorId);
                            sifreGuncelle.executeUpdate();
                        }

                        JOptionPane.showMessageDialog(dialog, "Doktor bilgileri güncellendi.");
                        dialog.dispose();
                        tablolariGuncelle();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(dialog, "Hata: " + ex.getMessage());
                    }
                });

                dialog.add(kaydetButon);
                dialog.setLocationRelativeTo(pencere);
                dialog.setVisible(true);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(pencere, "Hata: " + ex.getMessage());
        }
    }

    private void doktorSil() {
        int secilenSatir = doktorTablosu.getSelectedRow();
        if (secilenSatir == -1) {
            JOptionPane.showMessageDialog(pencere, "Lütfen silinecek doktoru seçin.");
            return;
        }

        int doktorId = (int) doktorTablosu.getValueAt(secilenSatir, 0);
        String doktorAdSoyad = doktorTablosu.getValueAt(secilenSatir, 2).toString();

        int secim = JOptionPane.showConfirmDialog(
            pencere,
            "Dr. " + doktorAdSoyad + " isimli doktoru silmek istediğinizden emin misiniz?",
            "Doktor Sil",
            JOptionPane.YES_NO_OPTION
        );

        if (secim == JOptionPane.YES_OPTION) {
            try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
                String sorgu = "DELETE FROM doktorlar WHERE id = ?";
                PreparedStatement stmt = baglanti.prepareStatement(sorgu);
                stmt.setInt(1, doktorId);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(pencere, "Doktor başarıyla silindi.");
                tablolariGuncelle();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(pencere, "Hata: " + e.getMessage());
            }
        }
    }

    private void ilacEkle() {
        JDialog dialog = new JDialog(pencere, "Yeni İlaç Ekle", true);
        dialog.setLayout(new GridLayout(5, 2, 10, 10));
        dialog.setSize(400, 250);

        JTextField ilacAdiAlani = new JTextField();
        JTextField stokAlani = new JTextField();
        JTextField fiyatAlani = new JTextField();

        dialog.add(new JLabel("İlaç Adı:"));
        dialog.add(ilacAdiAlani);
        dialog.add(new JLabel("Stok:"));
        dialog.add(stokAlani);
        dialog.add(new JLabel("Fiyat:"));
        dialog.add(fiyatAlani);

        JButton kaydetButon = UIStyle.createStyledButton("Kaydet");
        kaydetButon.addActionListener(e -> {
            try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
                String sorgu = "INSERT INTO ilaclar (ilac_adi, stok, fiyat) VALUES (?, ?, ?)";
                PreparedStatement stmt = baglanti.prepareStatement(sorgu);
                stmt.setString(1, ilacAdiAlani.getText());
                stmt.setInt(2, Integer.parseInt(stokAlani.getText()));
                stmt.setDouble(3, Double.parseDouble(fiyatAlani.getText()));
                
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(dialog, "İlaç başarıyla eklendi.");
                dialog.dispose();
                tablolariGuncelle();
            } catch (SQLException | NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Hata: " + ex.getMessage());
            }
        });

        dialog.add(kaydetButon);
        dialog.setLocationRelativeTo(pencere);
        dialog.setVisible(true);
    }

    private void ilacDuzenle() {
        int secilenSatir = ilacTablosu.getSelectedRow();
        if (secilenSatir == -1) {
            JOptionPane.showMessageDialog(pencere, "Lütfen düzenlenecek ilacı seçin.");
            return;
        }

        int ilacId = (int) ilacTablosu.getValueAt(secilenSatir, 0);
        
        try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
            String sorgu = "SELECT * FROM ilaclar WHERE id = ?";
            PreparedStatement stmt = baglanti.prepareStatement(sorgu);
            stmt.setInt(1, ilacId);
            ResultSet sonuc = stmt.executeQuery();

            if (sonuc.next()) {
                JDialog dialog = new JDialog(pencere, "İlaç Düzenle", true);
                dialog.setLayout(new GridLayout(5, 2, 10, 10));
                dialog.setSize(400, 250);

                JTextField ilacAdiAlani = new JTextField(sonuc.getString("ilac_adi"));
                JTextField stokAlani = new JTextField(String.valueOf(sonuc.getInt("stok")));
                JTextField fiyatAlani = new JTextField(String.valueOf(sonuc.getDouble("fiyat")));

                dialog.add(new JLabel("İlaç Adı:"));
                dialog.add(ilacAdiAlani);
                dialog.add(new JLabel("Stok:"));
                dialog.add(stokAlani);
                dialog.add(new JLabel("Fiyat:"));
                dialog.add(fiyatAlani);

                JButton kaydetButon = UIStyle.createStyledButton("Güncelle");
                kaydetButon.addActionListener(e -> {
                    try {
                        String guncellemeSorgusu = """
                            UPDATE ilaclar 
                            SET ilac_adi = ?, stok = ?, fiyat = ?
                            WHERE id = ?
                            """;
                        PreparedStatement guncelle = baglanti.prepareStatement(guncellemeSorgusu);
                        guncelle.setString(1, ilacAdiAlani.getText());
                        guncelle.setInt(2, Integer.parseInt(stokAlani.getText()));
                        guncelle.setDouble(3, Double.parseDouble(fiyatAlani.getText()));
                        guncelle.setInt(4, ilacId);
                        
                        guncelle.executeUpdate();
                        JOptionPane.showMessageDialog(dialog, "İlaç bilgileri güncellendi.");
                        dialog.dispose();
                        tablolariGuncelle();
                    } catch (SQLException | NumberFormatException ex) {
                        JOptionPane.showMessageDialog(dialog, "Hata: " + ex.getMessage());
                    }
                });

                dialog.add(kaydetButon);
                dialog.setLocationRelativeTo(pencere);
                dialog.setVisible(true);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(pencere, "Hata: " + ex.getMessage());
        }
    }

    private void ilacSil() {
        int secilenSatir = ilacTablosu.getSelectedRow();
        if (secilenSatir == -1) {
            JOptionPane.showMessageDialog(pencere, "Lütfen silinecek ilacı seçin.");
            return;
        }

        int ilacId = (int) ilacTablosu.getValueAt(secilenSatir, 0);
        String ilacAdi = ilacTablosu.getValueAt(secilenSatir, 1).toString();

        int secim = JOptionPane.showConfirmDialog(
            pencere,
            ilacAdi + " isimli ilacı silmek istediğinizden emin misiniz?",
            "İlaç Sil",
            JOptionPane.YES_NO_OPTION
        );

        if (secim == JOptionPane.YES_OPTION) {
            try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
                String sorgu = "DELETE FROM ilaclar WHERE id = ?";
                PreparedStatement stmt = baglanti.prepareStatement(sorgu);
                stmt.setInt(1, ilacId);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(pencere, "İlaç başarıyla silindi.");
                tablolariGuncelle();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(pencere, "Hata: " + e.getMessage());
            }
        }
    }

    private JPanel hastaTablosuOlustur() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] kolonlar = {"ID", "TCKNO", "Ad", "Soyad", "Doğum Tarihi", "İletişim"};
        hastaModel = new DefaultTableModel(kolonlar, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        hastaTablosu = new JTable(hastaModel);
        UIStyle.styleTable(hastaTablosu);
        panel.add(new JScrollPane(hastaTablosu), BorderLayout.CENTER);
        return panel;
    }

    private JPanel doktorTablosuOlustur() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] kolonlar = {"ID", "TCKNO", "Ad Soyad", "Uzmanlık"};
        doktorModel = new DefaultTableModel(kolonlar, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        doktorTablosu = new JTable(doktorModel);
        UIStyle.styleTable(doktorTablosu);
        panel.add(new JScrollPane(doktorTablosu), BorderLayout.CENTER);
        return panel;
    }

    private JPanel ilacTablosuOlustur() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] kolonlar = {"ID", "İlaç Adı", "Stok", "Fiyat"};
        ilacModel = new DefaultTableModel(kolonlar, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ilacTablosu = new JTable(ilacModel);
        UIStyle.styleTable(ilacTablosu);
        panel.add(new JScrollPane(ilacTablosu), BorderLayout.CENTER);
        return panel;
    }
}


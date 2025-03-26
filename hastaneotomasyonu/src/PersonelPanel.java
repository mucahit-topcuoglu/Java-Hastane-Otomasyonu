import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Personel;

public class PersonelPanel {
    private final JFrame pencere;
    private final Personel personel;
    private JTable hastaTablosu, randevuTablosu, faturaTablosu;
    private DefaultTableModel hastaModel, randevuModel;
    private SigortaFaturalandirma sigortaFaturalandirma;

    public PersonelPanel(Personel personel) {
        this.personel = personel;
        this.pencere = new JFrame("Personel Paneli - " + personel.getAdSoyad());
        UIStyle.styleFrame(pencere);
        pencere.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pencere.setSize(900, 600);
        pencere.setLayout(new BorderLayout(10, 10));

        JTabbedPane sekmeler = new JTabbedPane();
        UIStyle.styleTabbedPane(sekmeler);

        JPanel hastalarPaneli = new JPanel(new BorderLayout(10, 10));
        UIStyle.stylePanel(hastalarPaneli);
        
        String[] hastaSutunlar = {"ID", "TCKNO", "Ad", "Soyad", "Doğum Tarihi", "İletişim"};
        hastaModel = new DefaultTableModel(hastaSutunlar, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        hastaTablosu = new JTable(hastaModel);
        UIStyle.styleTable(hastaTablosu);
        hastalarPaneli.add(new JScrollPane(hastaTablosu), BorderLayout.CENTER);

        JPanel hastaButonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        UIStyle.stylePanel(hastaButonPanel);
        
        JButton hastaEkleButonu = UIStyle.createStyledButton("Hasta Ekle");
        JButton hastaDuzenleButonu = UIStyle.createStyledButton("Hasta Düzenle");
        
        hastaButonPanel.add(hastaEkleButonu);
        hastaButonPanel.add(hastaDuzenleButonu);
        hastalarPaneli.add(hastaButonPanel, BorderLayout.SOUTH);
        sekmeler.addTab("Hastalar", hastalarPaneli);

        JPanel randevularPaneli = new JPanel(new BorderLayout(10, 10));
        UIStyle.stylePanel(randevularPaneli);
        
        String[] randevuSutunlar = {"ID", "Hasta", "Doktor", "Tarih", "Saat", "Durum"};
        randevuModel = new DefaultTableModel(randevuSutunlar, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        randevuTablosu = new JTable(randevuModel);
        UIStyle.styleTable(randevuTablosu);
        randevularPaneli.add(new JScrollPane(randevuTablosu), BorderLayout.CENTER);

        JPanel randevuButonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        UIStyle.stylePanel(randevuButonPanel);
        
        JButton randevuEkleButonu = UIStyle.createStyledButton("Randevu Ekle");
        JButton randevuDuzenleButonu = UIStyle.createStyledButton("Randevu Düzenle");
        JButton randevuSilButonu = UIStyle.createStyledButton("Randevu Sil");
        
        randevuButonPanel.add(randevuEkleButonu);
        randevuButonPanel.add(randevuDuzenleButonu);
        randevuButonPanel.add(randevuSilButonu);
        randevularPaneli.add(randevuButonPanel, BorderLayout.SOUTH);
        sekmeler.addTab("Randevular", randevularPaneli);

        sigortaFaturalandirma = new SigortaFaturalandirma();
        JPanel faturaPanel = sigortaFaturalandirma.getPanel();
        sekmeler.add("Sigorta ve Faturalandırma", faturaPanel);

        faturaTablosu = sigortaFaturalandirma.getFaturaTablosu();

        JButton geriButonu = UIStyle.createStyledButton("Ana Sayfaya Dön");
        geriButonu.addActionListener(e -> {
            new GirisEkrani();
            pencere.dispose();
        });

        pencere.add(sekmeler, BorderLayout.CENTER);
        pencere.add(geriButonu, BorderLayout.SOUTH);
        pencere.setLocationRelativeTo(null);
        pencere.setVisible(true);

        hastaEkleButonu.addActionListener(e -> hastaEkle());
        hastaDuzenleButonu.addActionListener(e -> hastaDuzenle());
        randevuEkleButonu.addActionListener(e -> randevuEkle());
        randevuDuzenleButonu.addActionListener(e -> randevuDuzenle());
        randevuSilButonu.addActionListener(e -> randevuSil());

        hastalariYukle();
        randevulariYukle();
    }

    private void faturalariYukle() {
        sigortaFaturalandirma.faturalariYukle();
    }

    

    private void hastalariYukle() {
        try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
            String sorgu = "SELECT * FROM hastalar ORDER BY ad, soyad";
            ResultSet sonuc = baglanti.createStatement().executeQuery(sorgu);

            hastaModel.setRowCount(0);
            while (sonuc.next()) {
                hastaModel.addRow(new Object[]{
                    sonuc.getInt("id"),
                    sonuc.getString("tckno"),
                    sonuc.getString("ad"),
                    sonuc.getString("soyad"),
                    sonuc.getDate("dogum_tarihi"),
                    sonuc.getString("iletisim_bilgileri")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(pencere, 
                "Hastalar yüklenirken hata: " + e.getMessage(),
                "Hata",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void randevulariYukle() {
        try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
            String sorgu = """
                SELECT r.*, h.ad as hasta_adi, h.soyad as hasta_soyadi,
                d.ad_soyad as doktor_adi
                FROM randevular r
                JOIN hastalar h ON r.hastaId = h.id
                JOIN doktorlar d ON r.doktorId = d.id
                ORDER BY r.tarih, r.saat
                """;
            ResultSet sonuc = baglanti.createStatement().executeQuery(sorgu);

            randevuModel.setRowCount(0);
            while (sonuc.next()) {
                randevuModel.addRow(new Object[]{
                    sonuc.getInt("id"),
                    sonuc.getString("hasta_adi") + " " + sonuc.getString("hasta_soyadi"),
                    sonuc.getString("doktor_adi"),
                    sonuc.getDate("tarih"),
                    sonuc.getTime("saat"),
                    sonuc.getString("durum")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(pencere, 
                "Randevular yüklenirken hata: " + e.getMessage(),
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
        dialog.add(new JLabel("İletişim:"));
        dialog.add(iletisimAlani);
        dialog.add(new JLabel("Şifre:"));
        dialog.add(sifreAlani);

        JButton kaydetButon = UIStyle.createStyledButton("Kaydet");
        kaydetButon.addActionListener(e -> {
            try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
                String sorgu = """
                    INSERT INTO hastalar (tckno, ad, soyad, dogum_tarihi, 
                    iletisim_bilgileri, sifre)
                    VALUES (?, ?, ?, ?, ?, ?)
                    """;
                PreparedStatement stmt = baglanti.prepareStatement(sorgu);
                stmt.setString(1, tcknoAlani.getText().trim());
                stmt.setString(2, adAlani.getText().trim());
                stmt.setString(3, soyadAlani.getText().trim());
                stmt.setString(4, dogumTarihiAlani.getText().trim());
                stmt.setString(5, iletisimAlani.getText().trim());
                stmt.setString(6, new String(sifreAlani.getPassword()));
                
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(dialog, "Hasta başarıyla eklendi.");
                dialog.dispose();
                hastalariYukle();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Hasta eklenirken hata: " + ex.getMessage(),
                    "Hata",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton iptalButon = UIStyle.createStyledButton("İptal");
        iptalButon.addActionListener(e -> dialog.dispose());

        JPanel butonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        butonPanel.add(iptalButon);
        butonPanel.add(kaydetButon);
        dialog.add(butonPanel);

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
        String mevcutTckno = hastaTablosu.getValueAt(secilenSatir, 1).toString();
        String mevcutAd = hastaTablosu.getValueAt(secilenSatir, 2).toString();
        String mevcutSoyad = hastaTablosu.getValueAt(secilenSatir, 3).toString();
        Date mevcutDogumTarihi = (Date) hastaTablosu.getValueAt(secilenSatir, 4);
        String mevcutIletisim = hastaTablosu.getValueAt(secilenSatir, 5).toString();

        JDialog dialog = new JDialog(pencere, "Hasta Düzenle", true);
        dialog.setLayout(new GridLayout(6, 2, 10, 10));
        dialog.setSize(400, 300);

        JTextField tcknoAlani = new JTextField(mevcutTckno);
        JTextField adAlani = new JTextField(mevcutAd);
        JTextField soyadAlani = new JTextField(mevcutSoyad);
        JTextField dogumTarihiAlani = new JTextField(mevcutDogumTarihi.toString());
        JTextField iletisimAlani = new JTextField(mevcutIletisim);

        dialog.add(new JLabel("TCKNO:"));
        dialog.add(tcknoAlani);
        dialog.add(new JLabel("Ad:"));
        dialog.add(adAlani);
        dialog.add(new JLabel("Soyad:"));
        dialog.add(soyadAlani);
        dialog.add(new JLabel("Doğum Tarihi (YYYY-MM-DD):"));
        dialog.add(dogumTarihiAlani);
        dialog.add(new JLabel("İletişim:"));
        dialog.add(iletisimAlani);

        JButton kaydetButon = UIStyle.createStyledButton("Güncelle");
        kaydetButon.addActionListener(e -> {
            try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
                String sorgu = """
                    UPDATE hastalar 
                    SET tckno = ?, ad = ?, soyad = ?, 
                    dogum_tarihi = ?, iletisim_bilgileri = ?
                    WHERE id = ?
                    """;
                PreparedStatement stmt = baglanti.prepareStatement(sorgu);
                stmt.setString(1, tcknoAlani.getText().trim());
                stmt.setString(2, adAlani.getText().trim());
                stmt.setString(3, soyadAlani.getText().trim());
                stmt.setString(4, dogumTarihiAlani.getText().trim());
                stmt.setString(5, iletisimAlani.getText().trim());
                stmt.setInt(6, hastaId);
                
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(dialog, "Hasta bilgileri güncellendi.");
                dialog.dispose();
                hastalariYukle();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Hasta güncellenirken hata: " + ex.getMessage(),
                    "Hata",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton iptalButon = UIStyle.createStyledButton("İptal");
        iptalButon.addActionListener(e -> dialog.dispose());

        JPanel butonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        butonPanel.add(iptalButon);
        butonPanel.add(kaydetButon);
        dialog.add(butonPanel);

        dialog.setLocationRelativeTo(pencere);
        dialog.setVisible(true);
    }

    private void randevuEkle() {
        JDialog dialog = new JDialog(pencere, "Yeni Randevu", true);
        dialog.setLayout(new GridLayout(6, 2, 10, 10));
        dialog.setSize(400, 300);

        JComboBox<String> hastaCombo = new JComboBox<>();
        JComboBox<String> doktorCombo = new JComboBox<>();
        JTextField tarihAlani = new JTextField();
        JTextField saatAlani = new JTextField();

        try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
            String sorgu = "SELECT id, ad, soyad FROM hastalar ORDER BY ad, soyad";
            ResultSet sonuc = baglanti.createStatement().executeQuery(sorgu);
            while (sonuc.next()) {
                hastaCombo.addItem(sonuc.getInt("id") + " - " + 
                    sonuc.getString("ad") + " " + sonuc.getString("soyad"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(dialog, "Hastalar yüklenirken hata: " + e.getMessage());
        }

        try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
            String sorgu = "SELECT id, ad_soyad FROM doktorlar ORDER BY ad_soyad";
            ResultSet sonuc = baglanti.createStatement().executeQuery(sorgu);
            while (sonuc.next()) {
                doktorCombo.addItem(sonuc.getInt("id") + " - " + sonuc.getString("ad_soyad"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(dialog, "Doktorlar yüklenirken hata: " + e.getMessage());
        }

        dialog.add(new JLabel("Hasta:"));
        dialog.add(hastaCombo);
        dialog.add(new JLabel("Doktor:"));
        dialog.add(doktorCombo);
        dialog.add(new JLabel("Tarih (YYYY-MM-DD):"));
        dialog.add(tarihAlani);
        dialog.add(new JLabel("Saat (HH:MM):"));
        dialog.add(saatAlani);

        JButton kaydetButon = UIStyle.createStyledButton("Kaydet");
        kaydetButon.addActionListener(e -> {
            try {
                String secilenHasta = (String) hastaCombo.getSelectedItem();
                String secilenDoktor = (String) doktorCombo.getSelectedItem();
                
                int hastaId = Integer.parseInt(secilenHasta.split(" - ")[0]);
                int doktorId = Integer.parseInt(secilenDoktor.split(" - ")[0]);

                String tarih = tarihAlani.getText().trim();
                String saat = saatAlani.getText().trim();

                if (tarih.isEmpty() || saat.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Tarih ve saat alanları boş bırakılamaz.");
                    return;
                }

                try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
                    String sorgu = """
                        INSERT INTO randevular (hastaId, doktorId, tarih, saat, durum)
                        VALUES (?, ?, ?, ?, 'Bekliyor')
                        """;
                    PreparedStatement stmt = baglanti.prepareStatement(sorgu);
                    stmt.setInt(1, hastaId);
                    stmt.setInt(2, doktorId);
                    stmt.setString(3, tarih);
                    stmt.setString(4, saat);
                    
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(dialog, "Randevu başarıyla oluşturuldu.");
                    dialog.dispose();
                    randevulariYukle();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Randevu oluşturulurken hata: " + ex.getMessage(),
                    "Hata",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton iptalButon = UIStyle.createStyledButton("İptal");
        iptalButon.addActionListener(e -> dialog.dispose());

        JPanel butonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        butonPanel.add(iptalButon);
        butonPanel.add(kaydetButon);
        dialog.add(butonPanel);

        dialog.setLocationRelativeTo(pencere);
        dialog.setVisible(true);
    }

    private void randevuDuzenle() {
        int secilenSatir = randevuTablosu.getSelectedRow();
        if (secilenSatir == -1) {
            JOptionPane.showMessageDialog(pencere, "Lütfen düzenlenecek randevuyu seçin.");
            return;
        }

        int randevuId = (int) randevuTablosu.getValueAt(secilenSatir, 0);
        Date tarih = (Date) randevuTablosu.getValueAt(secilenSatir, 3);
        Time saat = (Time) randevuTablosu.getValueAt(secilenSatir, 4);

        JDialog dialog = new JDialog(pencere, "Randevu Düzenle", true);
        dialog.setLayout(new GridLayout(3, 2, 10, 10));
        dialog.setSize(400, 200);

        JTextField tarihAlani = new JTextField(tarih.toString());
        JTextField saatAlani = new JTextField(saat.toString());

        dialog.add(new JLabel("Tarih (YYYY-MM-DD):"));
        dialog.add(tarihAlani);
        dialog.add(new JLabel("Saat (HH:MM):"));
        dialog.add(saatAlani);

        JButton kaydetButon = UIStyle.createStyledButton("Güncelle");
        kaydetButon.addActionListener(e -> {
            try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
                String sorgu = "UPDATE randevular SET tarih = ?, saat = ? WHERE id = ?";
                PreparedStatement stmt = baglanti.prepareStatement(sorgu);
                stmt.setString(1, tarihAlani.getText().trim());
                stmt.setString(2, saatAlani.getText().trim());
                stmt.setInt(3, randevuId);
                
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(dialog, "Randevu güncellendi.");
                dialog.dispose();
                randevulariYukle();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Randevu güncellenirken hata: " + ex.getMessage(),
                    "Hata",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton iptalButon = UIStyle.createStyledButton("İptal");
        iptalButon.addActionListener(e -> dialog.dispose());

        JPanel butonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        butonPanel.add(iptalButon);
        butonPanel.add(kaydetButon);
        dialog.add(butonPanel);

        dialog.setLocationRelativeTo(pencere);
        dialog.setVisible(true);
    }

    private void randevuSil() {
        int secilenSatir = randevuTablosu.getSelectedRow();
        if (secilenSatir == -1) {
            JOptionPane.showMessageDialog(pencere, "Lütfen silinecek randevuyu seçin.");
            return;
        }

        int randevuId = (int) randevuTablosu.getValueAt(secilenSatir, 0);
        String hastaAdi = randevuTablosu.getValueAt(secilenSatir, 1).toString();
        Date tarih = (Date) randevuTablosu.getValueAt(secilenSatir, 3);

        int secim = JOptionPane.showConfirmDialog(
            pencere,
            hastaAdi + " hastasının " + tarih + " tarihli randevusunu silmek istediğinizden emin misiniz?",
            "Randevu Sil",
            JOptionPane.YES_NO_OPTION
        );

        if (secim == JOptionPane.YES_OPTION) {
            try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
                String sorgu = "DELETE FROM randevular WHERE id = ?";
                PreparedStatement stmt = baglanti.prepareStatement(sorgu);
                stmt.setInt(1, randevuId);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(pencere, "Randevu silindi.");
                randevulariYukle();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(pencere, 
                    "Randevu silinirken hata: " + e.getMessage(),
                    "Hata",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

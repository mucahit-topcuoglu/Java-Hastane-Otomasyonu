import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import models.Doktor;

import models.Ilac;

import java.util.ArrayList;
import java.util.List;

public class DoktorPanel {
    private final JFrame pencere;
    private final Doktor doktor;
    private JTable hastaTablosu, randevuTablosu, receteTablosu, labSonucTablosu;
    private JButton receteEkleButonu, randevuDuzenleButonu, randevuSilButonu, receteleriGorButonu, ilaclariGorButonu;

    public DoktorPanel(Doktor doktor) {
        this.doktor = doktor;
        this.pencere = new JFrame("Dr. " + doktor.getAdSoyad() + " - Doktor Paneli");
        UIStyle.styleFrame(pencere);
        pencere.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pencere.setSize(900, 600);
        pencere.setLayout(new BorderLayout(10, 10));

        JTabbedPane sekmeler = new JTabbedPane();
        UIStyle.styleTabbedPane(sekmeler);

        JPanel hastalarPaneli = new JPanel(new BorderLayout(10, 10));
        UIStyle.stylePanel(hastalarPaneli);
        hastaTablosu = new JTable();
        UIStyle.styleTable(hastaTablosu);
        hastalarPaneli.add(new JScrollPane(hastaTablosu), BorderLayout.CENTER);
        sekmeler.addTab("Hastalar", hastalarPaneli);

        JPanel randevularPaneli = new JPanel(new BorderLayout(10, 10));
        UIStyle.stylePanel(randevularPaneli);
        randevuTablosu = new JTable();
        UIStyle.styleTable(randevuTablosu);
        randevularPaneli.add(new JScrollPane(randevuTablosu), BorderLayout.CENTER);

        JPanel randevuButonPaneli = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        UIStyle.stylePanel(randevuButonPaneli);
        
        randevuDuzenleButonu = UIStyle.createStyledButton("Randevuyu Düzenle");
        randevuSilButonu = UIStyle.createStyledButton("Randevuyu Sil");
        receteEkleButonu = UIStyle.createStyledButton("Reçete Yaz");
        
        randevuButonPaneli.add(randevuDuzenleButonu);
        randevuButonPaneli.add(randevuSilButonu);
        randevuButonPaneli.add(receteEkleButonu);
        randevularPaneli.add(randevuButonPaneli, BorderLayout.SOUTH);
        sekmeler.addTab("Randevular", randevularPaneli);

        JPanel recetelerPaneli = new JPanel(new BorderLayout(10, 10));
        UIStyle.stylePanel(recetelerPaneli);
        receteTablosu = new JTable();
        UIStyle.styleTable(receteTablosu);
        recetelerPaneli.add(new JScrollPane(receteTablosu), BorderLayout.CENTER);

        JPanel receteButonPaneli = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        UIStyle.stylePanel(receteButonPaneli);
        
        receteleriGorButonu = UIStyle.createStyledButton("Reçeteleri Gör");
        ilaclariGorButonu = UIStyle.createStyledButton("İlaçları Gör");
        
        receteButonPaneli.add(receteleriGorButonu);
        receteButonPaneli.add(ilaclariGorButonu);
        recetelerPaneli.add(receteButonPaneli, BorderLayout.SOUTH);
        sekmeler.addTab("Reçeteler", recetelerPaneli);

        JPanel labSonuclarPaneli = new JPanel(new BorderLayout(10, 10));
        UIStyle.stylePanel(labSonuclarPaneli);
        labSonucTablosu = new JTable();
        UIStyle.styleTable(labSonucTablosu);
        labSonuclarPaneli.add(new JScrollPane(labSonucTablosu), BorderLayout.CENTER);

        JPanel labButonPaneli = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        UIStyle.stylePanel(labButonPaneli);
        
        JButton labSonucEkleButonu = UIStyle.createStyledButton("Lab Sonucu Ekle");
        JButton labSonucDuzenleButonu = UIStyle.createStyledButton("Lab Sonucu Düzenle");
        JButton labReceteYazButonu = UIStyle.createStyledButton("Lab Sonucuna Göre Reçete Yaz");
        
        labButonPaneli.add(labSonucEkleButonu);
        labButonPaneli.add(labSonucDuzenleButonu);
        labButonPaneli.add(labReceteYazButonu);
        labSonuclarPaneli.add(labButonPaneli, BorderLayout.SOUTH);
        sekmeler.addTab("Lab Sonuçları", labSonuclarPaneli);

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

        receteEkleButonu.addActionListener(e -> receteYaz());
        randevuDuzenleButonu.addActionListener(e -> randevuDuzenle());
        randevuSilButonu.addActionListener(e -> randevuSil());
        receteleriGorButonu.addActionListener(e -> receteleriGor());
        ilaclariGorButonu.addActionListener(e -> ilaclariGor());
        labSonucEkleButonu.addActionListener(e -> labSonucuEkle());
        labSonucDuzenleButonu.addActionListener(e -> labSonucuDuzenle());
        labReceteYazButonu.addActionListener(e -> labSonucunaGoreReceteYaz());

        loadData();

        pencere.setLocationRelativeTo(null);
        pencere.setVisible(true);
    }

    private void loadData() {
        hastalariYukle();
        randevulariYukle();
        labSonuclariYukle();
    }

    private void hastalariYukle() {
        try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
            String sorgu = """
                SELECT DISTINCT h.* 
                FROM hastalar h 
                JOIN randevular r ON h.id = r.hastaId 
                WHERE r.doktorId = ? 
                ORDER BY h.ad, h.soyad
                """;
            
            PreparedStatement stmt = baglanti.prepareStatement(sorgu);
            stmt.setInt(1, doktor.getId());
            ResultSet sonuc = stmt.executeQuery();

            String[] sutunlar = {"ID", "TCKNO", "Ad", "Soyad", "Doğum Tarihi", "İletişim"};
            DefaultTableModel model = new DefaultTableModel(sutunlar, 0);

            while (sonuc.next()) {
                model.addRow(new Object[]{
                    sonuc.getInt("id"),
                    sonuc.getString("tckno"),
                    sonuc.getString("ad"),
                    sonuc.getString("soyad"),
                    sonuc.getDate("dogum_tarihi"),
                    sonuc.getString("iletisim_bilgileri")
                });
            }
            hastaTablosu.setModel(model);
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
                SELECT r.*, h.tckno, h.ad, h.soyad 
                FROM randevular r 
                JOIN hastalar h ON r.hastaId = h.id 
                WHERE r.doktorId = ? 
                ORDER BY r.tarih, r.saat
                """;
            
            PreparedStatement stmt = baglanti.prepareStatement(sorgu);
            stmt.setInt(1, doktor.getId());
            ResultSet sonuc = stmt.executeQuery();

            String[] sutunlar = {"ID", "Hasta TCKNO", "Hasta Adı", "Hasta Soyadı", "Tarih", "Saat", "Durum"};
            DefaultTableModel model = new DefaultTableModel(sutunlar, 0);

            while (sonuc.next()) {
                model.addRow(new Object[]{
                    sonuc.getInt("id"),
                    sonuc.getString("tckno"),
                    sonuc.getString("ad"),
                    sonuc.getString("soyad"),
                    sonuc.getDate("tarih"),
                    sonuc.getTime("saat"),
                    sonuc.getString("durum")
                });
            }
            randevuTablosu.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(pencere, 
                "Randevular yüklenirken hata: " + e.getMessage(),
                "Hata", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void labSonuclariYukle() {
        try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
            String sorgu = """
                SELECT l.*, h.tckno, h.ad, h.soyad 
                FROM lab_sonuclari l 
                JOIN hastalar h ON l.hasta_id = h.id 
                WHERE l.doktor_id = ? 
                ORDER BY l.tarih DESC
                """;
            
            PreparedStatement stmt = baglanti.prepareStatement(sorgu);
            stmt.setInt(1, doktor.getId());
            ResultSet sonuc = stmt.executeQuery();

            String[] sutunlar = {"ID", "Hasta TCKNO", "Hasta Adı", "Hasta Soyadı", "Test Türü", "Sonuç", "Tarih"};
            DefaultTableModel model = new DefaultTableModel(sutunlar, 0);

            while (sonuc.next()) {
                model.addRow(new Object[]{
                    sonuc.getInt("id"),
                    sonuc.getString("tckno"),
                    sonuc.getString("ad"),
                    sonuc.getString("soyad"),
                    sonuc.getString("test_turu"),
                    sonuc.getString("sonuc"),
                    sonuc.getDate("tarih")
                });
            }
            labSonucTablosu.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(pencere, 
                "Lab sonuçları yüklenirken hata: " + e.getMessage(),
                "Hata", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void receteYaz() {
        int secilenSatir = randevuTablosu.getSelectedRow();
        if (secilenSatir == -1) {
            JOptionPane.showMessageDialog(pencere, "Lütfen bir randevu seçin.");
            return;
        }

        try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
            String hastaSorgu = "SELECT hastaId FROM randevular WHERE id = ?";
            PreparedStatement hastaStmt = baglanti.prepareStatement(hastaSorgu);
            hastaStmt.setInt(1, (int)randevuTablosu.getValueAt(secilenSatir, 0));
            ResultSet hastaSonuc = hastaStmt.executeQuery();
            
            if (!hastaSonuc.next()) {
                JOptionPane.showMessageDialog(pencere, "Randevu bilgisi bulunamadı!");
                return;
            }
            int hastaId = hastaSonuc.getInt("hastaId");

            JDialog dialog = new JDialog(pencere, "Reçete Yaz", true);
            dialog.setLayout(new GridLayout(4, 2, 10, 10));
            dialog.setSize(400, 300);

            JComboBox<Ilac> ilacSecimKutusu = new JComboBox<>();
            JTextField adetAlani = new JTextField();
            JTextArea notlarAlani = new JTextArea();

            String ilacSorgu = "SELECT * FROM ilaclar WHERE stok > 0";
            ResultSet ilacSonuc = baglanti.createStatement().executeQuery(ilacSorgu);
            while (ilacSonuc.next()) {
                ilacSecimKutusu.addItem(new Ilac(
                    ilacSonuc.getInt("id"),
                    ilacSonuc.getString("ilac_adi"),
                    ilacSonuc.getInt("stok"),
                    ilacSonuc.getDouble("fiyat")
                ));
            }

            dialog.add(new JLabel("İlaç:"));
            dialog.add(ilacSecimKutusu);
            dialog.add(new JLabel("Adet:"));
            dialog.add(adetAlani);
            dialog.add(new JLabel("Notlar:"));
            dialog.add(notlarAlani);

            JButton kaydetButon = UIStyle.createStyledButton("Kaydet");
            kaydetButon.addActionListener(e -> {
                try {
                    Ilac secilenIlac = (Ilac) ilacSecimKutusu.getSelectedItem();
                    int adet = Integer.parseInt(adetAlani.getText());

                    if (adet <= 0) {
                        JOptionPane.showMessageDialog(dialog, "Adet 0'dan büyük olmalıdır.");
                        return;
                    }

                    if (adet > secilenIlac.getStok()) {
                        JOptionPane.showMessageDialog(dialog, "Stokta yeterli ilaç yok!");
                        return;
                    }

                    String receteSorgu = """
                        INSERT INTO receteler (hastaId, doktorId, ilac_id, adet, notlar) 
                        VALUES (?, ?, ?, ?, ?)
                        """;
                    
                    PreparedStatement stmt = baglanti.prepareStatement(receteSorgu);
                    stmt.setInt(1, hastaId);
                    stmt.setInt(2, doktor.getId());
                    stmt.setInt(3, secilenIlac.getId());
                    stmt.setInt(4, adet);
                    stmt.setString(5, notlarAlani.getText());
                    
                    stmt.executeUpdate();

                    baglanti.createStatement().executeUpdate(
                        "UPDATE ilaclar SET stok = stok - " + adet + 
                        " WHERE id = " + secilenIlac.getId());

                    JOptionPane.showMessageDialog(dialog, "Reçete başarıyla yazıldı.");
                    dialog.dispose();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Reçete yazılırken hata: " + ex.getMessage(),
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
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(pencere, 
                "Hata: " + ex.getMessage(),
                "Hata",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void randevuDuzenle() {
        int secilenSatir = randevuTablosu.getSelectedRow();
        if (secilenSatir == -1) {
            JOptionPane.showMessageDialog(null, "Düzenlemek için bir randevu seçin.");
            return;
        }

        int randevuId = Integer.parseInt(randevuTablosu.getValueAt(secilenSatir, 0).toString());
        
        JDialog dialog = new JDialog();
        dialog.setTitle("Randevu Düzenle");
        UIStyle.styleDialog(dialog);
        dialog.setModal(true);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        UIStyle.stylePanel(formPanel);

        JTextField tarihAlani = new JTextField();
        UIStyle.styleTextField(tarihAlani);
        JTextField saatAlani = new JTextField();
        UIStyle.styleTextField(saatAlani);

        JLabel tarihLabel = new JLabel("Tarih (YYYY-MM-DD):");
        UIStyle.styleLabel(tarihLabel);
        JLabel saatLabel = new JLabel("Saat (HH:MM):");
        UIStyle.styleLabel(saatLabel);

        formPanel.add(tarihLabel);
        formPanel.add(tarihAlani);
        formPanel.add(saatLabel);
        formPanel.add(saatAlani);

        JPanel butonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        UIStyle.stylePanel(butonPanel);
        JButton iptalButon = UIStyle.createStyledButton("İptal");
        JButton kaydetButon = UIStyle.createStyledButton("Kaydet");

        iptalButon.addActionListener(e -> dialog.dispose());
        kaydetButon.addActionListener(e -> {
            try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
                String tarih = tarihAlani.getText();
                String saat = saatAlani.getText();

                String sorgu = "UPDATE randevular SET tarih = ?, saat = ? WHERE id = ?";
                PreparedStatement stmt = baglanti.prepareStatement(sorgu);
                stmt.setString(1, tarih);
                stmt.setString(2, saat);
                stmt.setInt(3, randevuId);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(null, "Randevu güncellendi.");
                randevulariYukle();
                dialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Hata: " + ex.getMessage());
            }
        });

        butonPanel.add(iptalButon);
        butonPanel.add(kaydetButon);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(butonPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void randevuSil() {
        int secilenSatir = randevuTablosu.getSelectedRow();
        if (secilenSatir == -1) {
            JOptionPane.showMessageDialog(null, "Silmek için bir randevu seçin.");
            return;
        }

        int randevuId = Integer.parseInt(randevuTablosu.getValueAt(secilenSatir, 0).toString());
        String hastaAdi = randevuTablosu.getValueAt(secilenSatir, 1).toString();
        String tarih = randevuTablosu.getValueAt(secilenSatir, 2).toString();

        JDialog onayDialog = new JDialog();
        UIStyle.styleDialog(onayDialog);
        onayDialog.setTitle("Randevu Sil");
        onayDialog.setModal(true);
        onayDialog.setLayout(new BorderLayout(10, 10));

        JPanel mesajPanel = new JPanel();
        UIStyle.stylePanel(mesajPanel);
        JLabel mesajLabel = new JLabel(String.format(
            "%s hastasının%s tarihli randevusunu silmek istediğinizden emin misiniz?",
            hastaAdi, tarih
        ));
        UIStyle.styleLabel(mesajLabel);
        mesajPanel.add(mesajLabel);

        JPanel butonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        UIStyle.stylePanel(butonPanel);
        JButton iptalButon = UIStyle.createStyledButton("İptal");
        JButton silButon = UIStyle.createStyledButton("Sil");

        iptalButon.addActionListener(e -> onayDialog.dispose());
        silButon.addActionListener(e -> {
            try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
                baglanti.createStatement().executeUpdate("DELETE FROM randevular WHERE id = " + randevuId);
                JOptionPane.showMessageDialog(null, "Randevu silindi.");
                randevulariYukle();
                onayDialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Hata: " + ex.getMessage());
            }
        });

        butonPanel.add(iptalButon);
        butonPanel.add(silButon);

        onayDialog.add(mesajPanel, BorderLayout.CENTER);
        onayDialog.add(butonPanel, BorderLayout.SOUTH);

        onayDialog.pack();
        onayDialog.setLocationRelativeTo(null);
        onayDialog.setVisible(true);
    }

    private void receteleriGor() {
        try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
            String sorgu = """
                SELECT r.*, h.tckno, h.ad, h.soyad, i.ilac_adi, i.fiyat 
                FROM receteler r 
                JOIN hastalar h ON r.hastaId = h.id 
                JOIN ilaclar i ON r.ilac_id = i.id 
                WHERE r.doktorId = ? 
                ORDER BY r.id DESC
                """;
            
            PreparedStatement stmt = baglanti.prepareStatement(sorgu);
            stmt.setInt(1, doktor.getId());
            ResultSet sonuc = stmt.executeQuery();

            String[] sutunlar = {"ID", "Hasta TCKNO", "Hasta Adı", "Hasta Soyadı", "İlaç Adı", "Adet", "Fiyat", "Notlar"};
            DefaultTableModel model = new DefaultTableModel(sutunlar, 0);

            while (sonuc.next()) {
                model.addRow(new Object[]{
                    sonuc.getInt("id"),
                    sonuc.getString("tckno"),
                    sonuc.getString("ad"),
                    sonuc.getString("soyad"),
                    sonuc.getString("ilac_adi"),
                    sonuc.getInt("adet"),
                    sonuc.getDouble("fiyat"),
                    sonuc.getString("notlar")
                });
            }
            receteTablosu.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(pencere, 
                "Reçeteler yüklenirken hata: " + e.getMessage(),
                "Hata", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ilaclariGor() {
        try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
            ResultSet sonuc = baglanti.createStatement().executeQuery(
                "SELECT id, ilac_adi, stok, fiyat FROM ilaclar");

            List<Object[]> ilacListesi = new ArrayList<>();
            while (sonuc.next()) {
                ilacListesi.add(new Object[]{
                    sonuc.getString("ilac_adi"),
                    sonuc.getInt("stok"),
                    sonuc.getDouble("fiyat")
                });
            }

            if (ilacListesi.isEmpty()) {
                JOptionPane.showMessageDialog(null, "İlaç listesi boş.");
                return;
            }

            String[] sutunlar = {"İlaç Adı", "Stok", "Fiyat (TL)"};
            Object[][] veriler = ilacListesi.toArray(new Object[0][]);
            receteTablosu.setModel(new javax.swing.table.DefaultTableModel(veriler, sutunlar));

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "İlaçlar yüklenirken hata: " + e.getMessage());
        }
    }

    private void labSonucuEkle() {
        int secilenHastaSatir = hastaTablosu.getSelectedRow();
        if (secilenHastaSatir == -1) {
            JOptionPane.showMessageDialog(pencere, "Lütfen bir hasta seçin.");
            return;
        }

        int hastaId = (int) hastaTablosu.getValueAt(secilenHastaSatir, 0);
        
        JDialog dialog = new JDialog(pencere, "Yeni Lab Sonucu", true);
        dialog.setLayout(new GridLayout(4, 2, 10, 10));
        dialog.setSize(400, 200);

        JTextField testTuruAlani = new JTextField();
        JTextField sonucAlani = new JTextField();
        JTextField tarihAlani = new JTextField();

        dialog.add(new JLabel("Test Türü:"));
        dialog.add(testTuruAlani);
        dialog.add(new JLabel("Sonuç:"));
        dialog.add(sonucAlani);
        dialog.add(new JLabel("Tarih (YYYY-MM-DD):"));
        dialog.add(tarihAlani);

        JButton kaydetButon = new JButton("Kaydet");
        JButton iptalButon = new JButton("İptal");

        kaydetButon.addActionListener(e -> {
            try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
                String sorgu = """
                    INSERT INTO lab_sonuclari 
                    (hasta_id, doktor_id, test_turu, sonuc, tarih) 
                    VALUES (?, ?, ?, ?, ?)
                    """;
                    
                PreparedStatement stmt = baglanti.prepareStatement(sorgu);
                stmt.setInt(1, hastaId);
                stmt.setInt(2, doktor.getId());
                stmt.setString(3, testTuruAlani.getText().trim());
                stmt.setString(4, sonucAlani.getText().trim());
                stmt.setDate(5, java.sql.Date.valueOf(tarihAlani.getText().trim()));
                
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(dialog, "Lab sonucu başarıyla eklendi.");
                labSonuclariYukle();
                dialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Lab sonucu eklenirken hata: " + ex.getMessage(),
                    "Hata",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        iptalButon.addActionListener(e -> dialog.dispose());

        JPanel butonPanel = new JPanel();
        butonPanel.add(iptalButon);
        butonPanel.add(kaydetButon);
        dialog.add(butonPanel);

        dialog.setLocationRelativeTo(pencere);
        dialog.setVisible(true);
    }

    private void labSonucuDuzenle() {
        int secilenSatir = labSonucTablosu.getSelectedRow();
        if (secilenSatir == -1) {
            JOptionPane.showMessageDialog(pencere, "Lütfen düzenlenecek lab sonucunu seçin.");
            return;
        }

        int labSonucId = (int) labSonucTablosu.getValueAt(secilenSatir, 0);
        String testTuru = (String) labSonucTablosu.getValueAt(secilenSatir, 4);
        String sonuc = (String) labSonucTablosu.getValueAt(secilenSatir, 5);
        String tarih = labSonucTablosu.getValueAt(secilenSatir, 6).toString();

        JDialog dialog = new JDialog(pencere, "Lab Sonucu Düzenle", true);
        dialog.setLayout(new GridLayout(4, 2, 10, 10));

        JTextField testTuruAlani = new JTextField(testTuru);
        JTextField sonucAlani = new JTextField(sonuc);
        JTextField tarihAlani = new JTextField(tarih);

        dialog.add(new JLabel("Test Türü:"));
        dialog.add(testTuruAlani);
        dialog.add(new JLabel("Sonuç:"));
        dialog.add(sonucAlani);
        dialog.add(new JLabel("Tarih (YYYY-MM-DD):"));
        dialog.add(tarihAlani);

        JButton kaydetButon = UIStyle.createStyledButton("Kaydet");
        kaydetButon.addActionListener(e -> {
            try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
                String sorgu = """
                    UPDATE lab_sonuclari 
                    SET test_turu = ?, sonuc = ?, tarih = ? 
                    WHERE id = ?
                    """;
                PreparedStatement stmt = baglanti.prepareStatement(sorgu);
                stmt.setString(1, testTuruAlani.getText().trim());
                stmt.setString(2, sonucAlani.getText().trim());
                stmt.setString(3, tarihAlani.getText().trim());
                stmt.setInt(4, labSonucId);
                
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(dialog, "Lab sonucu güncellendi.");
                labSonuclariYukle();
                dialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Lab sonucu güncellenirken hata: " + ex.getMessage(),
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

        dialog.pack();
        dialog.setLocationRelativeTo(pencere);
        dialog.setVisible(true);
    }

    private void labSonucunaGoreReceteYaz() {
        int secilenSatir = labSonucTablosu.getSelectedRow();
        if (secilenSatir == -1) {
            JOptionPane.showMessageDialog(pencere, "Lütfen bir lab sonucu seçin.");
            return;
        }

        try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
            String hastaSorgu = "SELECT hasta_id FROM lab_sonuclari WHERE id = ?";
            PreparedStatement hastaStmt = baglanti.prepareStatement(hastaSorgu);
            hastaStmt.setInt(1, (int)labSonucTablosu.getValueAt(secilenSatir, 0));
            ResultSet hastaSonuc = hastaStmt.executeQuery();
            
            if (!hastaSonuc.next()) {
                JOptionPane.showMessageDialog(pencere, "Lab sonucu bilgisi bulunamadı!");
                return;
            }
            int hastaId = hastaSonuc.getInt("hasta_id");

            JDialog dialog = new JDialog(pencere, "Lab Sonucuna Göre Reçete Yaz", true);
            dialog.setLayout(new GridLayout(4, 2, 10, 10));
            dialog.setSize(400, 300);

            JComboBox<Ilac> ilacSecimKutusu = new JComboBox<>();
            JTextField adetAlani = new JTextField();
            JTextArea notlarAlani = new JTextArea();

            String ilacSorgu = "SELECT * FROM ilaclar WHERE stok > 0";
            ResultSet ilacSonuc = baglanti.createStatement().executeQuery(ilacSorgu);
            while (ilacSonuc.next()) {
                ilacSecimKutusu.addItem(new Ilac(
                    ilacSonuc.getInt("id"),
                    ilacSonuc.getString("ilac_adi"),
                    ilacSonuc.getInt("stok"),
                    ilacSonuc.getDouble("fiyat")
                ));
            }

            dialog.add(new JLabel("İlaç:"));
            dialog.add(ilacSecimKutusu);
            dialog.add(new JLabel("Adet:"));
            dialog.add(adetAlani);
            dialog.add(new JLabel("Notlar:"));
            dialog.add(notlarAlani);

            JButton kaydetButon = UIStyle.createStyledButton("Kaydet");
            kaydetButon.addActionListener(e -> {
                try {
                    Ilac secilenIlac = (Ilac) ilacSecimKutusu.getSelectedItem();
                    int adet = Integer.parseInt(adetAlani.getText());

                    if (adet <= 0) {
                        JOptionPane.showMessageDialog(dialog, "Adet 0'dan büyük olmalıdır.");
                        return;
                    }

                    if (adet > secilenIlac.getStok()) {
                        JOptionPane.showMessageDialog(dialog, "Stokta yeterli ilaç yok!");
                        return;
                    }

                    String receteSorgu = """
                        INSERT INTO receteler (hastaId, doktorId, ilac_id, adet, lab_sonuc_id, notlar) 
                        VALUES (?, ?, ?, ?, ?, ?)
                        """;
                    
                    PreparedStatement stmt = baglanti.prepareStatement(receteSorgu);
                    stmt.setInt(1, hastaId);
                    stmt.setInt(2, doktor.getId());
                    stmt.setInt(3, secilenIlac.getId());
                    stmt.setInt(4, adet);
                    stmt.setInt(5, (int)labSonucTablosu.getValueAt(secilenSatir, 0));
                    stmt.setString(6, notlarAlani.getText());
                    
                    stmt.executeUpdate();

                    baglanti.createStatement().executeUpdate(
                        "UPDATE ilaclar SET stok = stok - " + adet + 
                        " WHERE id = " + secilenIlac.getId());

                    JOptionPane.showMessageDialog(dialog, "Reçete başarıyla yazıldı.");
                    dialog.dispose();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Reçete yazılırken hata: " + ex.getMessage(),
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
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(pencere, 
                "Hata: " + ex.getMessage(),
                "Hata",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}

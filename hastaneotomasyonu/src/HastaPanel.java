import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Hasta;
import models.Doktor;

public class HastaPanel {
    private final JFrame pencere;
    private final Hasta hasta;
    private JTable randevuTablosu, receteTablosu, labSonucTablosu, sigortaTablosu, faturaTablosu, sevkTablosu;

    public HastaPanel(Hasta hasta) {
        this.hasta = hasta;
        this.pencere = new JFrame("Hasta Paneli - " + hasta.getAdSoyad());
        UIStyle.styleFrame(pencere);
        pencere.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pencere.setLocationRelativeTo(null);
        pencere.getContentPane().setBackground(new Color(240, 240, 245));

        JTabbedPane sekmeler = new JTabbedPane();
        UIStyle.styleTabbedPane(sekmeler);
        sekmeler.setFont(new Font("Arial", Font.PLAIN, 12));

        JPanel bilgilerPanel = new JPanel(new BorderLayout(10, 10));
        UIStyle.stylePanel(bilgilerPanel);
        bilgilerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel bilgilerForm = new JPanel(new GridLayout(5, 2, 10, 10));
        UIStyle.stylePanel(bilgilerForm);

        JTextField adAlani = new JTextField();
        JTextField soyadAlani = new JTextField();
        JTextField tcknoAlani = new JTextField();
        JTextField iletisimAlani = new JTextField();

        bilgilerForm.add(new JLabel("Ad:"));
        bilgilerForm.add(adAlani);
        bilgilerForm.add(new JLabel("Soyad:"));
        bilgilerForm.add(soyadAlani);
        bilgilerForm.add(new JLabel("TCKNO:"));
        bilgilerForm.add(tcknoAlani);
        bilgilerForm.add(new JLabel("İletişim:"));
        bilgilerForm.add(iletisimAlani);

        JButton guncelleButonu = UIStyle.createStyledButton("Bilgileri Güncelle");
        guncelleButonu.setFocusPainted(false);
        guncelleButonu.addActionListener(e -> bilgilerGuncelle(adAlani, soyadAlani, iletisimAlani));

        bilgilerPanel.add(bilgilerForm, BorderLayout.CENTER);
        bilgilerPanel.add(guncelleButonu, BorderLayout.SOUTH);
        sekmeler.add("Kişisel Bilgiler", bilgilerPanel);

        JPanel randevuPanel = new JPanel(new BorderLayout(10, 10));
        UIStyle.stylePanel(randevuPanel);
        randevuPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        randevuTablosu = new JTable();
        UIStyle.styleTable(randevuTablosu);
        JScrollPane randevuKaydirma = new JScrollPane(randevuTablosu);

        JPanel randevuButonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        UIStyle.stylePanel(randevuButonPanel);

        JButton yeniRandevuButonu = UIStyle.createStyledButton("Yeni Randevu");
        JButton randevuDuzenleButonu = UIStyle.createStyledButton("Randevu Düzenle");
        JButton randevuSilButonu = UIStyle.createStyledButton("Randevu Sil");

        yeniRandevuButonu.addActionListener(e -> randevuAl());
        randevuDuzenleButonu.addActionListener(e -> randevuDuzenle());
        randevuSilButonu.addActionListener(e -> randevuSil());

        randevuButonPanel.add(yeniRandevuButonu);
        randevuButonPanel.add(randevuDuzenleButonu);
        randevuButonPanel.add(randevuSilButonu);

        randevuPanel.add(randevuKaydirma, BorderLayout.CENTER);
        randevuPanel.add(randevuButonPanel, BorderLayout.SOUTH);
        sekmeler.add("Randevular", randevuPanel);

        JPanel recetePanel = new JPanel(new BorderLayout());
        receteTablosu = new JTable();
        UIStyle.styleTable(receteTablosu);
        JScrollPane receteKaydirma = new JScrollPane(receteTablosu);

        recetePanel.add(receteKaydirma, BorderLayout.CENTER);
        sekmeler.add("Reçeteler", recetePanel);

        JPanel labSonucPanel = new JPanel(new BorderLayout());
        labSonucTablosu = new JTable();
        UIStyle.styleTable(labSonucTablosu);
        JScrollPane labSonucKaydirma = new JScrollPane(labSonucTablosu);

        labSonucPanel.add(labSonucKaydirma, BorderLayout.CENTER);
        sekmeler.add("Lab Sonuçları", labSonucPanel);

        JPanel sigortaPanel = new JPanel(new BorderLayout());
        sigortaTablosu = new JTable();
        UIStyle.styleTable(sigortaTablosu);
        JScrollPane sigortaKaydirma = new JScrollPane(sigortaTablosu);

        sigortaPanel.add(sigortaKaydirma, BorderLayout.CENTER);
        sekmeler.add("Sigorta Bilgileri", sigortaPanel);

        JPanel faturaPanel = new JPanel(new BorderLayout());
        faturaTablosu = new JTable();
        UIStyle.styleTable(faturaTablosu);
        JScrollPane faturaKaydirma = new JScrollPane(faturaTablosu);

        JPanel faturaButonPanel = new JPanel();
        JButton odeButonu = UIStyle.createStyledButton("Öde");

        odeButonu.addActionListener(e -> faturaOdemeIslemi());
        faturaButonPanel.add(odeButonu);

        faturaPanel.add(faturaKaydirma, BorderLayout.CENTER);
        faturaPanel.add(faturaButonPanel, BorderLayout.SOUTH);
        sekmeler.add("Faturalar", faturaPanel);

        pencere.add(sekmeler);

        faturalarYukle();

        JPanel sevkPanel = new JPanel(new BorderLayout());
        sevkTablosu = new JTable();
        UIStyle.styleTable(sevkTablosu);
        JScrollPane sevkKaydirma = new JScrollPane(sevkTablosu);

        sevkPanel.add(sevkKaydirma, BorderLayout.CENTER);
        sekmeler.add("Sevkler", sevkPanel);

        pencere.add(sekmeler);

        JButton geriButonu = UIStyle.createStyledButton("Ana Sayfa");
        geriButonu.setFocusPainted(false);
        geriButonu.addActionListener(e -> {
            new GirisEkrani();
            pencere.dispose();
        });

        pencere.add(geriButonu, BorderLayout.SOUTH);

        hastaBilgileriYukle(adAlani, soyadAlani, tcknoAlani, iletisimAlani);
        randevulariYukle();
        receteleriYukle();
        labSonuclariYukle();
        sigortaBilgileriYukle();
        faturalarYukle();
        sevklerYukle();

        pencere.setVisible(true);
    }

    private void hastaBilgileriYukle(JTextField ad, JTextField soyad, JTextField tc, JTextField iletisim) {
        try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
            ResultSet sonuc = baglanti.createStatement().executeQuery(
                    "SELECT ad, soyad, tckno, iletisim_bilgileri FROM hastalar WHERE id = " + hasta.getId()
            );

            if (sonuc.next()) {
                ad.setText(sonuc.getString("ad"));
                soyad.setText(sonuc.getString("soyad"));
                tc.setText(sonuc.getString("tckno"));
                iletisim.setText(sonuc.getString("iletisim_bilgileri"));
            }
        } catch (SQLException e) {
            System.out.println("hata" + e.getMessage());
        }
    }

    private void bilgilerGuncelle(JTextField ad, JTextField soyad, JTextField iletisim) {
        try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
            baglanti.createStatement().executeUpdate(
                    "UPDATE hastalar SET ad = '" + ad.getText() + "', soyad = '" + soyad.getText() + "', iletisim_bilgileri = '" + iletisim.getText() + "' WHERE id = " + hasta.getId()
            );
            JOptionPane.showMessageDialog(null, "Bilgiler güncellendi.");
        } catch (SQLException e) {
            System.out.println("hata" + e.getMessage());
        }
    }

    private void randevulariYukle() {
        try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
            String sorgu = """
                SELECT r.*, d.ad_soyad as doktor_adi, d.uzmanlik 
                FROM randevular r 
                JOIN doktorlar d ON r.doktorId = d.id 
                WHERE r.hastaId = ? 
                ORDER BY r.tarih, r.saat
                """;
            
            PreparedStatement stmt = baglanti.prepareStatement(sorgu);
            stmt.setInt(1, hasta.getId());
            ResultSet sonuc = stmt.executeQuery();

            String[] sutunlar = {"ID", "Doktor", "Uzmanlık", "Tarih", "Saat", "Durum"};
            DefaultTableModel model = new DefaultTableModel(sutunlar, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            while (sonuc.next()) {
                model.addRow(new Object[]{
                    sonuc.getInt("id"),
                    sonuc.getString("doktor_adi"),
                    sonuc.getString("uzmanlik"),
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

    private void receteleriYukle() {
        try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
            String sorgu = """
                SELECT r.*, d.ad_soyad as doktor_adi, i.ilac_adi, i.fiyat 
                FROM receteler r 
                JOIN doktorlar d ON r.doktorId = d.id 
                JOIN ilaclar i ON r.ilac_id = i.id 
                WHERE r.hastaId = ? 
                ORDER BY r.id DESC
                """;
            
            PreparedStatement stmt = baglanti.prepareStatement(sorgu);
            stmt.setInt(1, hasta.getId());
            ResultSet sonuc = stmt.executeQuery();

            String[] sutunlar = {"ID", "Doktor", "İlaç", "Adet", "Notlar"};
            DefaultTableModel model = new DefaultTableModel(sutunlar, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            while (sonuc.next()) {
                model.addRow(new Object[]{
                    sonuc.getInt("id"),
                    sonuc.getString("doktor_adi"),
                    sonuc.getString("ilac_adi"),
                    sonuc.getInt("adet"),
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

    private void faturalarYukle() {
        try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
            String sorgu = """
                SELECT f.id, f.tedavi_bilgileri, 
                       DATE_FORMAT(f.tarih, '%Y-%m-%d %H:%i') as tarih, 
                       f.masraf, f.odeme_durumu,
                       COALESCE((SELECT SUM(odeme_miktari) 
                        FROM odeme_gecmisi 
                        WHERE fatura_id = f.id), 0) as toplam_odenen
                FROM faturalar f
                WHERE f.hasta_id = ?
                ORDER BY f.tarih DESC
                """;
            
            PreparedStatement stmt = baglanti.prepareStatement(sorgu);
            stmt.setInt(1, hasta.getId());
            ResultSet sonuc = stmt.executeQuery();

            List<Object[]> veriler = new ArrayList<>();
            while (sonuc.next()) {
                veriler.add(new Object[]{
                    sonuc.getInt("id"),
                    sonuc.getString("tedavi_bilgileri"),
                    sonuc.getString("tarih"),
                    sonuc.getDouble("masraf"),
                    sonuc.getString("odeme_durumu"),
                    sonuc.getDouble("toplam_odenen")
                });
            }

            faturaTablosu.setModel(new javax.swing.table.DefaultTableModel(
                veriler.toArray(new Object[0][]),
                new String[]{"ID", "Tedavi Bilgileri", "Tarih", "Kalan Masraf", "Ödeme Durumu", "Toplam Ödenen"}
            ));

            faturaTablosu.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    if (value instanceof Double) {
                        value = String.format("%.2f TL", (Double) value);
                    }
                    return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                }
            });

            faturaTablosu.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    if (value instanceof Double) {
                        value = String.format("%.2f TL", (Double) value);
                    }
                    return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                }
            });

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Faturalar yüklenirken hata: " + e.getMessage());
        }
    }

    private void faturaOdemeIslemi() {
        int secilenSatir = faturaTablosu.getSelectedRow();
        if (secilenSatir == -1) {
            JOptionPane.showMessageDialog(null, "Lütfen ödemek istediğiniz faturayı seçin.");
            return;
        }

        int faturaId = (int) faturaTablosu.getValueAt(secilenSatir, 0);
        double toplamMasraf = (double) faturaTablosu.getValueAt(secilenSatir, 3);
        String odemeDurumu = faturaTablosu.getValueAt(secilenSatir, 4).toString();

        if (odemeDurumu.equals("Ödendi")) {
            JOptionPane.showMessageDialog(null, "Bu fatura zaten ödenmiş.");
            return;
        }

        JTextField odemeMiktariAlani = new JTextField();
        Object[] mesaj = {
            "Toplam Masraf: " + toplamMasraf + " TL",
            "Ödenecek Miktar (TL):", odemeMiktariAlani
        };

        int sonuc = JOptionPane.showConfirmDialog(null, mesaj, "Fatura Ödeme", JOptionPane.OK_CANCEL_OPTION);

        if (sonuc == JOptionPane.OK_OPTION) {
            try {
                double odemeMiktari = Double.parseDouble(odemeMiktariAlani.getText());
                
                if (odemeMiktari <= 0) {
                    JOptionPane.showMessageDialog(null, "Geçerli bir ödeme miktarı giriniz.");
                    return;
                }

                if (odemeMiktari > toplamMasraf) {
                    JOptionPane.showMessageDialog(null, "Ödeme miktarı fatura tutarından büyük olamaz.");
                    return;
                }

                try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
                    String sorgu;
                    PreparedStatement stmt;

                    if (odemeMiktari == toplamMasraf) {
                        sorgu = "UPDATE faturalar SET odeme_durumu = 'Ödendi', masraf = 0 WHERE id = ?";
                        stmt = baglanti.prepareStatement(sorgu);
                        stmt.setInt(1, faturaId);
                    } else {
                        sorgu = "UPDATE faturalar SET masraf = masraf - ?, odeme_durumu = 'Kısmi Ödeme' WHERE id = ?";
                        stmt = baglanti.prepareStatement(sorgu);
                        stmt.setDouble(1, odemeMiktari);
                        stmt.setInt(2, faturaId);
                    }

                    stmt.executeUpdate();

                    String odemeKayitSorgu = """
                        INSERT INTO odeme_gecmisi (fatura_id, odeme_miktari, odeme_tarihi) 
                        VALUES (?, ?, CURRENT_TIMESTAMP)
                        """;
                    PreparedStatement odemeStmt = baglanti.prepareStatement(odemeKayitSorgu);
                    odemeStmt.setInt(1, faturaId);
                    odemeStmt.setDouble(2, odemeMiktari);
                    odemeStmt.executeUpdate();

                    if (odemeMiktari == toplamMasraf) {
                        JOptionPane.showMessageDialog(null, "Fatura tamamen ödendi.");
                    } else {
                        double kalanBorc = toplamMasraf - odemeMiktari;
                        JOptionPane.showMessageDialog(null, String.format(
                            "Kısmi ödeme yapıldı.\nÖdenen: %.2f TL\nKalan: %.2f TL", 
                            odemeMiktari, kalanBorc));
                    }

                    faturalarYukle();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Ödeme işlemi sırasında hata: " + ex.getMessage());
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Lütfen geçerli bir sayı giriniz.");
            }
        }
    }

    private void sigortaBilgileriYukle() {
        try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
            ResultSet sonuc = baglanti.createStatement().executeQuery(
                    "SELECT id, sigorta_turu, sigorta_detayi FROM sigortalar WHERE hasta_id = " + hasta.getId()
            );

            List<Object[]> veriler = new ArrayList<>();
            while (sonuc.next()) {
                veriler.add(new Object[]{
                        sonuc.getInt("id"),
                        sonuc.getString("sigorta_turu"),
                        sonuc.getString("sigorta_detayi")
                });
            }

            sigortaTablosu.setModel(new javax.swing.table.DefaultTableModel(
                    veriler.toArray(new Object[0][]),
                    new String[]{"ID", "Sigorta Türü", "Sigorta Detayı"}
            ));
        } catch (SQLException e) {
            System.out.println("Sigorta Bilgileri Yüklenirken Hata: " + e.getMessage());
        }
    }

    private void sevklerYukle() {
        try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
            ResultSet sonuc = baglanti.createStatement().executeQuery(
                    "SELECT id, sevk_nedeni, hedef_hastane, tarih FROM sevkler WHERE hasta_id = " + hasta.getId()
            );

            List<Object[]> veriler = new ArrayList<>();
            while (sonuc.next()) {
                veriler.add(new Object[]{
                        sonuc.getInt("id"),
                        sonuc.getString("sevk_nedeni"),
                        sonuc.getString("hedef_hastane"),
                        sonuc.getString("tarih")
                });
            }

            sevkTablosu.setModel(new javax.swing.table.DefaultTableModel(
                    veriler.toArray(new Object[0][]),
                    new String[]{"Sevk ID", "Sevk Nedeni", "Hedef Hastane", "Tarih"}
            ));
        } catch (SQLException e) {
            System.out.println("Sevkler Yüklenirken Hata: " + e.getMessage());
        }
    }

    private void labSonuclariYukle() {
        try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
            String sorgu = """
                SELECT l.*, d.ad_soyad as doktor_adi 
                FROM lab_sonuclari l 
                JOIN doktorlar d ON l.doktor_id = d.id 
                WHERE l.hasta_id = ? 
                ORDER BY l.tarih DESC
                """;
            
            PreparedStatement stmt = baglanti.prepareStatement(sorgu);
            stmt.setInt(1, hasta.getId());
            ResultSet sonuc = stmt.executeQuery();

            String[] sutunlar = {"ID", "Doktor", "Test Türü", "Sonuç", "Tarih"};
            DefaultTableModel model = new DefaultTableModel(sutunlar, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            while (sonuc.next()) {
                model.addRow(new Object[]{
                    sonuc.getInt("id"),
                    sonuc.getString("doktor_adi"),
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

    private void randevuAl() {
        JDialog dialog = new JDialog(pencere, "Yeni Randevu Al", true);
        dialog.setLayout(new GridLayout(5, 2, 10, 10));
        dialog.setSize(400, 300);

        JComboBox<String> bransCombo = new JComboBox<>();
        JComboBox<Doktor> doktorCombo = new JComboBox<>();
        JTextField tarihAlani = new JTextField();
        JTextField saatAlani = new JTextField();

        dialog.add(new JLabel("Branş:"));
        dialog.add(bransCombo);
        dialog.add(new JLabel("Doktor:"));
        dialog.add(doktorCombo);
        dialog.add(new JLabel("Tarih (YYYY-MM-DD):"));
        dialog.add(tarihAlani);
        dialog.add(new JLabel("Saat (HH:MM):"));
        dialog.add(saatAlani);

        try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
            String sorgu = "SELECT DISTINCT uzmanlik FROM doktorlar ORDER BY uzmanlik";
            ResultSet sonuc = baglanti.createStatement().executeQuery(sorgu);
            while (sonuc.next()) {
                bransCombo.addItem(sonuc.getString("uzmanlik"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(dialog, "Branşlar yüklenirken hata: " + e.getMessage());
        }

        bransCombo.addActionListener(e -> {
            doktorCombo.removeAllItems();
            String secilenBrans = (String) bransCombo.getSelectedItem();
            
            try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
                String sorgu = "SELECT * FROM doktorlar WHERE uzmanlik = ?";
                PreparedStatement stmt = baglanti.prepareStatement(sorgu);
                stmt.setString(1, secilenBrans);
                ResultSet sonuc = stmt.executeQuery();

                while (sonuc.next()) {
                    Doktor doktor = new Doktor(
                        sonuc.getInt("id"),
                        sonuc.getString("tckno"),
                        sonuc.getString("sifre"),
                        sonuc.getString("ad_soyad")
                    );
                    doktor.setUzmanlikAlani(sonuc.getString("uzmanlik"));
                    doktorCombo.addItem(doktor);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Doktorlar yüklenirken hata: " + ex.getMessage());
            }
        });

        JButton kaydetButon = UIStyle.createStyledButton("Randevu Al");
        kaydetButon.addActionListener(e -> {
            try {
                Doktor secilenDoktor = (Doktor) doktorCombo.getSelectedItem();
                if (secilenDoktor == null) {
                    JOptionPane.showMessageDialog(dialog, "Lütfen bir doktor seçin.");
                    return;
                }

                String tarih = tarihAlani.getText().trim();
                String saat = saatAlani.getText().trim();

                if (tarih.isEmpty() || saat.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Tarih ve saat alanları boş bırakılamaz.");
                    return;
                }

                try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
                    String kontrolSorgusu = """
                        SELECT COUNT(*) as sayi FROM randevular 
                        WHERE (hastaId = ? OR doktorId = ?) 
                        AND tarih = ? AND saat = ?
                        """;
                    PreparedStatement kontrolStmt = baglanti.prepareStatement(kontrolSorgusu);
                    kontrolStmt.setInt(1, hasta.getId());
                    kontrolStmt.setInt(2, secilenDoktor.getId());
                    kontrolStmt.setString(3, tarih);
                    kontrolStmt.setString(4, saat);
                    ResultSet kontrolSonuc = kontrolStmt.executeQuery();

                    if (kontrolSonuc.next() && kontrolSonuc.getInt("sayi") > 0) {
                        JOptionPane.showMessageDialog(dialog, 
                            "Bu tarih ve saatte randevu mevcut!",
                            "Uyarı",
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    String kaydetSorgusu = """
                        INSERT INTO randevular (hastaId, doktorId, tarih, saat, durum) 
                        VALUES (?, ?, ?, ?, 'Bekliyor')
                        """;
                    PreparedStatement kaydetStmt = baglanti.prepareStatement(kaydetSorgusu);
                    kaydetStmt.setInt(1, hasta.getId());
                    kaydetStmt.setInt(2, secilenDoktor.getId());
                    kaydetStmt.setString(3, tarih);
                    kaydetStmt.setString(4, saat);
                    kaydetStmt.executeUpdate();

                    JOptionPane.showMessageDialog(dialog, "Randevu başarıyla alındı.");
                    dialog.dispose();
                    randevulariYukle();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Randevu alınırken hata: " + ex.getMessage(),
                    "Hata",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.add(kaydetButon);
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
                String sorgu = "UPDATE randevular SET tarih = ?, saat = ? WHERE id = ? AND hastaId = ?";
                PreparedStatement stmt = baglanti.prepareStatement(sorgu);
                stmt.setString(1, tarihAlani.getText().trim());
                stmt.setString(2, saatAlani.getText().trim());
                stmt.setInt(3, randevuId);
                stmt.setInt(4, hasta.getId());
                
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(dialog, "Randevu başarıyla güncellendi.");
                dialog.dispose();
                randevulariYukle();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Randevu güncellenirken hata: " + ex.getMessage(),
                    "Hata",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.add(kaydetButon);
        dialog.setLocationRelativeTo(pencere);
        dialog.setVisible(true);
    }

    private void randevuSil() {
        int secilenSatir = randevuTablosu.getSelectedRow();
        if (secilenSatir == -1) {
            JOptionPane.showMessageDialog(null, "Silmek için bir randevu seçin.");
            return;
        }

        int randevuId = (int) randevuTablosu.getValueAt(secilenSatir, 0);
        int onay = JOptionPane.showConfirmDialog(
                null, "Bu randevuyu silmek istediğinize emin misiniz?", "Onay", JOptionPane.YES_NO_OPTION
        );

        if (onay == JOptionPane.YES_OPTION) {
            try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
                baglanti.createStatement().executeUpdate("DELETE FROM randevular WHERE id = " + randevuId);
                JOptionPane.showMessageDialog(null, "Randevu silindi.");
                randevulariYukle();
            } catch (SQLException ex) {
                System.out.println("hata"+ex.getMessage());
            }
        }
    }
}
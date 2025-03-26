
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Fatura;
import models.Hasta;
import models.Sevk;

public class SigortaFaturalandirma {

    private JTable sigortaTablosu, faturaTablosu, sevkTablosu;
    private JButton faturaOlusturButonu, sigortaEkleButonu, faturaDuzenleButonu, faturaSilButonu, sigortaDuzenleButonu, sigortaSilButonu, odemeAlButonu, sevkEkleButonu, sevkDuzenleButonu, sevkSilButonu;
    private DefaultTableModel faturaModel, sevkModel;
    private List<Fatura> faturalar;
    private List<Sevk> sevkler;
    private final JPanel panel;

    public SigortaFaturalandirma() {
        panel = new JPanel(new BorderLayout(10, 10));
        UIStyle.stylePanel(panel);

        JTabbedPane sekmeler = new JTabbedPane();
        UIStyle.styleTabbedPane(sekmeler);

        JPanel sigortaPanel = new JPanel(new BorderLayout());
        sigortaTablosu = new JTable();
        JScrollPane sigortaScroll = new JScrollPane(sigortaTablosu);
        JPanel sigortaButonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        UIStyle.stylePanel(sigortaButonPanel);
        
        sigortaEkleButonu = UIStyle.createStyledButton("Sigorta Bilgisi Ekle");
        sigortaDuzenleButonu = UIStyle.createStyledButton("Sigorta Düzenle");
        sigortaSilButonu = UIStyle.createStyledButton("Sigorta Sil");
        
        sigortaEkleButonu.addActionListener(e -> sigortaEkle());
        sigortaDuzenleButonu.addActionListener(e -> sigortaDuzenle());
        sigortaSilButonu.addActionListener(e -> sigortaSil());
        
        sigortaButonPanel.add(sigortaEkleButonu);
        sigortaButonPanel.add(sigortaDuzenleButonu);
        sigortaButonPanel.add(sigortaSilButonu);
        sigortaPanel.add(sigortaScroll, BorderLayout.CENTER);
        sigortaPanel.add(sigortaButonPanel, BorderLayout.SOUTH);
        sekmeler.add("Sigorta Bilgileri", sigortaPanel);

        JPanel faturaPanel = new JPanel(new BorderLayout());
        faturalar = new ArrayList<>();
        faturaTablosu = new JTable();
        JScrollPane faturaScroll = new JScrollPane(faturaTablosu);
        JPanel faturaButonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        UIStyle.stylePanel(faturaButonPanel);
        
        faturaOlusturButonu = UIStyle.createStyledButton("Fatura Oluştur");
        faturaDuzenleButonu = UIStyle.createStyledButton("Fatura Düzenle");
        faturaSilButonu = UIStyle.createStyledButton("Fatura Sil");
        odemeAlButonu = UIStyle.createStyledButton("Ödeme Al");
        
        faturaOlusturButonu.addActionListener(e -> faturaOlustur());
        faturaDuzenleButonu.addActionListener(e -> faturaDuzenle());
        faturaSilButonu.addActionListener(e -> faturaSil());
        odemeAlButonu.addActionListener(e -> odemeAl());
        
        faturaButonPanel.add(faturaOlusturButonu);
        faturaButonPanel.add(faturaDuzenleButonu);
        faturaButonPanel.add(faturaSilButonu);
        faturaButonPanel.add(odemeAlButonu);
        faturaPanel.add(faturaScroll, BorderLayout.CENTER);
        faturaPanel.add(faturaButonPanel, BorderLayout.SOUTH);
        sekmeler.add("Faturalandırma", faturaPanel);

        JPanel sevkPanel = new JPanel(new BorderLayout());
        sevkler = new ArrayList<>();
        sevkTablosu = new JTable();
        JScrollPane sevkScroll = new JScrollPane(sevkTablosu);
        JPanel sevkButonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        UIStyle.stylePanel(sevkButonPanel);
        
        sevkEkleButonu = UIStyle.createStyledButton("Sevk Oluştur");
        sevkDuzenleButonu = UIStyle.createStyledButton("Sevk Düzenle");
        sevkSilButonu = UIStyle.createStyledButton("Sevk Sil");
        
        sevkEkleButonu.addActionListener(e -> sevkOlustur());
        sevkDuzenleButonu.addActionListener(e -> sevkDuzenle());
        sevkSilButonu.addActionListener(e -> sevkSil());
        
        sevkButonPanel.add(sevkEkleButonu);
        sevkButonPanel.add(sevkDuzenleButonu);
        sevkButonPanel.add(sevkSilButonu);
        sevkPanel.add(sevkScroll, BorderLayout.CENTER);
        sevkPanel.add(sevkButonPanel, BorderLayout.SOUTH);
        sekmeler.add("Hasta Sevk", sevkPanel);

        panel.add(sekmeler);

        sigortalariYukle();
        faturalariYukle();
        sevkleriYukle();
    }

    private void sigortalariYukle() {
        try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
            String sorgu = """
                SELECT s.id, h.ad, h.soyad, s.sigorta_turu, s.sigorta_detayi 
                FROM sigortalar s 
                JOIN hastalar h ON s.hasta_id = h.id
                """;
            
            ResultSet sonuc = baglanti.createStatement().executeQuery(sorgu);
            List<Object[]> veriler = new ArrayList<>();
            
            while (sonuc.next()) {
                veriler.add(new Object[]{
                    sonuc.getInt("id"),
                    sonuc.getString("ad") + " " + sonuc.getString("soyad"),
                        sonuc.getString("sigorta_turu"),
                        sonuc.getString("sigorta_detayi")
                });
            }

            sigortaTablosu.setModel(new DefaultTableModel(
                veriler.toArray(new Object[0][]),
                new String[]{"ID", "Hasta", "Sigorta Türü", "Detay"}
            ));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel, 
                "Sigorta bilgileri yüklenirken hata: " + e.getMessage(),
                "Hata",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public void faturalariYukle() {
        try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
            String sorgu = """
                SELECT f.*, h.ad, h.soyad, 
                COALESCE(og.odeme_miktari, 0) as odeme_miktari,
                og.odeme_tarihi
                FROM faturalar f 
                JOIN hastalar h ON f.hasta_id = h.id 
                LEFT JOIN odeme_gecmisi og ON f.id = og.fatura_id
                ORDER BY f.tarih DESC
                """;
            
            ResultSet sonuc = baglanti.createStatement().executeQuery(sorgu);
            faturaModel = new DefaultTableModel(
                new String[]{"ID", "Hasta", "Tedavi Bilgileri", "Masraf", "Ödeme Durumu", "Tarih", "Ödenen", "Ödeme Tarihi"}, 
                0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            faturaTablosu.setModel(faturaModel);
            faturalar.clear();

            while (sonuc.next()) {
                Fatura fatura = new Fatura(
                    sonuc.getInt("id"),
                    sonuc.getInt("hasta_id"),
                        sonuc.getString("tedavi_bilgileri"),
                    sonuc.getDouble("masraf")
                );
                fatura.setOdemeDurumu(sonuc.getString("odeme_durumu"));
                fatura.setTarih(sonuc.getDate("tarih"));
                faturalar.add(fatura);

                faturaModel.addRow(new Object[]{
                    fatura.getId(),
                    sonuc.getString("ad") + " " + sonuc.getString("soyad"),
                    fatura.getTedaviBilgileri(),
                    fatura.getMasraf(),
                    fatura.getOdemeDurumu(),
                    fatura.getTarih(),
                    sonuc.getDouble("odeme_miktari"),
                    sonuc.getDate("odeme_tarihi")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel, 
                "Faturalar yüklenirken hata: " + e.getMessage(),
                "Hata",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sigortaEkle() {
        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(panel), 
            "Sigorta Bilgisi Ekle", true);
        dialog.setTitle("Sigorta Bilgisi Ekle");
        UIStyle.styleDialog(dialog);
        dialog.setModal(true);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        UIStyle.stylePanel(formPanel);

        JComboBox<String> hastaComboBox = new JComboBox<>();
        JComboBox<String> sigortaTuruComboBox = new JComboBox<>(new String[]{"Tam", "Özel", "Temel"});
        JTextField detayAlani = new JTextField();

        UIStyle.styleComboBox(hastaComboBox);
        UIStyle.styleComboBox(sigortaTuruComboBox);
        UIStyle.styleTextField(detayAlani);

        try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
            ResultSet hastalar = baglanti.createStatement().executeQuery(
                "SELECT id, ad, soyad FROM hastalar");
            while (hastalar.next()) {
                String hastaStr = String.format("%d - %s %s", 
                    hastalar.getInt("id"),
                    hastalar.getString("ad"),
                    hastalar.getString("soyad"));
                hastaComboBox.addItem(hastaStr);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(dialog, "Hastalar yüklenirken hata: " + e.getMessage());
            return;
        }

        formPanel.add(new JLabel("Hasta:"));
        formPanel.add(hastaComboBox);
        formPanel.add(new JLabel("Sigorta Türü:"));
        formPanel.add(sigortaTuruComboBox);
        formPanel.add(new JLabel("Detay:"));
        formPanel.add(detayAlani);

        JPanel butonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        UIStyle.stylePanel(butonPanel);
        JButton iptalButon = UIStyle.createStyledButton("İptal");
        JButton kaydetButon = UIStyle.createStyledButton("Kaydet");

        iptalButon.addActionListener(e -> dialog.dispose());
        kaydetButon.addActionListener(e -> {
            try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
                String[] hastaIdParts = hastaComboBox.getSelectedItem().toString().split(" - ");
                int hastaId = Integer.parseInt(hastaIdParts[0]);
                
                String ekleSorgu = "INSERT INTO sigortalar (hasta_id, sigorta_turu, sigorta_detayi) VALUES (?, ?, ?)";
                PreparedStatement ekleStmt = baglanti.prepareStatement(ekleSorgu);
                ekleStmt.setInt(1, hastaId);
                ekleStmt.setString(2, sigortaTuruComboBox.getSelectedItem().toString());
                ekleStmt.setString(3, detayAlani.getText());
                ekleStmt.executeUpdate();
                
                JOptionPane.showMessageDialog(dialog, "Sigorta bilgisi eklendi.");
                sigortalariYukle();
                dialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Hata: " + ex.getMessage());
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

    private void faturaOlustur() {
        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(panel), 
            "Yeni Fatura", true);
        dialog.setLayout(new GridLayout(5, 2, 10, 10));
        dialog.setSize(400, 300);

        JComboBox<String> hastaCombo = new JComboBox<>();
        JTextField tedaviAlani = new JTextField();
        JTextField masrafAlani = new JTextField();

        try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
            String sorgu = "SELECT id, ad, soyad FROM hastalar ORDER BY ad, soyad";
            ResultSet sonuc = baglanti.createStatement().executeQuery(sorgu);
            while (sonuc.next()) {
                hastaCombo.addItem(sonuc.getInt("id") + " - " + 
                    sonuc.getString("ad") + " " + sonuc.getString("soyad"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(dialog, "Hastalar yüklenirken hata: " + e.getMessage());
            return;
        }

        dialog.add(new JLabel("Hasta:"));
        dialog.add(hastaCombo);
        dialog.add(new JLabel("Tedavi Bilgileri:"));
        dialog.add(tedaviAlani);
        dialog.add(new JLabel("Masraf:"));
        dialog.add(masrafAlani);

        JButton kaydetButon = UIStyle.createStyledButton("Kaydet");
        kaydetButon.addActionListener(e -> {
            try {
                String secilenHasta = (String) hastaCombo.getSelectedItem();
                int hastaId = Integer.parseInt(secilenHasta.split(" - ")[0]);
                double masraf = Double.parseDouble(masrafAlani.getText().trim());

                try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
                    double odenecekTutar = hesaplaSigortaliTutar(baglanti, hastaId, masraf);

                    String sorgu = """
                        INSERT INTO faturalar 
                        (hasta_id, tedavi_bilgileri, masraf, odeme_durumu, tarih)
                        VALUES (?, ?, ?, 'Ödenmedi', CURRENT_DATE)
                        """;
                    PreparedStatement stmt = baglanti.prepareStatement(sorgu);
                    stmt.setInt(1, hastaId);
                    stmt.setString(2, tedaviAlani.getText().trim());
                    stmt.setDouble(3, masraf);
                    
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(dialog, "Fatura başarıyla oluşturuldu.");
                    dialog.dispose();
                    faturalariYukle();
                }
            } catch (SQLException | NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Fatura oluşturulurken hata: " + ex.getMessage(),
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

        dialog.setLocationRelativeTo(panel);
        dialog.setVisible(true);
    }

    private double hesaplaSigortaliTutar(Connection baglanti, int hastaId, double masraf) 
        throws SQLException {
        String sigortaSorgu = "SELECT sigorta_turu FROM sigortalar WHERE hasta_id = ?";
        PreparedStatement sigortaStmt = baglanti.prepareStatement(sigortaSorgu);
        sigortaStmt.setInt(1, hastaId);
        ResultSet sigortaSonuc = sigortaStmt.executeQuery();
        
        double sigortaIndirimOrani = 0.0;
        if (sigortaSonuc.next()) {
            String sigortaTuru = sigortaSonuc.getString("sigorta_turu");
            sigortaIndirimOrani = switch (sigortaTuru.toLowerCase()) {
                case "tam" -> 1.0;
                case "özel" -> 0.8;
                case "temel" -> 0.5;
                default -> 0.0;
            };
        }
        return masraf * (1 - sigortaIndirimOrani);
    }

    private void odemeAl() {
        int secilenSatir = faturaTablosu.getSelectedRow();
        if (secilenSatir == -1) {
            JOptionPane.showMessageDialog(panel, "Lütfen bir fatura seçin.");
            return;
        }

        Fatura fatura = faturalar.get(secilenSatir);
        if ("Ödendi".equals(fatura.getOdemeDurumu())) {
            JOptionPane.showMessageDialog(panel, "Bu fatura zaten ödenmiş.");
            return;
        }

        int secim = JOptionPane.showConfirmDialog(
            panel,
            String.format("Ödenecek tutar: %.2f TL\nÖdemeyi onaylıyor musunuz?", 
                fatura.getMasraf()),
            "Ödeme Onayı",
            JOptionPane.YES_NO_OPTION
        );

        if (secim == JOptionPane.YES_OPTION) {
            try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
                baglanti.setAutoCommit(false);
                try {
                    String faturaSorgu = "UPDATE faturalar SET odeme_durumu = 'Ödendi' WHERE id = ?";
                    PreparedStatement faturaStmt = baglanti.prepareStatement(faturaSorgu);
                    faturaStmt.setInt(1, fatura.getId());
                    faturaStmt.executeUpdate();

                    String odemeSorgu = """
                        INSERT INTO odeme_gecmisi 
                        (fatura_id, odeme_miktari, odeme_tarihi)  
                        VALUES (?, ?, CURRENT_DATE)
                        """;
                    PreparedStatement odemeStmt = baglanti.prepareStatement(odemeSorgu);
                    odemeStmt.setInt(1, fatura.getId());
                    odemeStmt.setDouble(2, fatura.getMasraf());
                    odemeStmt.executeUpdate();

                    baglanti.commit();
                    JOptionPane.showMessageDialog(panel, "Ödeme başarıyla alındı.");
                    faturalariYukle();
                } catch (SQLException ex) {
                    baglanti.rollback();
                    throw ex;
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(panel, 
                    "Ödeme alınırken hata: " + e.getMessage(),
                    "Hata",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public JPanel getPanel() {
        return panel;
    }

    private void sigortaDuzenle() {
        int seciliSatir = sigortaTablosu.getSelectedRow();
        if (seciliSatir == -1) {
            JOptionPane.showMessageDialog(null, "Lütfen düzenlemek için bir sigorta seçin.");
            return;
        }

        int sigortaId = (int) sigortaTablosu.getValueAt(seciliSatir, 0);
        String mevcutTur = (String) sigortaTablosu.getValueAt(seciliSatir, 2);
        String mevcutDetay = (String) sigortaTablosu.getValueAt(seciliSatir, 3);

        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(panel), 
            "Sigorta Bilgisi Düzenle", true);
        dialog.setTitle("Sigorta Bilgisi Düzenle");
        UIStyle.styleDialog(dialog);
        dialog.setModal(true);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        UIStyle.stylePanel(formPanel);

        JComboBox<String> sigortaTuruComboBox = new JComboBox<>(new String[]{"Tam", "Özel", "Temel"});
        sigortaTuruComboBox.setSelectedItem(mevcutTur);
        JTextField detayAlani = new JTextField(mevcutDetay);

        UIStyle.styleComboBox(sigortaTuruComboBox);
        UIStyle.styleTextField(detayAlani);

        formPanel.add(new JLabel("Sigorta Türü:"));
        formPanel.add(sigortaTuruComboBox);
        formPanel.add(new JLabel("Detay:"));
        formPanel.add(detayAlani);

        JPanel butonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        UIStyle.stylePanel(butonPanel);
        JButton iptalButon = UIStyle.createStyledButton("İptal");
        JButton kaydetButon = UIStyle.createStyledButton("Kaydet");

        iptalButon.addActionListener(e -> dialog.dispose());
        kaydetButon.addActionListener(e -> {
            try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
                String sorgu = "UPDATE sigortalar SET sigorta_turu = ?, sigorta_detayi = ? WHERE id = ?";
                PreparedStatement stmt = baglanti.prepareStatement(sorgu);
                stmt.setString(1, sigortaTuruComboBox.getSelectedItem().toString());
                stmt.setString(2, detayAlani.getText());
                stmt.setInt(3, sigortaId);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(dialog, "Sigorta bilgileri güncellendi.");
                sigortalariYukle();
                dialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Hata: " + ex.getMessage());
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

    private void sigortaSil() {
        int seciliSatir = sigortaTablosu.getSelectedRow();
        if (seciliSatir == -1) {
            JOptionPane.showMessageDialog(panel, "Lütfen silmek için bir sigorta kaydı seçin.");
            return;
        }

        int sigortaId = (int) sigortaTablosu.getValueAt(seciliSatir, 0);
        String hastaAdi = (String) sigortaTablosu.getValueAt(seciliSatir, 1);
        String sigortaTuru = (String) sigortaTablosu.getValueAt(seciliSatir, 2);

        JDialog onayDialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(panel), "Sigorta Sil", true);
        UIStyle.styleDialog(onayDialog);
        onayDialog.setModal(true);
        onayDialog.setLayout(new BorderLayout(10, 10));

        JPanel mesajPanel = new JPanel();
        UIStyle.stylePanel(mesajPanel);
        JLabel mesajLabel = new JLabel(String.format(
            "%s adlı hastanın %s türündeki sigorta kaydını silmek istediğinizden emin misiniz?",
            hastaAdi, sigortaTuru
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
                String sorgu = "DELETE FROM sigortalar WHERE id = ?";
                PreparedStatement stmt = baglanti.prepareStatement(sorgu);
                stmt.setInt(1, sigortaId);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(onayDialog, "Sigorta kaydı başarıyla silindi.");
                sigortalariYukle();
                onayDialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(onayDialog, "Hata: " + ex.getMessage());
            }
        });

        butonPanel.add(iptalButon);
        butonPanel.add(silButon);

        onayDialog.add(mesajPanel, BorderLayout.CENTER);
        onayDialog.add(butonPanel, BorderLayout.SOUTH);

        onayDialog.pack();
        onayDialog.setLocationRelativeTo(panel);
        onayDialog.setVisible(true);
    }

    private void faturaDuzenle() {
        int seciliSatir = faturaTablosu.getSelectedRow();
        if (seciliSatir == -1) {
            JOptionPane.showMessageDialog(panel, "Lütfen düzenlemek için bir fatura seçin.");
            return;
        }

        int faturaId = (int) faturaTablosu.getValueAt(seciliSatir, 0);
        String tedaviBilgileri = (String) faturaTablosu.getValueAt(seciliSatir, 2);
        double masraf = (double) faturaTablosu.getValueAt(seciliSatir, 3);

        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(panel), "Fatura Düzenle", true);
        UIStyle.styleDialog(dialog);
        dialog.setModal(true);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        UIStyle.stylePanel(formPanel);

        JTextField tedaviBilgileriAlani = new JTextField(tedaviBilgileri);
        JTextField masrafAlani = new JTextField(String.valueOf(masraf));

        UIStyle.styleTextField(tedaviBilgileriAlani);
        UIStyle.styleTextField(masrafAlani);

        formPanel.add(new JLabel("Tedavi Bilgileri:"));
        formPanel.add(tedaviBilgileriAlani);
        formPanel.add(new JLabel("Masraf:"));
        formPanel.add(masrafAlani);

        JPanel butonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        UIStyle.stylePanel(butonPanel);
        JButton iptalButon = UIStyle.createStyledButton("İptal");
        JButton kaydetButon = UIStyle.createStyledButton("Kaydet");

        iptalButon.addActionListener(e -> dialog.dispose());
        kaydetButon.addActionListener(e -> {
            try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
                String sorgu = """
                    UPDATE faturalar 
                    SET tedavi_bilgileri = ?, masraf = ? 
                    WHERE id = ?
                    """;
                
                PreparedStatement stmt = baglanti.prepareStatement(sorgu);
                stmt.setString(1, tedaviBilgileriAlani.getText());
                stmt.setDouble(2, Double.parseDouble(masrafAlani.getText()));
                stmt.setInt(3, faturaId);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(dialog, "Fatura başarıyla güncellendi.");
                faturalariYukle();
                dialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Hata: " + ex.getMessage());
            }
        });

        butonPanel.add(iptalButon);
        butonPanel.add(kaydetButon);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(butonPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(panel);
        dialog.setVisible(true);
    }

    private void faturaSil() {
        int seciliSatir = faturaTablosu.getSelectedRow();
        if (seciliSatir == -1) {
            JOptionPane.showMessageDialog(panel, "Lütfen silmek için bir fatura seçin.");
            return;
        }

        int faturaId = (int) faturaTablosu.getValueAt(seciliSatir, 0);
        String hastaAdi = (String) faturaTablosu.getValueAt(seciliSatir, 1);
        double masraf = (double) faturaTablosu.getValueAt(seciliSatir, 3);

        JDialog onayDialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(panel), "Fatura Sil", true);
        UIStyle.styleDialog(onayDialog);
        onayDialog.setModal(true);
        onayDialog.setLayout(new BorderLayout(10, 10));

        JPanel mesajPanel = new JPanel();
        UIStyle.stylePanel(mesajPanel);
        JLabel mesajLabel = new JLabel(String.format(
            "%s adlı hastanın %,.2f TL tutarındaki faturasını silmek istediğinizden emin misiniz?",
            hastaAdi, masraf
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
                String odemeGecmisiSilSorgu = "DELETE FROM odeme_gecmisi WHERE fatura_id = ?";
                PreparedStatement odemeStmt = baglanti.prepareStatement(odemeGecmisiSilSorgu);
                odemeStmt.setInt(1, faturaId);
                odemeStmt.executeUpdate();

                String faturaSilSorgu = "DELETE FROM faturalar WHERE id = ?";
                PreparedStatement faturaStmt = baglanti.prepareStatement(faturaSilSorgu);
                faturaStmt.setInt(1, faturaId);
                faturaStmt.executeUpdate();

                JOptionPane.showMessageDialog(onayDialog, "Fatura başarıyla silindi.");
                faturalariYukle();
                onayDialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(onayDialog, "Hata: " + ex.getMessage());
            }
        });

        butonPanel.add(iptalButon);
        butonPanel.add(silButon);

        onayDialog.add(mesajPanel, BorderLayout.CENTER);
        onayDialog.add(butonPanel, BorderLayout.SOUTH);

        onayDialog.pack();
        onayDialog.setLocationRelativeTo(panel);
        onayDialog.setVisible(true);
    }

    public JTable getFaturaTablosu() {
        return faturaTablosu;
    }

    private void sevkleriYukle() {
        try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
            String sorgu = """
                SELECT s.*, h.ad, h.soyad
                FROM sevkler s 
                JOIN hastalar h ON s.hasta_id = h.id
                ORDER BY s.tarih DESC
                """;
            
            ResultSet sonuc = baglanti.createStatement().executeQuery(sorgu);
            sevkModel = new DefaultTableModel(
                new String[]{"ID", "Hasta", "Sevk Nedeni", "Hedef Hastane", "Tarih"}, 
                0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            sevkTablosu.setModel(sevkModel);
            sevkler.clear();

            while (sonuc.next()) {
                Sevk sevk = new Sevk(
                    sonuc.getInt("id"),
                    sonuc.getInt("hasta_id"),
                    sonuc.getString("sevk_nedeni"),
                    sonuc.getString("hedef_hastane"),
                    sonuc.getDate("tarih")
                );
                sevkler.add(sevk);

                sevkModel.addRow(new Object[]{
                    sevk.getId(),
                    sonuc.getString("ad") + " " + sonuc.getString("soyad"),
                    sevk.getSevkNedeni(),
                    sevk.getHedefHastane(),
                    sevk.getTarih()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel, 
                "Sevkler yüklenirken hata: " + e.getMessage(),
                "Hata",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sevkOlustur() {
        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(panel), 
            "Yeni Sevk", true);
        dialog.setLayout(new GridLayout(5, 2, 10, 10));
        dialog.setSize(400, 300);

        JComboBox<String> hastaCombo = new JComboBox<>();
        JTextField sevkNedeniAlani = new JTextField();
        JTextField hedefHastaneAlani = new JTextField();

        try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
            ResultSet hastalar = baglanti.createStatement().executeQuery(
                "SELECT id, ad, soyad FROM hastalar ORDER BY ad, soyad");
            while (hastalar.next()) {
                hastaCombo.addItem(hastalar.getInt("id") + " - " + 
                    hastalar.getString("ad") + " " + hastalar.getString("soyad"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(dialog, "Veri yüklenirken hata: " + e.getMessage());
            return;
        }

        dialog.add(new JLabel("Hasta:"));
        dialog.add(hastaCombo);
        dialog.add(new JLabel("Sevk Nedeni:"));
        dialog.add(sevkNedeniAlani);
        dialog.add(new JLabel("Hedef Hastane:"));
        dialog.add(hedefHastaneAlani);

        JButton kaydetButon = UIStyle.createStyledButton("Kaydet");
        kaydetButon.addActionListener(e -> {
            try {
                String secilenHasta = (String) hastaCombo.getSelectedItem();
                int hastaId = Integer.parseInt(secilenHasta.split(" - ")[0]);

                String sorgu = """
                    INSERT INTO sevkler 
                    (hasta_id, sevk_nedeni, hedef_hastane, tarih)
                    VALUES (?, ?, ?, CURRENT_DATE)
                    """;
                
                try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
                    PreparedStatement stmt = baglanti.prepareStatement(sorgu);
                    stmt.setInt(1, hastaId);
                    stmt.setString(2, sevkNedeniAlani.getText().trim());
                    stmt.setString(3, hedefHastaneAlani.getText().trim());
                    stmt.executeUpdate();

                    JOptionPane.showMessageDialog(dialog, "Sevk başarıyla oluşturuldu.");
                    dialog.dispose();
                    sevkleriYukle();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Sevk oluşturulurken hata: " + ex.getMessage(),
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

        dialog.setLocationRelativeTo(panel);
        dialog.setVisible(true);
    }

    private void sevkDuzenle() {
        int seciliSatir = sevkTablosu.getSelectedRow();
        if (seciliSatir == -1) {
            JOptionPane.showMessageDialog(panel, "Lütfen düzenlenecek sevki seçin.");
            return;
        }

        Sevk sevk = sevkler.get(seciliSatir);
        
        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(panel), 
            "Sevk Düzenle", true);
        dialog.setLayout(new GridLayout(4, 2, 10, 10));
        dialog.setSize(400, 200);

        JTextField sevkNedeniAlani = new JTextField(sevk.getSevkNedeni());
        JTextField hedefHastaneAlani = new JTextField(sevk.getHedefHastane());
        JTextField tarihAlani = new JTextField(sevk.getTarih().toString());

        dialog.add(new JLabel("Sevk Nedeni:"));
        dialog.add(sevkNedeniAlani);
        dialog.add(new JLabel("Hedef Hastane:"));
        dialog.add(hedefHastaneAlani);
        dialog.add(new JLabel("Tarih (YYYY-MM-DD):"));
        dialog.add(tarihAlani);

        JButton kaydetButon = UIStyle.createStyledButton("Kaydet");
        kaydetButon.addActionListener(e -> {
            try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
                String sorgu = """
                    UPDATE sevkler 
                    SET sevk_nedeni = ?, hedef_hastane = ?, tarih = ? 
                    WHERE id = ?
                    """;
                PreparedStatement stmt = baglanti.prepareStatement(sorgu);
                stmt.setString(1, sevkNedeniAlani.getText().trim());
                stmt.setString(2, hedefHastaneAlani.getText().trim());
                stmt.setDate(3, java.sql.Date.valueOf(tarihAlani.getText().trim()));
                stmt.setInt(4, sevk.getId());
                
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(dialog, "Sevk başarıyla güncellendi.");
                sevkleriYukle();
                dialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Sevk güncellenirken hata: " + ex.getMessage(),
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

        dialog.setLocationRelativeTo(panel);
        dialog.setVisible(true);
    }

    private void sevkSil() {
        int seciliSatir = sevkTablosu.getSelectedRow();
        if (seciliSatir == -1) {
            JOptionPane.showMessageDialog(panel, "Lütfen silinecek sevki seçin.");
            return;
        }

        Sevk sevk = sevkler.get(seciliSatir);
        String hastaAdi = (String) sevkTablosu.getValueAt(seciliSatir, 1);

        JDialog onayDialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(panel), 
            "Sevk Sil", true);
        onayDialog.setLayout(new BorderLayout(10, 10));

        JPanel mesajPanel = new JPanel();
        UIStyle.stylePanel(mesajPanel);
        JLabel mesajLabel = new JLabel(String.format(
            "%s adlı hastanın %s tarihli sevkini silmek istediğinizden emin misiniz?",
            hastaAdi, sevk.getTarih().toString()
        ));
        UIStyle.styleLabel(mesajLabel);
        mesajPanel.add(mesajLabel);

        JPanel butonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton iptalButon = UIStyle.createStyledButton("İptal");
        JButton silButon = UIStyle.createStyledButton("Sil");

        iptalButon.addActionListener(e -> onayDialog.dispose());
        silButon.addActionListener(e -> {
            try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
                String sorgu = "DELETE FROM sevkler WHERE id = ?";
                PreparedStatement stmt = baglanti.prepareStatement(sorgu);
                stmt.setInt(1, sevk.getId());
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(onayDialog, "Sevk başarıyla silindi.");
                sevkleriYukle();
                onayDialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(onayDialog, 
                    "Sevk silinirken hata: " + ex.getMessage(),
                    "Hata",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        butonPanel.add(iptalButon);
        butonPanel.add(silButon);

        onayDialog.add(mesajPanel, BorderLayout.CENTER);
        onayDialog.add(butonPanel, BorderLayout.SOUTH);

        onayDialog.pack();
        onayDialog.setLocationRelativeTo(panel);
        onayDialog.setVisible(true);
    }
}


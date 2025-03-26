import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import models.Hasta;
import models.Doktor;
import models.LabSonuc;

public class LaboratuvarSonucModulu {
    private final JFrame pencere;
    private final Hasta hasta;
    private final Doktor doktor;
    private JTable sonucTablosu;
    private DefaultTableModel sonucModel;

    public LaboratuvarSonucModulu(Hasta hasta, Doktor doktor) {
        this.hasta = hasta;
        this.doktor = doktor;
        this.pencere = new JFrame("Laboratuvar Sonuçları");
        UIStyle.styleFrame(pencere);
        pencere.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pencere.setSize(800, 600);
        pencere.setLayout(new BorderLayout(10, 10));

        JPanel sonucPanel = new JPanel(new BorderLayout(10, 10));
        UIStyle.stylePanel(sonucPanel);
        
        String[] sutunlar = {"ID", "Test Türü", "Sonuç", "Tarih", "Durum"};
        sonucModel = new DefaultTableModel(sutunlar, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        sonucTablosu = new JTable(sonucModel);
        UIStyle.styleTable(sonucTablosu);
        sonucPanel.add(new JScrollPane(sonucTablosu), BorderLayout.CENTER);

        JPanel butonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        UIStyle.stylePanel(butonPanel);
        
        JButton sonucEkleButonu = UIStyle.createStyledButton("Yeni Sonuç Ekle");
        JButton sonucDuzenleButonu = UIStyle.createStyledButton("Sonuç Düzenle");
        JButton sonucSilButonu = UIStyle.createStyledButton("Sonuç Sil");
        JButton kapatButonu = UIStyle.createStyledButton("Kapat");

        butonPanel.add(sonucEkleButonu);
        butonPanel.add(sonucDuzenleButonu);
        butonPanel.add(sonucSilButonu);
        butonPanel.add(kapatButonu);

        sonucEkleButonu.addActionListener(e -> sonucEkle());
        sonucDuzenleButonu.addActionListener(e -> sonucDuzenle());
        sonucSilButonu.addActionListener(e -> sonucSil());
        kapatButonu.addActionListener(e -> pencere.dispose());

        pencere.add(sonucPanel, BorderLayout.CENTER);
        pencere.add(butonPanel, BorderLayout.SOUTH);

        sonuclariYukle();
        
        pencere.setLocationRelativeTo(null);
        pencere.setVisible(true);
    }

    private void sonuclariYukle() {
        try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
            String sorgu = """
                SELECT * FROM lab_sonuclari 
                WHERE hasta_id = ? AND doktor_id = ?
                ORDER BY tarih DESC
                """;
            
            PreparedStatement stmt = baglanti.prepareStatement(sorgu);
            stmt.setInt(1, hasta.getId());
            stmt.setInt(2, doktor.getId());
            ResultSet sonuc = stmt.executeQuery();

            sonucModel.setRowCount(0);
            while (sonuc.next()) {
                sonucModel.addRow(new Object[]{
                    sonuc.getInt("id"),
                    sonuc.getString("test_turu"),
                    sonuc.getString("sonuc"),
                    sonuc.getDate("tarih"),
                    sonuc.getString("durum")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(pencere, 
                "Sonuçlar yüklenirken hata: " + e.getMessage(),
                "Hata",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sonucEkle() {
        JDialog dialog = new JDialog(pencere, "Yeni Sonuç Ekle", true);
        dialog.setLayout(new GridLayout(4, 2, 10, 10));
        dialog.setSize(400, 250);

        JTextField testTuruAlani = new JTextField();
        JTextField sonucAlani = new JTextField();
        JTextField tarihAlani = new JTextField();

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
                    INSERT INTO lab_sonuclari 
                    (hasta_id, doktor_id, test_turu, sonuc, tarih, durum)
                    VALUES (?, ?, ?, ?, ?, 'Yeni')
                    """;
                
                PreparedStatement stmt = baglanti.prepareStatement(sorgu);
                stmt.setInt(1, hasta.getId());
                stmt.setInt(2, doktor.getId());
                stmt.setString(3, testTuruAlani.getText().trim());
                stmt.setString(4, sonucAlani.getText().trim());
                stmt.setDate(5, java.sql.Date.valueOf(tarihAlani.getText().trim()));
                
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(dialog, "Sonuç başarıyla eklendi.");
                dialog.dispose();
                sonuclariYukle();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Sonuç eklenirken hata: " + ex.getMessage(),
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

    private void sonucDuzenle() {
        int secilenSatir = sonucTablosu.getSelectedRow();
        if (secilenSatir == -1) {
            JOptionPane.showMessageDialog(pencere, "Lütfen düzenlenecek sonucu seçin.");
            return;
        }

        int sonucId = (int) sonucTablosu.getValueAt(secilenSatir, 0);
        String mevcutTest = sonucTablosu.getValueAt(secilenSatir, 1).toString();
        String mevcutSonuc = sonucTablosu.getValueAt(secilenSatir, 2).toString();
        Date mevcutTarih = (Date) sonucTablosu.getValueAt(secilenSatir, 3);

        JDialog dialog = new JDialog(pencere, "Sonuç Düzenle", true);
        dialog.setLayout(new GridLayout(4, 2, 10, 10));
        dialog.setSize(400, 250);

        JTextField testTuruAlani = new JTextField(mevcutTest);
        JTextField sonucAlani = new JTextField(mevcutSonuc);
        JTextField tarihAlani = new JTextField(mevcutTarih.toString());

        dialog.add(new JLabel("Test Türü:"));
        dialog.add(testTuruAlani);
        dialog.add(new JLabel("Sonuç:"));
        dialog.add(sonucAlani);
        dialog.add(new JLabel("Tarih (YYYY-MM-DD):"));
        dialog.add(tarihAlani);

        JButton kaydetButon = UIStyle.createStyledButton("Güncelle");
        kaydetButon.addActionListener(e -> {
            try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
                String sorgu = """
                    UPDATE lab_sonuclari 
                    SET test_turu = ?, sonuc = ?, tarih = ? 
                    WHERE id = ? AND hasta_id = ? AND doktor_id = ?
                    """;
                
                PreparedStatement stmt = baglanti.prepareStatement(sorgu);
                stmt.setString(1, testTuruAlani.getText().trim());
                stmt.setString(2, sonucAlani.getText().trim());
                stmt.setDate(3, java.sql.Date.valueOf(tarihAlani.getText().trim()));
                stmt.setInt(4, sonucId);
                stmt.setInt(5, hasta.getId());
                stmt.setInt(6, doktor.getId());
                
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(dialog, "Sonuç başarıyla güncellendi.");
                dialog.dispose();
                sonuclariYukle();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Sonuç güncellenirken hata: " + ex.getMessage(),
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

    private void sonucSil() {
        int secilenSatir = sonucTablosu.getSelectedRow();
        if (secilenSatir == -1) {
            JOptionPane.showMessageDialog(pencere, "Lütfen silinecek sonucu seçin.");
            return;
        }

        int sonucId = (int) sonucTablosu.getValueAt(secilenSatir, 0);
        String testTuru = sonucTablosu.getValueAt(secilenSatir, 1).toString();

        int secim = JOptionPane.showConfirmDialog(
            pencere,
            testTuru + " test sonucunu silmek istediğinizden emin misiniz?",
            "Sonuç Sil",
            JOptionPane.YES_NO_OPTION
        );

        if (secim == JOptionPane.YES_OPTION) {
            try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
                String sorgu = "DELETE FROM lab_sonuclari WHERE id = ? AND hasta_id = ? AND doktor_id = ?";
                PreparedStatement stmt = baglanti.prepareStatement(sorgu);
                stmt.setInt(1, sonucId);
                stmt.setInt(2, hasta.getId());
                stmt.setInt(3, doktor.getId());
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(pencere, "Sonuç başarıyla silindi.");
                sonuclariYukle();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(pencere, 
                    "Sonuç silinirken hata: " + e.getMessage(),
                    "Hata",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

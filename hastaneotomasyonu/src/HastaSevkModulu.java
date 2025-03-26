import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import models.Hasta;
import models.Sevk;
import models.Doktor;

public class HastaSevkModulu {
    private final JFrame pencere;
    private final Hasta hasta;
    private final Doktor doktor;
    private JTable sevkTablosu;
    private DefaultTableModel sevkModel;

    public HastaSevkModulu(Hasta hasta, Doktor doktor) {
        this.hasta = hasta;
        this.doktor = doktor;
        this.pencere = new JFrame("Hasta Sevk İşlemleri");
        UIStyle.styleFrame(pencere);
        pencere.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pencere.setSize(800, 600);
        pencere.setLayout(new BorderLayout(10, 10));

        JPanel sevkPanel = new JPanel(new BorderLayout(10, 10));
        UIStyle.stylePanel(sevkPanel);
        
        String[] sutunlar = {"ID", "Sevk Nedeni", "Hedef Hastane", "Tarih", "Durum"};
        sevkModel = new DefaultTableModel(sutunlar, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        sevkTablosu = new JTable(sevkModel);
        UIStyle.styleTable(sevkTablosu);
        sevkPanel.add(new JScrollPane(sevkTablosu), BorderLayout.CENTER);

        JPanel butonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        UIStyle.stylePanel(butonPanel);
        
        JButton sevkOlusturButonu = UIStyle.createStyledButton("Yeni Sevk Oluştur");
        JButton sevkDuzenleButonu = UIStyle.createStyledButton("Sevk Düzenle");
        JButton sevkSilButonu = UIStyle.createStyledButton("Sevk Sil");
        JButton kapatButonu = UIStyle.createStyledButton("Kapat");

        butonPanel.add(sevkOlusturButonu);
        butonPanel.add(sevkDuzenleButonu);
        butonPanel.add(sevkSilButonu);
        butonPanel.add(kapatButonu);

        sevkOlusturButonu.addActionListener(e -> sevkOlustur());
        sevkDuzenleButonu.addActionListener(e -> sevkDuzenle());
        sevkSilButonu.addActionListener(e -> sevkSil());
        kapatButonu.addActionListener(e -> pencere.dispose());

        pencere.add(sevkPanel, BorderLayout.CENTER);
        pencere.add(butonPanel, BorderLayout.SOUTH);

        sevkleriYukle();
        
        pencere.setLocationRelativeTo(null);
        pencere.setVisible(true);
    }

    private void sevkleriYukle() {
        try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
            String sorgu = """
                SELECT * FROM sevkler 
                WHERE hasta_id = ? 
                ORDER BY tarih DESC
                """;
            
            PreparedStatement stmt = baglanti.prepareStatement(sorgu);
            stmt.setInt(1, hasta.getId());
            ResultSet sonuc = stmt.executeQuery();

            sevkModel.setRowCount(0);
            while (sonuc.next()) {
                sevkModel.addRow(new Object[]{
                    sonuc.getInt("id"),
                    sonuc.getString("sevk_nedeni"),
                    sonuc.getString("hedef_hastane"),
                    sonuc.getDate("tarih"),
                    sonuc.getString("durum")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(pencere, 
                "Sevkler yüklenirken hata: " + e.getMessage(),
                "Hata",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sevkOlustur() {
        JDialog dialog = new JDialog(pencere, "Yeni Sevk Oluştur", true);
        dialog.setLayout(new GridLayout(4, 2, 10, 10));
        dialog.setSize(400, 250);

        JTextField sevkNedeniAlani = new JTextField();
        JTextField hedefHastaneAlani = new JTextField();
        JTextField tarihAlani = new JTextField();

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
                    INSERT INTO sevkler (hasta_id, doktor_id, sevk_nedeni, hedef_hastane, tarih, durum)
                    VALUES (?, ?, ?, ?, ?, 'Beklemede')
                    """;
                
                PreparedStatement stmt = baglanti.prepareStatement(sorgu);
                stmt.setInt(1, hasta.getId());
                stmt.setInt(2, doktor.getId());
                stmt.setString(3, sevkNedeniAlani.getText().trim());
                stmt.setString(4, hedefHastaneAlani.getText().trim());
                stmt.setDate(5, java.sql.Date.valueOf(tarihAlani.getText().trim()));
                
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(dialog, "Sevk başarıyla oluşturuldu.");
                dialog.dispose();
                sevkleriYukle();
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

        dialog.setLocationRelativeTo(pencere);
        dialog.setVisible(true);
    }

    private void sevkDuzenle() {
        int secilenSatir = sevkTablosu.getSelectedRow();
        if (secilenSatir == -1) {
            JOptionPane.showMessageDialog(pencere, "Lütfen düzenlenecek sevki seçin.");
            return;
        }

        int sevkId = (int) sevkTablosu.getValueAt(secilenSatir, 0);
        String mevcutNeden = sevkTablosu.getValueAt(secilenSatir, 1).toString();
        String mevcutHastane = sevkTablosu.getValueAt(secilenSatir, 2).toString();
        Date mevcutTarih = (Date) sevkTablosu.getValueAt(secilenSatir, 3);

        JDialog dialog = new JDialog(pencere, "Sevk Düzenle", true);
        dialog.setLayout(new GridLayout(4, 2, 10, 10));
        dialog.setSize(400, 250);

        JTextField sevkNedeniAlani = new JTextField(mevcutNeden);
        JTextField hedefHastaneAlani = new JTextField(mevcutHastane);
        JTextField tarihAlani = new JTextField(mevcutTarih.toString());

        dialog.add(new JLabel("Sevk Nedeni:"));
        dialog.add(sevkNedeniAlani);
        dialog.add(new JLabel("Hedef Hastane:"));
        dialog.add(hedefHastaneAlani);
        dialog.add(new JLabel("Tarih (YYYY-MM-DD):"));
        dialog.add(tarihAlani);

        JButton kaydetButon = UIStyle.createStyledButton("Güncelle");
        kaydetButon.addActionListener(e -> {
            try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
                String sorgu = """
                    UPDATE sevkler 
                    SET sevk_nedeni = ?, hedef_hastane = ?, tarih = ? 
                    WHERE id = ? AND hasta_id = ?
                    """;
                
                PreparedStatement stmt = baglanti.prepareStatement(sorgu);
                stmt.setString(1, sevkNedeniAlani.getText().trim());
                stmt.setString(2, hedefHastaneAlani.getText().trim());
                stmt.setDate(3, java.sql.Date.valueOf(tarihAlani.getText().trim()));
                stmt.setInt(4, sevkId);
                stmt.setInt(5, hasta.getId());
                
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(dialog, "Sevk başarıyla güncellendi.");
                dialog.dispose();
                sevkleriYukle();
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

        dialog.setLocationRelativeTo(pencere);
        dialog.setVisible(true);
    }

    private void sevkSil() {
        int secilenSatir = sevkTablosu.getSelectedRow();
        if (secilenSatir == -1) {
            JOptionPane.showMessageDialog(pencere, "Lütfen silinecek sevki seçin.");
            return;
        }

        int sevkId = (int) sevkTablosu.getValueAt(secilenSatir, 0);
        String sevkNedeni = sevkTablosu.getValueAt(secilenSatir, 1).toString();

        int secim = JOptionPane.showConfirmDialog(
            pencere,
            sevkNedeni + " nedenli sevki silmek istediğinizden emin misiniz?",
            "Sevk Sil",
            JOptionPane.YES_NO_OPTION
        );

        if (secim == JOptionPane.YES_OPTION) {
            try (Connection baglanti = VeritabaniBaglantisi.baglan()) {
                String sorgu = "DELETE FROM sevkler WHERE id = ? AND hasta_id = ?";
                PreparedStatement stmt = baglanti.prepareStatement(sorgu);
                stmt.setInt(1, sevkId);
                stmt.setInt(2, hasta.getId());
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(pencere, "Sevk başarıyla silindi.");
                sevkleriYukle();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(pencere, 
                    "Sevk silinirken hata: " + e.getMessage(),
                    "Hata",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

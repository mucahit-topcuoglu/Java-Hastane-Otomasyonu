import javax.swing.*;
import java.awt.*;

public class UIStyle {
    public static final Color BACKGROUND_COLOR = new Color(240, 240, 245);
    public static final Color PANEL_COLOR = new Color(245, 245, 250);
    public static final Color BUTTON_COLOR = new Color(70, 130, 180);
    public static final Color TAB_COLOR = new Color(230, 230, 235);
    
    public static final Font MAIN_FONT = new Font("Arial", Font.PLAIN, 12);
    
    public static void styleFrame(JFrame frame) {
        frame.setSize(900, 650);
        frame.getContentPane().setBackground(BACKGROUND_COLOR);
        frame.setLocationRelativeTo(null);
    }
    
    public static void stylePanel(JPanel panel) {
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
    
    public static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(BUTTON_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(MAIN_FONT);
        return button;
    }
    
    public static void styleTable(JTable table) {
        table.setFont(MAIN_FONT);
        table.setGridColor(new Color(200, 200, 200));
        table.setSelectionBackground(BUTTON_COLOR);
        table.setSelectionForeground(Color.WHITE);
        table.setRowHeight(25);
        table.getTableHeader().setFont(MAIN_FONT);
    }
    
    public static void styleTabbedPane(JTabbedPane tabbedPane) {
        tabbedPane.setBackground(TAB_COLOR);
        tabbedPane.setFont(MAIN_FONT);
    }
    
    public static void styleDialog(JDialog dialog) {
        dialog.getContentPane().setBackground(PANEL_COLOR);
        for (Component comp : dialog.getContentPane().getComponents()) {
            if (comp instanceof JPanel) {
                ((JPanel) comp).setBackground(PANEL_COLOR);
            }
        }
    }
    
    public static void styleLoginFrame(JFrame frame) {
        frame.setSize(400, 250);
        frame.getContentPane().setBackground(BACKGROUND_COLOR);
        frame.setLocationRelativeTo(null);
    }
    
    public static void styleTextField(JTextField textField) {
        textField.setFont(MAIN_FONT);
        textField.setBorder(BorderFactory.createCompoundBorder(
            textField.getBorder(), 
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }
    
    public static void styleLabel(JLabel label) {
        label.setFont(MAIN_FONT);
        label.setForeground(new Color(50, 50, 50));
    }
    
    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(MAIN_FONT);
        comboBox.setBackground(Color.WHITE);
    }
} 
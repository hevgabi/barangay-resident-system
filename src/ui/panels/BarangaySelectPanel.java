package ui.panels;

import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ui.frames.AppFrames;

public class BarangaySelectPanel extends JPanel {

    public BarangaySelectPanel(AppFrames frame) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        add(new JLabel("Select Barangay"));

        JComboBox<String> combo = new JComboBox<>(new String[] {
            "Brgy_SanRoque", "Brgy_Poblacion"
        });
        add(combo);

        JTextField txtNew = new JTextField(15);
        add(txtNew);

        JButton btn = new JButton("Continue");
        btn.addActionListener(e -> frame.goToLogin());
        add(btn);
    }
}

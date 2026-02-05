package ui.panels.dashboard.resident.account;

import ui.mock.DummyStore;
import ui.panels.dashboard.resident.ResidentDashboardPanel;

import javax.swing.*;
import java.awt.*;

public class ChangePasswordPanel extends JPanel {

    private final JPasswordField txtOld = new JPasswordField();
    private final JPasswordField txtNew = new JPasswordField();
    private final JPasswordField txtConfirm = new JPasswordField();

    public ChangePasswordPanel(ResidentDashboardPanel dash) {
        setOpaque(false);
        setLayout(new BorderLayout(10,10));

        add(topBar("Change Password", () -> dash.showCard(ResidentDashboardPanel.CARD_ACCOUNT_MENU)), BorderLayout.NORTH);

        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8,8,8,8);
        c.fill = GridBagConstraints.HORIZONTAL;

        int y=0;
        y = row(p,c,y,"Old Password", txtOld);
        y = row(p,c,y,"New Password", txtNew);
        y = row(p,c,y,"Confirm New Password", txtConfirm);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);

        JButton btnSave = new JButton("Save");
        btnSave.setFocusPainted(false);
        btnSave.addActionListener(e -> save());

        actions.add(btnSave);

        add(p, BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);
    }

    private int row(JPanel p, GridBagConstraints c, int y, String label, JComponent field) {
        c.gridx=0; c.gridy=y; c.weightx=0; p.add(new JLabel(label), c);
        c.gridx=1; c.gridy=y; c.weightx=1; p.add(field, c);
        return y+1;
    }

    private void save() {
        String oldP = new String(txtOld.getPassword());
        String newP = new String(txtNew.getPassword());
        String conP = new String(txtConfirm.getPassword());

        if (oldP.isEmpty() || newP.isEmpty() || conP.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill all fields.");
            return;
        }
        if (!newP.equals(conP)) {
            JOptionPane.showMessageDialog(this, "New password mismatch.");
            return;
        }

        DummyStore.addLoginHistory("res01", "PASSWORD_CHANGED");
        JOptionPane.showMessageDialog(this, "Password updated (dummy).");

        txtOld.setText(""); txtNew.setText(""); txtConfirm.setText("");
    }

    private JPanel topBar(String title, Runnable onBack) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JButton back = new JButton("Back");
        back.setFocusPainted(false);
        back.addActionListener(e -> onBack.run());

        JLabel h = new JLabel(title, SwingConstants.CENTER);
        h.setFont(new Font("Segoe UI", Font.BOLD, 18));

        p.add(back, BorderLayout.WEST);
        p.add(h, BorderLayout.CENTER);
        return p;
    }
}

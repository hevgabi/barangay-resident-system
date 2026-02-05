package ui.panels.dashboard.staff.account;

import ui.panels.dashboard.staff.StaffDashboardPanel;

import javax.swing.*;
import java.awt.*;

public class ChangePasswordPanel extends JPanel {

    private final JPasswordField txtOld = new JPasswordField();
    private final JPasswordField txtNew = new JPasswordField();
    private final JPasswordField txtConfirm = new JPasswordField();

    public ChangePasswordPanel(StaffDashboardPanel dash) {
        setOpaque(false);
        setLayout(new BorderLayout(10,10));

        add(topBar("Change Password", () -> dash.showCard(StaffDashboardPanel.CARD_SETTINGS_MENU)),
                BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.fill = GridBagConstraints.HORIZONTAL;

        int y = 0;
        y = row(form, c, y, "Old Password", txtOld);
        y = row(form, c, y, "New Password", txtNew);
        y = row(form, c, y, "Confirm New Password", txtConfirm);

        // push up like other panels
        c.gridx = 0;
        c.gridy = y;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 2;
        form.add(Box.createVerticalGlue(), c);
        c.weighty = 0;
        c.gridwidth = 1;

        add(form, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);

        JButton btnClear = new JButton("Clear");
        btnClear.setFocusPainted(false);
        btnClear.addActionListener(e -> clear());

        JButton btnSave = new JButton("Save");
        btnSave.setFocusPainted(false);
        btnSave.addActionListener(e -> save());

        actions.add(btnClear);
        actions.add(btnSave);

        add(actions, BorderLayout.SOUTH);
    }

    private int row(JPanel p, GridBagConstraints c, int y, String label, JComponent field) {
        c.gridx = 0;
        c.gridy = y;
        c.weightx = 0;
        c.anchor = GridBagConstraints.WEST;
        p.add(new JLabel(label), c);

        c.gridx = 1;
        c.gridy = y;
        c.weightx = 1;

        // match style: let layout decide width, set only height
        field.setPreferredSize(new Dimension(0, 28));
        p.add(field, c);

        return y + 1;
    }

    private void save() {
        String oldP = new String(txtOld.getPassword()).trim();
        String newP = new String(txtNew.getPassword()).trim();
        String conP = new String(txtConfirm.getPassword()).trim();

        if (oldP.isEmpty() || newP.isEmpty() || conP.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill all fields.");
            return;
        }

        if (newP.length() < 6) {
            JOptionPane.showMessageDialog(this, "New password must be at least 6 characters.");
            return;
        }

        if (newP.equals(oldP)) {
            JOptionPane.showMessageDialog(this, "New password must be different from old password.");
            return;
        }

        if (!newP.equals(conP)) {
            JOptionPane.showMessageDialog(this, "New password mismatch.");
            return;
        }

        // ✅ UI-only placeholder for now (backend later)
        JOptionPane.showMessageDialog(this, "Password update is UI-only for now.");

        clear();
    }

    private void clear() {
        txtOld.setText("");
        txtNew.setText("");
        txtConfirm.setText("");
        txtOld.requestFocusInWindow();
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

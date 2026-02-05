package ui.panels.dashboard.staff.usermanagement;

import ui.mock.DummyStore;
import ui.panels.dashboard.staff.StaffDashboardPanel;

import javax.swing.*;
import java.awt.*;

public class RemoveUserPanel extends JPanel {

    private final JComboBox<String> cboUsers = new JComboBox<>();
    private final JLabel lblInfo = new JLabel(" ");

    public RemoveUserPanel(StaffDashboardPanel dash) {
        setOpaque(false);
        setLayout(new BorderLayout(10,10));

        add(topBar("Remove User", () -> dash.showCard(StaffDashboardPanel.CARD_USERS_MENU)), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8,8,8,8);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0;
        form.add(new JLabel("Select Username:"), c);

        c.gridx = 1; c.gridy = 0; c.weightx = 1;
        form.add(cboUsers, c);

        JButton reload = new JButton("Reload");
        reload.setFocusPainted(false);
        reload.addActionListener(e -> loadUsers());

        c.gridx = 2; c.gridy = 0; c.weightx = 0;
        form.add(reload, c);

        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        c.gridx = 0; c.gridy = 1; c.gridwidth = 3;
        form.add(lblInfo, c);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);

        JButton btnRemove = new JButton("Remove");
        btnRemove.setFocusPainted(false);
        btnRemove.addActionListener(e -> removeSelected());

        actions.add(btnRemove);

        add(form, BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);

        loadUsers();
    }

    private void loadUsers() {
        cboUsers.removeAllItems();
        for (DummyStore.UserRow u : DummyStore.getUsers()) {
            cboUsers.addItem(u.username);
        }
        if (cboUsers.getItemCount() == 0) {
            lblInfo.setText("No users available.");
        } else {
            lblInfo.setText("Select a user and click Remove.");
        }
    }

    private void removeSelected() {
        String username = (String) cboUsers.getSelectedItem();
        if (username == null || username.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a username.");
            return;
        }

        int ok = JOptionPane.showConfirmDialog(this,
                "Remove user: " + username + " ?",
                "Confirm",
                JOptionPane.YES_NO_OPTION);

        if (ok != JOptionPane.YES_OPTION) return;

        boolean removed = DummyStore.removeUser(username);
        if (removed) {
            DummyStore.addLoginHistory(username, "REMOVED"); // visible sa history screen
            JOptionPane.showMessageDialog(this, "User removed.");
            loadUsers();
        } else {
            JOptionPane.showMessageDialog(this, "User not found.");
        }
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

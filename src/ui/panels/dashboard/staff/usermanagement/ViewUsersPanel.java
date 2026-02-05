package ui.panels.dashboard.staff.usermanagement;

import ui.frames.AppFrames;
import ui.panels.dashboard.staff.StaffDashboardPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ViewUsersPanel extends JPanel {

    private final AppFrames frame;
    private final JTable table = new JTable();

    public ViewUsersPanel(AppFrames frame, StaffDashboardPanel dash) {
        this.frame = frame;
        setOpaque(false);
        setLayout(new BorderLayout(10, 10));

        add(topBar("View Users", () -> dash.showCard(StaffDashboardPanel.CARD_USERS_MENU)), BorderLayout.NORTH);

        table.setRowHeight(24);
        refresh();

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);

        JButton refresh = new JButton("Refresh");
        refresh.setFocusPainted(false);
        refresh.addActionListener(e -> refresh());

        bottom.add(refresh);
        add(bottom, BorderLayout.SOUTH);
    }

    private void refresh() {
        DefaultTableModel model = new DefaultTableModel(
                new Object[] { "Username", "Role", "Email", "Full Name", "Resident ID", "Status" },
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // ✅ NOW this is List<User>, not Object
        java.util.List<models.User> users = frame.getAuthService().getAllUsers();

        for (models.User u : users) {
            model.addRow(new Object[] {
                    u.getUsername(),
                    u.getRole(),
                    u.getEmail(),
                    u.getFullName(),
                    u.getResidentId(),
                    u.isActive() ? "ACTIVE" : "DISABLED"
            });
        }

        table.setModel(model);
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

package ui.panels.dashboard.staff.usermanagement;

import ui.panels.dashboard.staff.StaffDashboardPanel;

import javax.swing.*;
import java.awt.*;

public class UserManagementMenuPanel extends JPanel {

    private final StaffDashboardPanel dash;

    public UserManagementMenuPanel(StaffDashboardPanel dash) {
        this.dash = dash;

        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(header("User Management"));
        add(Box.createVerticalStrut(14));

        add(menuBtn("View Users", StaffDashboardPanel.CARD_VIEW_USERS));
        add(menuBtn("Remove User", StaffDashboardPanel.CARD_REMOVE_USER));

        add(Box.createVerticalStrut(14));
        add(backBtn());
    }

    private JComponent header(String text) {
        JLabel h = new JLabel(text);
        h.setFont(new Font("Segoe UI", Font.BOLD, 18));
        h.setAlignmentX(Component.CENTER_ALIGNMENT);
        return h;
    }

    private JButton menuBtn(String text, String cardKey) {
        JButton b = new JButton(text);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setMaximumSize(new Dimension(260, 38));
        b.setFocusPainted(false);
        b.addActionListener(e -> dash.showCard(cardKey));
        return b;
    }

    private JButton backBtn() {
        JButton b = new JButton("Back");
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setMaximumSize(new Dimension(120, 38));
        b.setFocusPainted(false);
        b.addActionListener(e -> dash.showCard(StaffDashboardPanel.CARD_HOME));
        return b;
    }
}

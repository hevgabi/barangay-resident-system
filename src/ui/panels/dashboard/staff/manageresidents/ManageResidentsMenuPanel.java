package ui.panels.dashboard.staff.manageresidents;

import ui.panels.dashboard.staff.StaffDashboardPanel;

import javax.swing.*;
import java.awt.*;

public class ManageResidentsMenuPanel extends JPanel {

    private final StaffDashboardPanel dash;

    public ManageResidentsMenuPanel(StaffDashboardPanel dash) {
        this.dash = dash;

        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(header("Manage Residents"));
        add(Box.createVerticalStrut(14));

        add(menuBtn("Add Resident", StaffDashboardPanel.CARD_ADD_RESIDENT));
        add(menuBtn("View All Residents", StaffDashboardPanel.CARD_VIEW_RESIDENTS));
        add(menuBtn("Search Resident", StaffDashboardPanel.CARD_SEARCH_RESIDENT));
        add(menuBtn("Update Resident", StaffDashboardPanel.CARD_UPDATE_RESIDENT));
        add(menuBtn("Delete Resident", StaffDashboardPanel.CARD_DELETE_RESIDENT));

        add(Box.createVerticalStrut(14));
        add(backBtn());
    }

    private JComponent header(String text) {
        JLabel h = new JLabel(text);
        h.setFont(new Font("Segoe UI", Font.BOLD, 18));
        h.setAlignmentX(Component.CENTER_ALIGNMENT);
        return h;
    }

    private JButton menuBtn(String text, String key) {
        JButton b = new JButton(text);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setMaximumSize(new Dimension(260, 38));
        b.setFocusPainted(false);
        b.addActionListener(e -> dash.showCard(key));
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

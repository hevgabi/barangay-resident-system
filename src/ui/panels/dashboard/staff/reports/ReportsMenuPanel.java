package ui.panels.dashboard.staff.reports;

import ui.panels.dashboard.staff.StaffDashboardPanel;

import javax.swing.*;
import java.awt.*;

public class ReportsMenuPanel extends JPanel {

    private final StaffDashboardPanel dash;

    public ReportsMenuPanel(StaffDashboardPanel dash) {
        this.dash = dash;

        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(Box.createVerticalStrut(10));
        add(header("Reports (Statistics)"));
        add(Box.createVerticalStrut(16));

        add(menuBtn("Total Residents", StaffDashboardPanel.CARD_REPORT_TOTAL));
        add(Box.createVerticalStrut(8));

        add(menuBtn("Gender Summary", StaffDashboardPanel.CARD_REPORT_GENDER));
        add(Box.createVerticalStrut(8));

        add(menuBtn("Household Count", StaffDashboardPanel.CARD_REPORT_HOUSEHOLD));
        add(Box.createVerticalStrut(8));

        add(menuBtn("Age Group Summary", StaffDashboardPanel.CARD_REPORT_AGEGROUP));

        add(Box.createVerticalStrut(18));
        add(backBtn());
        add(Box.createVerticalGlue());
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
        b.setMaximumSize(new Dimension(260, 40));
        b.setPreferredSize(new Dimension(260, 40));
        b.setFocusPainted(false);
        b.addActionListener(e -> dash.showCard(key));
        return b;
    }

    private JButton backBtn() {
        JButton b = new JButton("Back");
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setMaximumSize(new Dimension(140, 38));
        b.setPreferredSize(new Dimension(140, 38));
        b.setFocusPainted(false);
        b.addActionListener(e -> dash.showCard(StaffDashboardPanel.CARD_HOME));
        return b;
    }
}

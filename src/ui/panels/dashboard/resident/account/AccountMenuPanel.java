package ui.panels.dashboard.resident.account;

import ui.panels.dashboard.resident.ResidentDashboardPanel;

import javax.swing.*;
import java.awt.*;

public class AccountMenuPanel extends JPanel {

    private final ResidentDashboardPanel dash;

    public AccountMenuPanel(ResidentDashboardPanel dash) {
        this.dash = dash;

        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(header("Account Settings"));
        add(Box.createVerticalStrut(14));

        add(menuBtn("Change Password", ResidentDashboardPanel.CARD_CHANGE_PASSWORD));
        add(menuBtn("Login History", ResidentDashboardPanel.CARD_LOGIN_HISTORY));

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
        b.addActionListener(e -> dash.showCard(ResidentDashboardPanel.CARD_HOME));
        return b;
    }
}

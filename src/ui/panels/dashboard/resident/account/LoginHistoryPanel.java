package ui.panels.dashboard.resident.account;

import ui.mock.DummyStore;
import ui.panels.dashboard.resident.ResidentDashboardPanel;

import javax.swing.*;
import java.awt.*;

public class LoginHistoryPanel extends JPanel {

    private final JTable table = new JTable();

    public LoginHistoryPanel(ResidentDashboardPanel dash) {
        setOpaque(false);
        setLayout(new BorderLayout(10,10));

        add(topBar("Login History", () -> dash.showCard(ResidentDashboardPanel.CARD_ACCOUNT_MENU)), BorderLayout.NORTH);

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
        table.setModel(DummyStore.loginHistoryModel());
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

package ui.panels.dashboard.resident.report;

import models.Resident;
import services.common.ServiceResult;
import ui.frames.AppFrames;
import ui.panels.dashboard.resident.ResidentDashboardPanel;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HouseholdCountPanel extends JPanel {

    private final AppFrames frame;
    private final JLabel lbl = new JLabel();

    public HouseholdCountPanel(AppFrames frame, ResidentDashboardPanel dash) {
        this.frame = frame;

        setOpaque(false);
        setLayout(new BorderLayout(10,10));

        add(topBar("Household Count",
                () -> dash.showCard(ResidentDashboardPanel.CARD_REPORTS_MENU)),
                BorderLayout.NORTH);

        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 40));

        refresh();
        add(lbl, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);

        JButton refresh = new JButton("Refresh");
        refresh.setFocusPainted(false);
        refresh.addActionListener(e -> refresh());

        bottom.add(refresh);
        add(bottom, BorderLayout.SOUTH);
    }

    private void refresh() {
        ServiceResult<List<Resident>> res = frame.getResidentService().getAll();
        List<Resident> list = (res != null) ? res.getData() : null;

        Set<String> households = new HashSet<>();

        if (list != null) {
            for (Resident r : list) {
                if (r == null) continue;
                String addr = r.getAddress();
                if (addr == null || addr.trim().isEmpty()) continue;
                households.add(addr.trim().toLowerCase());
            }
        }

        lbl.setText(String.valueOf(households.size()));
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

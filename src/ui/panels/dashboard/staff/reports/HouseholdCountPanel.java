package ui.panels.dashboard.staff.reports;

import models.Resident;
import services.common.ServiceResult;
import ui.frames.AppFrames;
import ui.panels.dashboard.staff.StaffDashboardPanel;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HouseholdCountPanel extends JPanel {

    private final AppFrames frame;
    private final JLabel lbl = new JLabel();

    public HouseholdCountPanel(AppFrames frame, StaffDashboardPanel dash) {
        this.frame = frame;

        setOpaque(false);
        setLayout(new BorderLayout(10,10));

        add(topBar("Household Count", () -> dash.showCard(StaffDashboardPanel.CARD_REPORTS_MENU)),
                BorderLayout.NORTH);

        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 40));

        refresh();
        add(lbl, BorderLayout.CENTER);

        JButton btn = new JButton("Refresh");
        btn.setFocusPainted(false);
        btn.addActionListener(e -> refresh());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        bottom.add(btn);

        add(bottom, BorderLayout.SOUTH);
    }

    private void refresh() {
        ServiceResult<List<Resident>> res = frame.getResidentService().getAll();
        List<Resident> list = (res != null) ? res.getData() : null;

        if (list == null || list.isEmpty()) {
            lbl.setText("0");
            return;
        }

        // Unique addresses = households
        Set<String> households = new HashSet<>();

        for (Resident r : list) {
            if (r == null) continue;
            String addr = r.getAddress();
            if (addr == null || addr.isBlank()) continue;

            households.add(normalize(addr));
        }

        lbl.setText(String.valueOf(households.size()));
    }

    private String normalize(String s) {
        return s.trim().toLowerCase().replaceAll("\\s+", " ");
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

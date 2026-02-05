package ui.panels.dashboard.staff.reports;

import models.Resident;
import services.common.ServiceResult;
import ui.frames.AppFrames;
import ui.panels.dashboard.staff.StaffDashboardPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TotalResidentsPanel extends JPanel {

    private final AppFrames frame;
    private final JLabel lbl = new JLabel();

    public TotalResidentsPanel(AppFrames frame, StaffDashboardPanel dash) {
        this.frame = frame;

        setOpaque(false);
        setLayout(new BorderLayout(10,10));

        add(topBar("Total Residents", () -> dash.showCard(StaffDashboardPanel.CARD_REPORTS_MENU)),
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

        int total = (list == null) ? 0 : list.size();
        lbl.setText(String.valueOf(total));
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

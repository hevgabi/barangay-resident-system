package ui.panels.dashboard.staff.reports;

import models.Resident;
import services.common.ServiceResult;
import ui.frames.AppFrames;
import ui.panels.dashboard.staff.StaffDashboardPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GenderSummaryPanel extends JPanel {

    private final AppFrames frame;
    private final JTable table = new JTable();

    public GenderSummaryPanel(AppFrames frame, StaffDashboardPanel dash) {
        this.frame = frame;

        setOpaque(false);
        setLayout(new BorderLayout(10,10));

        add(topBar("Gender Summary", () -> dash.showCard(StaffDashboardPanel.CARD_REPORTS_MENU)), BorderLayout.NORTH);

        table.setRowHeight(24);
        refresh();

        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton btn = new JButton("Refresh");
        btn.setFocusPainted(false);
        btn.addActionListener(e -> refresh());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        bottom.add(btn);

        add(bottom, BorderLayout.SOUTH);
    }

    private void refresh() {
        DefaultTableModel m = new DefaultTableModel(new String[]{"Gender","Count"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        ServiceResult<List<Resident>> res = frame.getResidentService().getAll();
        List<Resident> list = (res != null) ? res.getData() : null;

        Map<String, Integer> counts = buildBase();

        if (list != null) {
            for (Resident r : list) {
                if (r == null) continue;
                String sex = normalizeSex(r.getSex());
                counts.put(sex, counts.get(sex) + 1);
            }
        }

        for (Map.Entry<String,Integer> e : counts.entrySet()) {
            m.addRow(new Object[]{e.getKey(), e.getValue()});
        }

        table.setModel(m);
    }

    private Map<String, Integer> buildBase() {
        Map<String, Integer> m = new LinkedHashMap<>();
        m.put("Male", 0);
        m.put("Female", 0);
        m.put("Other", 0);
        m.put("Unknown", 0);
        return m;
    }

    private String normalizeSex(String s) {
        if (s == null) return "Unknown";
        String v = s.trim().toLowerCase();
        if (v.isEmpty()) return "Unknown";

        if (v.equals("male") || v.equals("m")) return "Male";
        if (v.equals("female") || v.equals("f")) return "Female";

        return "Other";
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

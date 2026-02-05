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

public class AgeGroupSummaryPanel extends JPanel {

    private final AppFrames frame;
    private final JTable table = new JTable();

    public AgeGroupSummaryPanel(AppFrames frame, StaffDashboardPanel dash) {
        this.frame = frame;

        setOpaque(false);
        setLayout(new BorderLayout(10,10));

        add(topBar("Age Group Summary", () -> dash.showCard(StaffDashboardPanel.CARD_REPORTS_MENU)), BorderLayout.NORTH);

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
        DefaultTableModel m = new DefaultTableModel(new String[]{"Age Group","Count"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        ServiceResult<List<Resident>> res = frame.getResidentService().getAll();
        List<Resident> list = (res != null) ? res.getData() : null;

        Map<String, Integer> buckets = buildBuckets();

        if (list != null) {
            for (Resident r : list) {
                if (r == null) continue;
                int age = r.getAge();
                String key = bucketForAge(age);
                buckets.put(key, buckets.get(key) + 1);
            }
        }

        for (Map.Entry<String,Integer> e : buckets.entrySet()) {
            m.addRow(new Object[]{e.getKey(), e.getValue()});
        }

        table.setModel(m);
    }

    // Ordered buckets (so table looks consistent)
    private Map<String, Integer> buildBuckets() {
        Map<String, Integer> m = new LinkedHashMap<>();
        m.put("0-12", 0);
        m.put("13-17", 0);
        m.put("18-24", 0);
        m.put("25-34", 0);
        m.put("35-44", 0);
        m.put("45-59", 0);
        m.put("60+", 0);
        m.put("Unknown", 0);
        return m;
    }

    private String bucketForAge(int age) {
        if (age <= 0) return "Unknown";
        if (age <= 12) return "0-12";
        if (age <= 17) return "13-17";
        if (age <= 24) return "18-24";
        if (age <= 34) return "25-34";
        if (age <= 44) return "35-44";
        if (age <= 59) return "45-59";
        return "60+";
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

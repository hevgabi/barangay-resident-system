package ui.panels.dashboard.staff.manageresidents;

import models.Resident;
import services.format.ResidentRowFormatter;
import ui.frames.AppFrames;
import ui.panels.dashboard.staff.StaffDashboardPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public class ViewResidentsPanel extends JPanel {

    private final AppFrames frame;
    private final JTable table = new JTable();
    private final JScrollPane scroll = new JScrollPane(table);

    public ViewResidentsPanel(AppFrames frame, StaffDashboardPanel dash) {
        this.frame = frame;

        setOpaque(false);
        setLayout(new BorderLayout(10,10));

        add(topBar("View All Residents", () -> dash.showCard(StaffDashboardPanel.CARD_RESIDENTS_MENU)), BorderLayout.NORTH);

        table.setRowHeight(24);

        // IMPORTANT: prevent squeezing columns
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);

        JButton refresh = new JButton("Refresh");
        refresh.setFocusPainted(false);
        refresh.addActionListener(e -> refreshTable());

        bottom.add(refresh);
        add(bottom, BorderLayout.SOUTH);

        refreshTable();
    }

    private void refreshTable() {
        var result = frame.getResidentService().getAll();
        List<Resident> data = result.getData();
        if (data == null) data = Collections.emptyList();

        ResidentRowFormatter fmt = new ResidentRowFormatter();

        DefaultTableModel model = new DefaultTableModel(fmt.headers(), 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        for (Resident r : data) {
            model.addRow(fmt.toRow(r));
        }

        table.setModel(model);
        applyColumnWidths();

        if (!result.isSuccess()) {
            JOptionPane.showMessageDialog(this, result.getMessage());
        }
    }

    // Keep design, just makes columns readable.
    private void applyColumnWidths() {
        int[] widths = new int[] {
                90,   // Resident ID
                120,  // Last Name
                120,  // First Name
                60,   // M.I.
                70,   // Sex
                110,  // Birthday
                110,  // Contact
                220,  // Address
                110,  // Marital
                140,  // Employment
                140   // Occupation
        };

        TableColumnModel cm = table.getColumnModel();
        for (int i = 0; i < widths.length && i < cm.getColumnCount(); i++) {
            cm.getColumn(i).setPreferredWidth(widths[i]);
        }
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

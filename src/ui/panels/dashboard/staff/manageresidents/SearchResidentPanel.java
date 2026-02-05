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

public class SearchResidentPanel extends JPanel {

    private final AppFrames frame;

    private final JTextField txtKeyword = new JTextField();
    private final JComboBox<String> cboSex = new JComboBox<>(new String[]{"All", "Male", "Female"});
    private final JComboBox<String> cboPurok = new JComboBox<>(new String[]{"All", "1", "2", "3"}); // UI only for now
    private final JTable table = new JTable();
    private final JScrollPane sp = new JScrollPane(table);

    public SearchResidentPanel(AppFrames frame, StaffDashboardPanel dash) {
        this.frame = frame;

        setOpaque(false);
        setLayout(new BorderLayout(10, 10));

        add(topBar("Search Resident", () -> dash.showCard(StaffDashboardPanel.CARD_RESIDENTS_MENU)),
                BorderLayout.NORTH);

        // Filters bar stays on top (below header)
        JPanel filters = new JPanel(new GridBagLayout());
        filters.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0; c.weightx = 0;
        filters.add(new JLabel("Keyword:"), c);

        c.gridx = 1; c.gridy = 0; c.weightx = 1;
        filters.add(txtKeyword, c);

        c.gridx = 2; c.gridy = 0; c.weightx = 0;
        filters.add(new JLabel("Sex:"), c);

        c.gridx = 3; c.gridy = 0;
        filters.add(cboSex, c);

        c.gridx = 4; c.gridy = 0;
        filters.add(new JLabel("Purok:"), c);

        c.gridx = 5; c.gridy = 0;
        filters.add(cboPurok, c);

        JButton btnSearch = new JButton("Search");
        btnSearch.setFocusPainted(false);
        btnSearch.addActionListener(e -> doSearch());

        c.gridx = 6; c.gridy = 0;
        filters.add(btnSearch, c);

        // Put filters in a wrapper so CENTER is reserved for table
        JPanel northMid = new JPanel(new BorderLayout());
        northMid.setOpaque(false);
        northMid.add(filters, BorderLayout.CENTER);

        add(northMid, BorderLayout.CENTER); // ok because table will be in SOUTH? nope: we will put table in CENTER.
        // We'll fix this properly below by using a center wrapper

        // Table setup
        table.setRowHeight(24);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        sp.setBorder(null);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Proper layout: filters at top, table in center
        JPanel body = new JPanel(new BorderLayout(10, 10));
        body.setOpaque(false);
        body.add(filters, BorderLayout.NORTH);
        body.add(sp, BorderLayout.CENTER);

        add(body, BorderLayout.CENTER);

        // Initial load
        doSearch();
    }

    private void doSearch() {
        String keyword = txtKeyword.getText().trim();

        var result = frame.getResidentService().search(keyword);
        if (!result.isSuccess()) {
            JOptionPane.showMessageDialog(this, result.getMessage());
            return;
        }

        List<Resident> data = result.getData();
        if (data == null) data = Collections.emptyList();

        var fmt = new ResidentRowFormatter();

        DefaultTableModel model = new DefaultTableModel(fmt.headers(), 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        for (Resident r : data) {
            model.addRow(fmt.toRow(r));
        }

        table.setModel(model);
        applyColumnWidths();
    }

    // Same widths as View para consistent/readable
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

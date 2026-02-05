package ui.panels.dashboard.staff.account;

import ui.panels.dashboard.staff.StaffDashboardPanel;
import util.FileMaker;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class LoginHistoryPanel extends JPanel {

    private final JTable table = new JTable();
    private final String path = "data/login_history.txt";

    private final JTextField txtSearch = new JTextField();
    private final JComboBox<String> cboResult = new JComboBox<>(new String[]{"All", "SUCCESS", "FAIL"});
    private final JButton btnRefresh = new JButton("Refresh");
    private final JButton btnClear = new JButton("Clear History");

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Time", "Username", "Result", "Device"},
            0
    ) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    private final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);

    public LoginHistoryPanel(StaffDashboardPanel dash) {
        setOpaque(false);
        setLayout(new BorderLayout(10, 10));

        FileMaker.makeFileIfNotExists(path);

        add(topBar("Login History",
                () -> dash.showCard(StaffDashboardPanel.CARD_SETTINGS_MENU)),
                BorderLayout.NORTH);

        // ---- Table setup ----
        table.setModel(model);
        table.setRowHeight(24);
        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setRowSorter(sorter);

        // ---- Controls (Search + Filter) ----
        JPanel controls = new JPanel(new GridBagLayout());
        controls.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0;
        controls.add(new JLabel("Search:"), c);

        c.gridx = 1; c.gridy = 0;
        c.weightx = 1;
        controls.add(txtSearch, c);

        c.gridx = 2; c.gridy = 0;
        c.weightx = 0;
        controls.add(new JLabel("Result:"), c);

        c.gridx = 3; c.gridy = 0;
        controls.add(cboResult, c);

        // listeners
        txtSearch.getDocument().addDocumentListener((SimpleDocumentListener) e -> applyFilters());
        cboResult.addActionListener(e -> applyFilters());

        add(controls, BorderLayout.CENTER);

        // Wrap table + controls properly
        JPanel mid = new JPanel(new BorderLayout(10, 10));
        mid.setOpaque(false);
        mid.add(controls, BorderLayout.NORTH);
        mid.add(new JScrollPane(table), BorderLayout.CENTER);
        add(mid, BorderLayout.CENTER);

        // ---- Bottom buttons ----
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);

        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(e -> refresh());

        btnClear.setFocusPainted(false);
        btnClear.addActionListener(e -> clearHistory());

        bottom.add(btnClear);
        bottom.add(btnRefresh);
        add(bottom, BorderLayout.SOUTH);

        refresh();
    }

    private void refresh() {
        model.setRowCount(0);

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;

                // expected: time|username|result|device
                String[] p = line.split("\\|", -1);

                String time = (p.length > 0) ? p[0] : "";
                String user = (p.length > 1) ? p[1] : "";
                String result = (p.length > 2) ? p[2] : "";
                String device = (p.length > 3) ? p[3] : "";

                model.addRow(new Object[]{time, user, result, device});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        applyFilters();
    }

    private void applyFilters() {
        String keyword = txtSearch.getText().trim();
        String result = (String) cboResult.getSelectedItem();

        RowFilter<DefaultTableModel, Object> rf = new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                String time = entry.getStringValue(0);
                String user = entry.getStringValue(1);
                String res = entry.getStringValue(2);
                String device = entry.getStringValue(3);

                boolean matchResult = "All".equalsIgnoreCase(result) || res.equalsIgnoreCase(result);

                boolean matchKeyword = true;
                if (!keyword.isEmpty()) {
                    String all = (time + " " + user + " " + res + " " + device).toLowerCase();
                    matchKeyword = all.contains(keyword.toLowerCase());
                }

                return matchResult && matchKeyword;
            }
        };

        sorter.setRowFilter(rf);
    }

    private void clearHistory() {
        int ok = JOptionPane.showConfirmDialog(
                this,
                "Clear login history file?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
        );
        if (ok != JOptionPane.YES_OPTION) return;

        try (PrintWriter pw = new PrintWriter(path)) {
            pw.print("");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to clear history.");
            return;
        }

        refresh();
        JOptionPane.showMessageDialog(this, "Cleared.");
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

    // ---- small helper interface for DocumentListener ----
    @FunctionalInterface
    private interface SimpleDocumentListener extends javax.swing.event.DocumentListener {
        void update(javax.swing.event.DocumentEvent e);

        @Override default void insertUpdate(javax.swing.event.DocumentEvent e) { update(e); }
        @Override default void removeUpdate(javax.swing.event.DocumentEvent e) { update(e); }
        @Override default void changedUpdate(javax.swing.event.DocumentEvent e) { update(e); }
    }
}

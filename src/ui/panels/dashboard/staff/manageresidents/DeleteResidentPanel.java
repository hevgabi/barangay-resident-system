package ui.panels.dashboard.staff.manageresidents;

import models.Resident;
import ui.frames.AppFrames;
import ui.panels.dashboard.staff.StaffDashboardPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public class DeleteResidentPanel extends JPanel {

    private final AppFrames frame;

    private final JTextField txtId = new JTextField();
    private final JTable table = new JTable();
    private final DefaultTableModel model;

    private final JButton btnDelete = new JButton("Delete Selected");
    private final JButton btnRefresh = new JButton("Refresh");

    public DeleteResidentPanel(AppFrames frame, StaffDashboardPanel dash) {
        this.frame = frame;

        setOpaque(false);
        setLayout(new BorderLayout(10, 10));

        add(topBar("Delete Resident", () -> dash.showCard(StaffDashboardPanel.CARD_RESIDENTS_MENU)),
                BorderLayout.NORTH);

        // --- TABLE MODEL (8 columns) ---
        model = new DefaultTableModel(
                new Object[]{"Resident ID", "Last Name", "First Name", "MI", "Sex", "Birthdate", "Contact", "Address"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table.setModel(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(26);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.getTableHeader().setReorderingAllowed(false);

        table.getSelectionModel().addListSelectionListener(this::onRowSelect);

        JScrollPane sp = new JScrollPane(table);
        // para di sobrang laki yung table area pag konti rows
        sp.setPreferredSize(new Dimension(0, 240));

        // --- FORM / ACTIONS ---
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0;
        form.add(new JLabel("Resident ID:"), c);

        c.gridx = 1; c.gridy = 0;
        c.weightx = 1;
        txtId.setEditable(false);
        form.add(txtId, c);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(e -> refreshTable());

        btnDelete.setFocusPainted(false);
        btnDelete.addActionListener(e -> doDelete());

        actions.add(btnRefresh);
        actions.add(btnDelete);

        c.gridx = 0; c.gridy = 1;
        c.gridwidth = 2;
        c.weightx = 0;
        form.add(actions, c);

        // --- CENTER LAYOUT ---
        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setOpaque(false);

        center.add(sp, BorderLayout.NORTH);     // table top (compact)
        center.add(form, BorderLayout.CENTER);  // controls below

        add(center, BorderLayout.CENTER);

        refreshTable();
    }

    private void onRowSelect(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;

        int row = table.getSelectedRow();
        if (row < 0) {
            txtId.setText("");
            return;
        }

        Object id = model.getValueAt(row, 0);
        txtId.setText(id == null ? "" : id.toString());
    }

    private void refreshTable() {
        model.setRowCount(0);

        var res = frame.getResidentService().getAll(); // ServiceResult<List<Resident>>
        if (!res.isSuccess()) {
            JOptionPane.showMessageDialog(this, res.getMessage());
            return;
        }

        List<Resident> list = res.getData();
        if (list == null) list = Collections.emptyList();

        for (Resident r : list) {
            // IMPORTANT: order must match headers (8 columns)
            model.addRow(new Object[]{
                    safe(r.getResidentId()),
                    safe(r.getLastName()),
                    safe(r.getFirstName()),
                    safe(getMI(r)),          // handle if your model uses different getter
                    safe(r.getSex()),
                    safe(getBirthdate(r)),   // handle if your model uses different getter
                    safe(getContact(r)),     // handle if your model uses different getter
                    safe(r.getAddress())
            });
        }

        txtId.setText("");
        table.clearSelection();
    }

    private void doDelete() {
        String id = txtId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a resident from the table.");
            return;
        }

        int ok = JOptionPane.showConfirmDialog(
                this,
                "Delete resident " + id + " ?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
        );
        if (ok != JOptionPane.YES_OPTION) return;

        var res = frame.getResidentService().deleteById(id);
        if (!res.isSuccess()) {
            JOptionPane.showMessageDialog(this, res.getMessage());
            return;
        }

        JOptionPane.showMessageDialog(this, "Deleted.");
        refreshTable();
    }

    // ---- SAFE GETTERS (para di mag error kung iba method names mo) ----

    private String getMI(Resident r) {
        // palitan mo to kung ano talaga getter mo for MI
        // example: return r.getMi();
        try {
            // attempt common names
            return (String) r.getClass().getMethod("getMiddleInitial").invoke(r);
        } catch (Exception ignored) { }

        try {
            return (String) r.getClass().getMethod("getMi").invoke(r);
        } catch (Exception ignored) { }

        return ""; // fallback
    }

    private String getBirthdate(Resident r) {
        // palitan mo to kung ano talaga getter mo for birthdate
        // example: return String.valueOf(r.getBirthday());
        try {
            Object v = r.getClass().getMethod("getBirthdate").invoke(r);
            return v == null ? "" : v.toString();
        } catch (Exception ignored) { }

        try {
            Object v = r.getClass().getMethod("getBirthday").invoke(r);
            return v == null ? "" : v.toString();
        } catch (Exception ignored) { }

        return "";
    }

    private String getContact(Resident r) {
        // palitan mo to kung ano talaga getter mo for contact
        // example: return r.getContactNo();
        try {
            Object v = r.getClass().getMethod("getContact").invoke(r);
            return v == null ? "" : v.toString();
        } catch (Exception ignored) { }

        try {
            Object v = r.getClass().getMethod("getContactNo").invoke(r);
            return v == null ? "" : v.toString();
        } catch (Exception ignored) { }

        return "";
    }

    private String safe(Object o) {
        return o == null ? "" : o.toString();
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

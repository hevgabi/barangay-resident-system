package ui.panels.dashboard.staff.manageresidents;

import models.Resident;
import ui.frames.AppFrames;
import ui.panels.dashboard.staff.StaffDashboardPanel;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class UpdateResidentPanel extends JPanel {

    private final AppFrames frame;

    // ID + load
    private final JTextField txtId = new JTextField();

    // Same fields as AddResidentPanel
    private final JTextField txtFirst = new JTextField();
    private final JTextField txtMI = new JTextField();
    private final JTextField txtLast = new JTextField();
    private final JComboBox<String> cboSex = new JComboBox<>(new String[]{"Male","Female"});
    private final JTextField txtBday = new JTextField();
    private final JTextField txtContact = new JTextField();
    private final JTextField txtAddress = new JTextField();
    private final JComboBox<String> cboMarital = new JComboBox<>(new String[]{"Single","Married","Separated","Widowed"});
    private final JComboBox<String> cboEmployment = new JComboBox<>(new String[]{"Employed","Self-Employed","Unemployed","Student"});
    private final JTextField txtOccupation = new JTextField();
    private final JComboBox<String> cboIncome = new JComboBox<>(new String[]{"Low","Middle","High"});
    private final JComboBox<String> cboReligion = new JComboBox<>(new String[]{"Catholic","INC","Islam","Others"});
    private final JComboBox<String> cboMother = new JComboBox<>(new String[]{"Tagalog","Cebuano","Ilocano","Others"});
    private final JTextField txtMedical = new JTextField();
    private final JTextField txtPosition = new JTextField();
    private final JComboBox<String> cboStatus = new JComboBox<>(new String[]{"ACTIVE","MOVED","DECEASED"});

    private Resident loaded;

    public UpdateResidentPanel(AppFrames frame, StaffDashboardPanel dash) {
        this.frame = frame;

        setOpaque(false);
        setLayout(new BorderLayout(10,10));

        add(topBar("Update Resident", () -> dash.showCard(StaffDashboardPanel.CARD_RESIDENTS_MENU)),
                BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.fill = GridBagConstraints.HORIZONTAL;

        int y = 0;

        // --- ID row with Load button (kept in same "look") ---
        c.gridx = 0; c.gridy = y; c.weightx = 0; c.anchor = GridBagConstraints.WEST;
        form.add(new JLabel("Resident ID"), c);

        c.gridx = 1; c.gridy = y; c.weightx = 1;
        txtId.setPreferredSize(new Dimension(0, 28));
        form.add(txtId, c);

        JButton btnLoad = new JButton("Load");
        btnLoad.setFocusPainted(false);
        btnLoad.addActionListener(e -> loadById());

        c.gridx = 2; c.gridy = y; c.weightx = 0;
        form.add(btnLoad, c);

        y++;

        // --- same rows as AddResidentPanel ---
        y = row(form, c, y, "First Name", txtFirst);
        y = row(form, c, y, "M.I.", txtMI);
        y = row(form, c, y, "Last Name", txtLast);
        y = row(form, c, y, "Sex", cboSex);
        y = row(form, c, y, "Birthday (YYYY-MM-DD)", txtBday);
        y = row(form, c, y, "Contact Number", txtContact);
        y = row(form, c, y, "Address", txtAddress);
        y = row(form, c, y, "Marital Status", cboMarital);
        y = row(form, c, y, "Employment", cboEmployment);
        y = row(form, c, y, "Occupation", txtOccupation);
        y = row(form, c, y, "Income Bracket", cboIncome);
        y = row(form, c, y, "Religion", cboReligion);
        y = row(form, c, y, "Mother Tongue", cboMother);
        y = row(form, c, y, "Medical Condition", txtMedical);
        y = row(form, c, y, "Position (optional)", txtPosition);
        y = row(form, c, y, "Status", cboStatus);

        // glue pushes content up
        c.gridx = 0;
        c.gridy = y;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 3;
        form.add(Box.createVerticalGlue(), c);
        c.weighty = 0;
        c.gridwidth = 1;

        JScrollPane sp = new JScrollPane(form);
        sp.setBorder(null);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        add(sp, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);

        JButton btnClear = new JButton("Clear");
        btnClear.setFocusPainted(false);
        btnClear.addActionListener(e -> clearForm());

        JButton btnUpdate = new JButton("Update");
        btnUpdate.setFocusPainted(false);
        btnUpdate.addActionListener(e -> update());

        actions.add(btnClear);
        actions.add(btnUpdate);

        add(actions, BorderLayout.SOUTH);
    }

    private int row(JPanel p, GridBagConstraints c, int y, String label, JComponent field) {
        c.gridx = 0;
        c.gridy = y;
        c.weightx = 0;
        c.anchor = GridBagConstraints.WEST;
        p.add(new JLabel(label), c);

        // field expands
        c.gridx = 1;
        c.gridy = y;
        c.weightx = 1;
        c.gridwidth = 2;
        field.setPreferredSize(new Dimension(0, 28));
        p.add(field, c);

        c.gridwidth = 1;
        return y + 1;
    }

    private void loadById() {
        String id = txtId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Resident ID.");
            return;
        }

        var result = frame.getResidentService().findById(id);
        if (!result.isSuccess()) {
            JOptionPane.showMessageDialog(this, result.getMessage());
            loaded = null;
            return;
        }

        loaded = result.getData();

        txtFirst.setText(nz(loaded.getFirstName()));
        txtMI.setText(nz(loaded.getMiddleName()));
        txtLast.setText(nz(loaded.getLastName()));

        // normalize sex (handles "MALE" / "Male")
        setComboValue(cboSex, nz(loaded.getSex()), "Male");

        txtBday.setText(loaded.getDateOfBirth() == null ? "" : loaded.getDateOfBirth().toString());
        txtContact.setText(nz(loaded.getContactNumber()));
        txtAddress.setText(nz(loaded.getAddress()));

        setComboValue(cboMarital, nz(loaded.getMaritalStatus()), "Single");
        setComboValue(cboEmployment, nz(loaded.getEmploymentStatus()), "Unemployed");
        txtOccupation.setText(nz(loaded.getOccupation()));
        setComboValue(cboIncome, nz(loaded.getIncomeBracket()), "Low");
        setComboValue(cboReligion, nz(loaded.getReligion()), "Others");
        setComboValue(cboMother, nz(loaded.getMotherTongue()), "Others");
        txtMedical.setText(nz(loaded.getMedicalCondition()));
        txtPosition.setText(nz(loaded.getPosition()));
        setComboValue(cboStatus, nz(loaded.getStatus()), "ACTIVE");

        JOptionPane.showMessageDialog(this, "Loaded.");
    }

    private void update() {
        if (loaded == null) {
            JOptionPane.showMessageDialog(this, "Load a resident first.");
            return;
        }

        String id = txtId.getText().trim();
        String first = txtFirst.getText().trim();
        String last = txtLast.getText().trim();
        String bdayRaw = txtBday.getText().trim();

        if (id.isEmpty() || first.isEmpty() || last.isEmpty() || bdayRaw.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Resident ID, First Name, Last Name, Birthday are required.");
            return;
        }

        LocalDate dob;
        try {
            dob = LocalDate.parse(bdayRaw);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid birthday. Use YYYY-MM-DD.");
            return;
        }

        Resident updated = new Resident(
                id,
                first,
                txtMI.getText().trim(),
                last,
                (String) cboSex.getSelectedItem(),
                dob,
                txtContact.getText().trim(),
                txtAddress.getText().trim(),
                (String) cboMarital.getSelectedItem(),
                (String) cboEmployment.getSelectedItem(),
                txtOccupation.getText().trim(),
                (String) cboIncome.getSelectedItem(),
                (String) cboReligion.getSelectedItem(),
                (String) cboMother.getSelectedItem(),
                txtMedical.getText().trim(),
                txtPosition.getText().trim(),
                (String) cboStatus.getSelectedItem(),
                null, null
        );

        var result = frame.getResidentService().update(updated);
        if (!result.isSuccess()) {
            JOptionPane.showMessageDialog(this, result.getMessage());
            return;
        }

        loaded = result.getData();
        JOptionPane.showMessageDialog(this, "Updated.");
    }

    private void clearForm() {
        loaded = null;

        txtId.setText("");
        txtFirst.setText("");
        txtMI.setText("");
        txtLast.setText("");
        txtBday.setText("");
        txtContact.setText("");
        txtAddress.setText("");
        txtOccupation.setText("");
        txtMedical.setText("");
        txtPosition.setText("");

        cboSex.setSelectedIndex(0);
        cboMarital.setSelectedIndex(0);
        cboEmployment.setSelectedIndex(0);
        cboIncome.setSelectedIndex(0);
        cboReligion.setSelectedIndex(0);
        cboMother.setSelectedIndex(0);
        cboStatus.setSelectedIndex(0);
    }

    private void setComboValue(JComboBox<String> combo, String value, String fallback) {
        String v = (value == null) ? "" : value.trim();
        if (v.isEmpty()) {
            combo.setSelectedItem(fallback);
            return;
        }

        // try exact
        ComboBoxModel<String> m = combo.getModel();
        for (int i = 0; i < m.getSize(); i++) {
            String item = m.getElementAt(i);
            if (item.equalsIgnoreCase(v)) {
                combo.setSelectedItem(item);
                return;
            }
        }

        combo.setSelectedItem(fallback);
    }

    private String nz(String s) { return (s == null) ? "" : s; }

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

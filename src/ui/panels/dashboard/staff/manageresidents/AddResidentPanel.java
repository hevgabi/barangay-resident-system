package ui.panels.dashboard.staff.manageresidents;

import models.Resident;
import ui.frames.AppFrames;
import ui.panels.dashboard.staff.StaffDashboardPanel;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class AddResidentPanel extends JPanel {

    private final AppFrames frame;

    private final JTextField txtId = new JTextField(); // read-only display
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

    public AddResidentPanel(AppFrames frame, StaffDashboardPanel dash) {
        this.frame = frame;

        setOpaque(false);
        setLayout(new BorderLayout(10,10));

        add(topBar("Add Resident", () -> dash.showCard(StaffDashboardPanel.CARD_RESIDENTS_MENU)), BorderLayout.NORTH);

        txtId.setEditable(false);
        txtId.setFocusable(false);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.fill = GridBagConstraints.HORIZONTAL;

        int y = 0;
        y = row(form, c, y, "Resident ID", txtId);
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

        // glue pushes content up; looks clean and uses space
        c.gridx = 0;
        c.gridy = y;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 2;
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
        btnClear.addActionListener(e -> clear());

        JButton btnSave = new JButton("Save");
        btnSave.setFocusPainted(false);
        btnSave.addActionListener(e -> save());

        actions.add(btnClear);
        actions.add(btnSave);

        add(actions, BorderLayout.SOUTH);
    }

    private int row(JPanel p, GridBagConstraints c, int y, String label, JComponent field) {
        c.gridx = 0;
        c.gridy = y;
        c.weightx = 0;
        c.anchor = GridBagConstraints.WEST;
        p.add(new JLabel(label), c);

        // IMPORTANT: field expands and uses space (~60% look)
        c.gridx = 1;
        c.gridy = y;
        c.weightx = 1;
        c.gridwidth = 1;

        // Remove fixed 260px width. Let layout decide.
        // Give a "nice height" only.
        field.setPreferredSize(new Dimension(0, 28));

        p.add(field, c);
        return y + 1;
    }

    private void save() {
        String first = txtFirst.getText().trim();
        String last = txtLast.getText().trim();
        String bday = txtBday.getText().trim();

        if (first.isEmpty() || last.isEmpty() || bday.isEmpty()) {
            JOptionPane.showMessageDialog(this, "First Name, Last Name, Birthday are required.");
            return;
        }

        LocalDate dob;
        try {
            dob = LocalDate.parse(bday);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid birthday. Use YYYY-MM-DD.");
            return;
        }

        Resident r = new Resident(
                null,
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
                "ACTIVE",
                null, null
        );

        var result = frame.getResidentService().create(r);
        if (!result.isSuccess()) {
            JOptionPane.showMessageDialog(this, result.getMessage());
            return;
        }

        txtId.setText(result.getData().getResidentId());
        JOptionPane.showMessageDialog(this, "Resident saved.");
        clearButKeepId();
    }

    private void clear() {
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
    }

    private void clearButKeepId() {
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

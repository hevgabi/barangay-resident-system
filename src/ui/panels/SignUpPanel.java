package ui.panels;

import ui.frames.AppFrames;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

import services.auth.SignupRequest;
import services.auth.StaffSignupRequest;
import services.common.ServiceResult;

public class SignUpPanel extends JPanel {

    private final AppFrames frame;

    // Resident proof
    private JTextField txtResidentId;
    private JTextField txtDob; // YYYY-MM-DD

    // Staff proof
    private JTextField txtInviteCode;

    // Common
    private JTextField txtFullName;
    private JTextField txtUsername;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirm;

    private JComboBox<String> cboRole;

    private JButton btnCreate;
    private JButton btnBack;

    // labels for toggle
    private JLabel lblResidentId;
    private JLabel lblDob;
    private JLabel lblInvite;

    public SignUpPanel(AppFrames frame) {
        this.frame = frame;

        setLayout(new GridBagLayout());
        setBackground(new Color(245, 245, 245));

        JPanel card = new JPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(460, 650));
        card.setBackground(Color.LIGHT_GRAY);
        card.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.weightx = 1;

        JLabel title = new JLabel("Create Account", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        cboRole = new JComboBox<>(new String[]{"Resident", "Staff"});

        // Resident fields (REQUIRED for Resident signup)
        txtResidentId = new JTextField(18);
        txtDob = new JTextField(18);

        // Staff fields
        txtInviteCode = new JTextField(18);

        // Common fields
        txtFullName = new JTextField(18);
        txtUsername = new JTextField(18);
        txtEmail = new JTextField(18);
        txtPassword = new JPasswordField(18);
        txtConfirm = new JPasswordField(18);

        btnCreate = new JButton("Create Account");
        btnBack = new JButton("Back");

        // --- layout ---
        c.gridy = 0;
        card.add(title, c);

        c.gridy++;
        card.add(new JLabel("Role"), c);
        c.gridy++;
        card.add(cboRole, c);

        lblResidentId = new JLabel("Resident ID");
        lblDob = new JLabel("Date of Birth (YYYY-MM-DD)");
        lblInvite = new JLabel("Staff Invite Code");

        c.gridy++;
        card.add(lblResidentId, c);
        c.gridy++;
        card.add(txtResidentId, c);

        c.gridy++;
        card.add(lblDob, c);
        c.gridy++;
        card.add(txtDob, c);

        c.gridy++;
        card.add(lblInvite, c);
        c.gridy++;
        card.add(txtInviteCode, c);

        c.gridy++;
        card.add(new JLabel("Full Name (optional)"), c);
        c.gridy++;
        card.add(txtFullName, c);

        c.gridy++;
        card.add(new JLabel("Username"), c);
        c.gridy++;
        card.add(txtUsername, c);

        c.gridy++;
        card.add(new JLabel("Email"), c);
        c.gridy++;
        card.add(txtEmail, c);

        c.gridy++;
        card.add(new JLabel("Password"), c);
        c.gridy++;
        card.add(txtPassword, c);

        c.gridy++;
        card.add(new JLabel("Confirm Password"), c);
        c.gridy++;
        card.add(txtConfirm, c);

        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        row.add(btnBack);
        row.add(btnCreate);

        c.gridy++;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        card.add(row, c);

        add(card);

        applyRoleUI();

        cboRole.addActionListener(e -> applyRoleUI());

        btnBack.addActionListener(e -> {
            clearFields();
            frame.goToLogin();
        });

        btnCreate.addActionListener(e -> onCreate());
    }

    private void applyRoleUI() {
        String role = (String) cboRole.getSelectedItem();
        boolean isStaff = "Staff".equalsIgnoreCase(role);

        // Resident controls visible only for Resident
        lblResidentId.setVisible(!isStaff);
        txtResidentId.setVisible(!isStaff);
        lblDob.setVisible(!isStaff);
        txtDob.setVisible(!isStaff);

        // Staff invite visible only for Staff
        lblInvite.setVisible(isStaff);
        txtInviteCode.setVisible(isStaff);

        // Clear fields when switching roles
        if (isStaff) {
            txtResidentId.setText("");
            txtDob.setText("");
        } else {
            txtInviteCode.setText("");
        }

        revalidate();
        repaint();
    }

    private void onCreate() {
        String role = (String) cboRole.getSelectedItem();

        String fullName = txtFullName.getText().trim(); // optional
        String username = txtUsername.getText().trim();
        String email = txtEmail.getText().trim();
        String pass = new String(txtPassword.getPassword());
        String confirm = new String(txtConfirm.getPassword());

        if (username.isEmpty() || email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please complete all required fields.");
            return;
        }
        if (!email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email.");
            return;
        }
        if (!pass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Password does not match.");
            return;
        }

        // RESIDENT SIGNUP (ResidentID + DOB required)
        if ("Resident".equalsIgnoreCase(role)) {
            String residentId = txtResidentId.getText().trim();
            String dobRaw = txtDob.getText().trim();

            if (residentId.isEmpty() || dobRaw.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Resident ID and DOB are required.");
                return;
            }

            LocalDate dob;
            try {
                dob = LocalDate.parse(dobRaw);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "DOB format must be YYYY-MM-DD.");
                return;
            }

            SignupRequest req = new SignupRequest(residentId, dob, username, pass, email);
            ServiceResult<Void> res = frame.getAuthService().signupResident(req);

            if (!res.isSuccess()) {
                JOptionPane.showMessageDialog(this, res.getMessage());
                return;
            }

            JOptionPane.showMessageDialog(this, res.getMessage());
            clearFields();
            frame.goToOTP();
            return;
        }

        // STAFF SIGNUP (Invite Code required)
        String invite = txtInviteCode.getText().trim();
        if (invite.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Invite Code is required for Staff signup.");
            return;
        }

        StaffSignupRequest req = new StaffSignupRequest(invite, fullName, username, pass, email);
        ServiceResult<Void> res = frame.getAuthService().signupStaffWithInvite(req);

        if (!res.isSuccess()) {
            JOptionPane.showMessageDialog(this, res.getMessage());
            return;
        }

        JOptionPane.showMessageDialog(this, res.getMessage());
        clearFields();
        frame.goToOTP();
    }

    private void clearFields() {
        txtResidentId.setText("");
        txtDob.setText("");
        txtInviteCode.setText("");
        txtFullName.setText("");
        txtUsername.setText("");
        txtEmail.setText("");
        txtPassword.setText("");
        txtConfirm.setText("");
        cboRole.setSelectedIndex(0);
        applyRoleUI();
    }
}

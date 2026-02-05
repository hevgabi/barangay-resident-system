package ui.panels;

import ui.frames.AppFrames;

import javax.swing.*;
import java.awt.*;

import services.common.ServiceResult;

public class LoginPanel extends JPanel {

    private final AppFrames frame;

    public LoginPanel(AppFrames frame) {
        this.frame = frame;

        GridBagConstraints root = new GridBagConstraints();
        root.gridx = 0;
        root.gridy = 0;
        root.anchor = GridBagConstraints.CENTER;
        root.fill = GridBagConstraints.NONE;
        root.weightx = 1;
        root.weighty = 1;

        setLayout(new GridBagLayout());
        setBackground(new Color(245, 245, 245));

        JPanel card = new JPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(400, 300));
        card.setBackground(Color.LIGHT_GRAY);
        card.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Login", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JTextField txtUser = new JTextField(15);
        JPasswordField txtPass = new JPasswordField(15);

        JButton btnLogin = new JButton("Login");
        JButton btnSignup = new JButton("Sign Up");

        btnLogin.addActionListener(e -> {
            String username = txtUser.getText().trim();
            String password = new String(txtPass.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter username and password.");
                // log failed attempt (missing fields)
                frame.logLoginFail(username, "missing fields");
                return;
            }

            // simple UX: prevent double click
            btnLogin.setEnabled(false);
            btnSignup.setEnabled(false);

            ServiceResult<Void> res = frame.getAuthService().login(username, password);

            if (!res.isSuccess()) {
                JOptionPane.showMessageDialog(this, res.getMessage());
                // log failed attempt (wrong creds / not found / etc)
                frame.logLoginFail(username, res.getMessage());

                btnLogin.setEnabled(true);
                btnSignup.setEnabled(true);
                return;
            }

            // IMPORTANT: do NOT log success here.
            // Success should be logged after OTP verification.
            JOptionPane.showMessageDialog(this, res.getMessage());

            txtPass.setText("");
            btnLogin.setEnabled(true);
            btnSignup.setEnabled(true);

            frame.goToOTP();
        });

        btnSignup.addActionListener(e -> frame.goToSignup());

        c.gridx = 0; c.gridy = 0;
        card.add(title, c);

        c.gridy++;
        card.add(new JLabel("Username"), c);

        c.gridy++;
        card.add(txtUser, c);

        c.gridy++;
        card.add(new JLabel("Password"), c);

        c.gridy++;
        card.add(txtPass, c);

        c.gridy++;
        c.anchor = GridBagConstraints.CENTER;
        card.add(btnLogin, c);

        c.gridy++;
        card.add(btnSignup, c);

        add(card, root);
    }
}

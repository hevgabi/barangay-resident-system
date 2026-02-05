package ui.panels;

import java.awt.*;
import javax.swing.*;

import ui.frames.AppFrames;
import services.common.ServiceResult;

public class OTPPanel extends JPanel {
    private final AppFrames frame;

    private JLabel title;
    private JLabel enterOTP;
    private JTextField otpFieldJTextField;
    private JButton submitButton;
    private JButton backButton;

    public OTPPanel(AppFrames frame) {
        this.frame = frame;

        setLayout(new GridBagLayout());
        setBackground(new Color(245, 245, 245));

        JPanel card = new JPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(400, 600));
        card.setBackground(Color.LIGHT_GRAY);
        card.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;

        title = new JLabel("OTP Verification");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        enterOTP = new JLabel("Enter OTP:");
        otpFieldJTextField = new JTextField(15);

        submitButton = new JButton("Verify");
        backButton = new JButton("Back");

        c.gridy = 0;
        card.add(title, c);

        c.gridy++;
        card.add(enterOTP, c);

        c.gridy++;
        card.add(otpFieldJTextField, c);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnRow.add(backButton);
        btnRow.add(submitButton);

        c.gridy++;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        card.add(btnRow, c);

        add(card);

        backButton.addActionListener(e -> {
            clear();
            frame.goToLogin();
        });

        submitButton.addActionListener(e -> {
            String input = otpFieldJTextField.getText().trim();

            if (input.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter OTP first.");
                return;
            }

            ServiceResult<models.User> res = frame.getAuthService().verifyOtp(input);

            if (!res.isSuccess()) {
                JOptionPane.showMessageDialog(this, res.getMessage());
                return;
            }

            models.User user = res.getData();

            // ✅ IMPORTANT: set session FIRST
            frame.setCurrentUser(user);

            // ✅ clear OTP field
            clear();

            frame.logLoginSuccess();

            // ✅ route by role using AppFrames (fixed)
            frame.showDashboardForRole(user.getRole());

        });
    }

    public String getOtpInput() {
        return otpFieldJTextField.getText();
    }

    public void clear() {
        otpFieldJTextField.setText("");
        otpFieldJTextField.requestFocusInWindow();
    }
}

package ui.frames;

import javax.swing.*;

import models.User;
import repos.ResidentRepo;
import services.ResidentService;
import services.auth.AuthService;
import repos.UserRepo;
import services.LoginHistoryService;

import java.awt.*;

import ui.panels.BarangaySelectPanel;
import ui.panels.LoginPanel;
import ui.panels.OTPPanel;
import ui.panels.SignUpPanel;
import ui.panels.dashboard.resident.ResidentDashboardPanel;
import ui.panels.dashboard.staff.StaffDashboardPanel;

public class AppFrames extends JFrame {

    private final ResidentService residentService;
    private AuthService authService;
    private User currentUser;
    private final LoginHistoryService loginHistoryService = new LoginHistoryService();

    public static final String SCREEN_BARANGAY = "Barangay";
    public static final String SCREEN_LOGIN = "Login";
    public static final String SCREEN_SIGNUP = "Signup";
    public static final String SCREEN_OTP = "OTP";

    public static final String SCREEN_STAFF_DASHBOARD = "StaffDashboard";
    public static final String SCREEN_RESIDENT_DASHBOARD = "ResidentDashboard";

    private OTPPanel otpPanel;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);

    public AppFrames() {
        ResidentRepo residentRepo = new ResidentRepo();
        this.residentService = new ResidentService(residentRepo);

        UserRepo userRepo = new UserRepo();
        this.authService = new AuthService(userRepo, this.residentService);

        setTitle("Barangay Resident System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        otpPanel = new OTPPanel(this);

        // Core screens
        cards.add(new BarangaySelectPanel(this), SCREEN_BARANGAY);
        cards.add(new LoginPanel(this), SCREEN_LOGIN);
        cards.add(new SignUpPanel(this), SCREEN_SIGNUP);
        cards.add(otpPanel, SCREEN_OTP);

        // ✅ DO NOT add dashboards here (currentUser is null at startup)
        // Dashboards will be created when needed

        setContentPane(cards);

        setSize(1000, 700);
        setLocationRelativeTo(null);
        showScreen(SCREEN_LOGIN);
        setVisible(true);
    }

    public void showScreen(String name) {
        cardLayout.show(cards, name);
        cards.revalidate();
        cards.repaint();
    }

    public void goToBarangay() {
        showScreen(SCREEN_BARANGAY);
    }

    public void goToLogin() {
        showScreen(SCREEN_LOGIN);
    }

    public void goToSignup() {
        showScreen(SCREEN_SIGNUP);
    }

    public void goToOTP() {
        showScreen(SCREEN_OTP);
    }

    // ✅ Create & show dashboard dynamically (fresh panel with currentUser ready)
    private void showStaffDashboard() {
        cards.add(new StaffDashboardPanel(this), SCREEN_STAFF_DASHBOARD);
        showScreen(SCREEN_STAFF_DASHBOARD);
    }

    private void showResidentDashboard() {
        cards.add(new ResidentDashboardPanel(this), SCREEN_RESIDENT_DASHBOARD);
        showScreen(SCREEN_RESIDENT_DASHBOARD);
    }

    // Role routing after OTP
    public void showDashboardForRole(String role) {
        if (role != null && role.equalsIgnoreCase("STAFF")) {
            showStaffDashboard();
        } else {
            showResidentDashboard();
        }
    }

    public ResidentService getResidentService() {
        return residentService;
    }

    public void setAuthService(services.auth.AuthService authService) {
        this.authService = authService;
    }

    public services.auth.AuthService getAuthService() {
        return authService;
    }

    public void setCurrentUser(models.User u) {
        this.currentUser = u;
    }

    public models.User getCurrentUser() {
        return currentUser;
    }

    public void showCard(String name) {
        showScreen(name);
    }

    public LoginHistoryService getLoginHistoryService() {
        return loginHistoryService;
    }

    public void logLoginSuccess() {
        if (currentUser == null)
            return;
        loginHistoryService.logSuccess(
                currentUser.getUsername(),
                currentUser.getRole(),
                loginHistoryService.getDefaultDeviceLabel());
    }

    public void logLoginFail(String username, String note) {
        loginHistoryService.logFail(
                username == null ? "" : username.trim(),
                note == null ? "" : note.trim(),
                loginHistoryService.getDefaultDeviceLabel());
    }   
}

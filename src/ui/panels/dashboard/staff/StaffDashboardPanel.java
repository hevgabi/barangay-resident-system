package ui.panels.dashboard.staff;

import ui.frames.AppFrames;

import ui.panels.dashboard.staff.account.AccountMenuPanel;
import ui.panels.dashboard.staff.account.ChangePasswordPanel;
import ui.panels.dashboard.staff.account.LoginHistoryPanel;

import ui.panels.dashboard.staff.manageresidents.*;
import ui.panels.dashboard.staff.reports.*;
import ui.panels.dashboard.staff.usermanagement.*;

import javax.swing.*;
import java.awt.*;

public class StaffDashboardPanel extends JPanel {

    private final AppFrames frame;

    private final CardLayout contentLayout = new CardLayout();
    private final JPanel content = new JPanel(contentLayout);

    // MENUS
    public static final String CARD_HOME = "HOME";
    public static final String CARD_RESIDENTS_MENU = "RESIDENTS_MENU";
    public static final String CARD_REPORTS_MENU = "REPORTS_MENU";
    public static final String CARD_USERS_MENU = "USERS_MENU";
    public static final String CARD_SETTINGS_MENU = "SETTINGS_MENU";

    // Manage Residents pages
    public static final String CARD_ADD_RESIDENT = "ADD_RESIDENT";
    public static final String CARD_VIEW_RESIDENTS = "VIEW_RESIDENTS";
    public static final String CARD_SEARCH_RESIDENT = "SEARCH_RESIDENT";
    public static final String CARD_UPDATE_RESIDENT = "UPDATE_RESIDENT";
    public static final String CARD_DELETE_RESIDENT = "DELETE_RESIDENT";

    // Reports pages
    public static final String CARD_REPORT_TOTAL = "REPORT_TOTAL";
    public static final String CARD_REPORT_GENDER = "REPORT_GENDER";
    public static final String CARD_REPORT_HOUSEHOLD = "REPORT_HOUSEHOLD";
    public static final String CARD_REPORT_AGEGROUP = "REPORT_AGEGROUP";

    // Users pages
    public static final String CARD_VIEW_USERS = "VIEW_USERS";
    public static final String CARD_REMOVE_USER = "REMOVE_USER";

    // Account pages
    public static final String CARD_CHANGE_PASSWORD = "CHANGE_PASSWORD";
    public static final String CARD_LOGIN_HISTORY = "LOGIN_HISTORY";

    public StaffDashboardPanel(AppFrames frame) {
        this.frame = frame;

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        models.User u = frame.getCurrentUser();
        if (u == null || !"STAFF".equalsIgnoreCase(u.getRole())) {
            // ✅ Never return a blank panel
            add(new JLabel("Unauthorized. Redirecting to login...", SwingConstants.CENTER), BorderLayout.CENTER);
            SwingUtilities.invokeLater(frame::goToLogin);
            return;
        }

        add(buildSidebar(), BorderLayout.WEST);
        add(buildContent(), BorderLayout.CENTER);

        showCard(CARD_HOME);
    }

    private JPanel buildSidebar() {
        JPanel side = new JPanel();
        side.setPreferredSize(new Dimension(220, 0));
        side.setBackground(new Color(230, 230, 230));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(BorderFactory.createEmptyBorder(16, 12, 16, 12));

        JLabel title = new JLabel("Menu");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        side.add(title);
        side.add(Box.createVerticalStrut(18));

        JButton btnResidents = navBtn("Manage Residents");
        btnResidents.addActionListener(e -> showCard(CARD_RESIDENTS_MENU));

        JButton btnReports = navBtn("Reports (Statistics)");
        btnReports.addActionListener(e -> showCard(CARD_REPORTS_MENU));

        JButton btnUsers = navBtn("User Management");
        btnUsers.addActionListener(e -> showCard(CARD_USERS_MENU));

        JButton btnSettings = navBtn("Account Settings");
        btnSettings.addActionListener(e -> showCard(CARD_SETTINGS_MENU));

        JButton btnInvite = navBtn("Generate Staff Invite");
        btnInvite.addActionListener(e -> {
            models.User u = frame.getCurrentUser();
            var res = frame.getAuthService().generateStaffInvite(u, 15);

            if (!res.isSuccess()) {
                JOptionPane.showMessageDialog(this, res.getMessage());
                return;
            }

            JOptionPane.showMessageDialog(this,
                    "Invite Code: " + res.getData() + "\nValid for 15 minutes.");
        });

        JButton btnLogout = navBtn("Logout");
        btnLogout.addActionListener(e -> {
            frame.setCurrentUser(null);
            frame.goToLogin();
        });

        side.add(btnResidents);
        side.add(Box.createVerticalStrut(10));
        side.add(btnReports);
        side.add(Box.createVerticalStrut(10));
        side.add(btnUsers);
        side.add(Box.createVerticalStrut(10));
        side.add(btnSettings);
        side.add(Box.createVerticalStrut(10));
        side.add(btnInvite);
        side.add(Box.createVerticalGlue());
        side.add(btnLogout);

        return side;
    }

    private JPanel buildContent() {
        content.setOpaque(false);

        // HOME
        content.add(buildHomePanel(), CARD_HOME);

        // MENUS
        content.add(new ManageResidentsMenuPanel(this), CARD_RESIDENTS_MENU);
        content.add(new ReportsMenuPanel(this), CARD_REPORTS_MENU);
        content.add(new UserManagementMenuPanel(this), CARD_USERS_MENU);
        content.add(new AccountMenuPanel(this), CARD_SETTINGS_MENU);

        // Manage Residents pages
        content.add(new AddResidentPanel(frame, this), CARD_ADD_RESIDENT);
        content.add(new ViewResidentsPanel(frame, this), CARD_VIEW_RESIDENTS);
        content.add(new SearchResidentPanel(frame, this), CARD_SEARCH_RESIDENT);
        content.add(new UpdateResidentPanel(frame, this), CARD_UPDATE_RESIDENT);
        content.add(new DeleteResidentPanel(frame, this), CARD_DELETE_RESIDENT);

        // Reports pages
        content.add(new TotalResidentsPanel(frame, this), CARD_REPORT_TOTAL);
        content.add(new GenderSummaryPanel(frame, this), CARD_REPORT_GENDER);
        content.add(new HouseholdCountPanel(frame, this), CARD_REPORT_HOUSEHOLD);
        content.add(new AgeGroupSummaryPanel(frame, this), CARD_REPORT_AGEGROUP);

        // Users pages
        content.add(new ViewUsersPanel(frame, this), CARD_VIEW_USERS);
        content.add(new RemoveUserPanel(this), CARD_REMOVE_USER);

        // Account pages
        content.add(new ChangePasswordPanel(this), CARD_CHANGE_PASSWORD);
        content.add(new LoginHistoryPanel(this), CARD_LOGIN_HISTORY);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(245, 245, 245));
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        card.add(content, BorderLayout.CENTER);
        wrapper.add(card, BorderLayout.CENTER);

        return wrapper;
    }

    private JPanel buildHomePanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JLabel lbl = new JLabel("Staff Dashboard");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbl.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel sub = new JLabel("Manage residents, view reports, manage users, and update account settings.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sub.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        center.add(Box.createVerticalGlue());
        center.add(lbl);
        center.add(Box.createVerticalStrut(8));
        center.add(sub);
        center.add(Box.createVerticalGlue());

        p.add(center, BorderLayout.CENTER);
        return p;
    }

    private JButton navBtn(String text) {
        JButton b = new JButton(text);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setMaximumSize(new Dimension(190, 42));
        b.setPreferredSize(new Dimension(190, 42));
        b.setFocusPainted(false);
        return b;
    }

    public void showCard(String key) {
        contentLayout.show(content, key);
        content.revalidate();
        content.repaint();
    }
}

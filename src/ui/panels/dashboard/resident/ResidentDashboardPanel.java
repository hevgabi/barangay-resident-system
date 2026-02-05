package ui.panels.dashboard.resident;

import ui.frames.AppFrames;
import ui.panels.dashboard.resident.account.AccountMenuPanel;
import ui.panels.dashboard.resident.account.ChangePasswordPanel;
import ui.panels.dashboard.resident.account.LoginHistoryPanel;
import ui.panels.dashboard.resident.report.*;

import javax.swing.*;
import java.awt.*;

public class ResidentDashboardPanel extends JPanel {

    private final AppFrames frame;

    private final CardLayout contentLayout = new CardLayout();
    private final JPanel content = new JPanel(contentLayout);

    // MENUS
    public static final String CARD_HOME = "HOME";
    public static final String CARD_REPORTS_MENU = "REPORTS_MENU";
    public static final String CARD_ACCOUNT_MENU = "ACCOUNT_MENU";

    // REPORT PAGES
    public static final String CARD_REPORT_TOTAL = "REPORT_TOTAL";
    public static final String CARD_REPORT_GENDER = "REPORT_GENDER";
    public static final String CARD_REPORT_HOUSEHOLD = "REPORT_HOUSEHOLD";
    public static final String CARD_REPORT_AGEGROUP = "REPORT_AGEGROUP";

    // ACCOUNT PAGES
    public static final String CARD_CHANGE_PASSWORD = "CHANGE_PASSWORD";
    public static final String CARD_LOGIN_HISTORY = "LOGIN_HISTORY";

    public ResidentDashboardPanel(AppFrames frame) {
        this.frame = frame;

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

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

        JButton btnReports = navBtn("Reports (Statistics)");
        btnReports.addActionListener(e -> showCard(CARD_REPORTS_MENU));

        JButton btnAccount = navBtn("Account Settings");
        btnAccount.addActionListener(e -> showCard(CARD_ACCOUNT_MENU));

        JButton btnLogout = navBtn("Logout");
        btnLogout.addActionListener(e -> frame.goToLogin());

        side.add(btnReports);
        side.add(Box.createVerticalStrut(10));
        side.add(btnAccount);
        side.add(Box.createVerticalGlue());
        side.add(btnLogout);

        return side;
    }

    private JPanel buildContent() {
        content.setOpaque(false);

        // HOME
        content.add(buildHomePanel(), CARD_HOME);

        // MENUS
        content.add(new ReportsMenuPanel(this), CARD_REPORTS_MENU);
        content.add(new AccountMenuPanel(this), CARD_ACCOUNT_MENU);

        // REPORT PAGES
        content.add(new TotalResidentsPanel(frame, this), CARD_REPORT_TOTAL);
        content.add(new GenderSummaryPanel(frame, this), CARD_REPORT_GENDER);
        content.add(new HouseholdCountPanel(frame, this), CARD_REPORT_HOUSEHOLD);
        content.add(new AgeGroupSummaryPanel(frame, this), CARD_REPORT_AGEGROUP);

        // ACCOUNT PAGES
        content.add(new ChangePasswordPanel(this), CARD_CHANGE_PASSWORD);
        content.add(new LoginHistoryPanel(this), CARD_LOGIN_HISTORY);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(245, 245, 245));

        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(780, 520));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210,210,210)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        card.add(content, BorderLayout.CENTER);
        wrapper.add(card);

        return wrapper;
    }

    private JPanel buildHomePanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JLabel lbl = new JLabel("Resident Dashboard");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbl.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel sub = new JLabel("You can view statistics reports and manage your account.");
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
    }
}

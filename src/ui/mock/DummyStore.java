package ui.mock;

import javax.swing.table.DefaultTableModel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DummyStore {

    // ---------- Residents ----------
    public static class ResidentRow {
        public String residentID, firstName, middleInitial, lastName, sex, birthday, address, contactNumber;
        public String maritalStatus, employment, occupation, incomeBracket, religion, motherTongue, medicalCondition, position;

        public ResidentRow(String residentID, String firstName, String middleInitial, String lastName,
                           String sex, String birthday, String address, String contactNumber,
                           String maritalStatus, String employment, String occupation, String incomeBracket,
                           String religion, String motherTongue, String medicalCondition, String position) {
            this.residentID = residentID;
            this.firstName = firstName;
            this.middleInitial = middleInitial;
            this.lastName = lastName;
            this.sex = sex;
            this.birthday = birthday;
            this.address = address;
            this.contactNumber = contactNumber;
            this.maritalStatus = maritalStatus;
            this.employment = employment;
            this.occupation = occupation;
            this.incomeBracket = incomeBracket;
            this.religion = religion;
            this.motherTongue = motherTongue;
            this.medicalCondition = medicalCondition;
            this.position = position;
        }
    }

    private static final List<ResidentRow> residents = new ArrayList<>();

    // ---------- Users ----------
    public static class UserRow {
        public String username, role, email, status;

        public UserRow(String username, String role, String email, String status) {
            this.username = username;
            this.role = role;
            this.email = email;
            this.status = status;
        }
    }

    private static final List<UserRow> users = new ArrayList<>();
    private static final List<String[]> loginHistory = new ArrayList<>();

    static {
        // seed residents
        residents.add(new ResidentRow("R-0001","Juan","D","Cruz","Male","2003-05-10","Purok 1, Brgy. Sample","09171234567",
                "Single","Employed","Clerk","Low","Catholic","Tagalog","None","None"));
        residents.add(new ResidentRow("R-0002","Maria","A","Santos","Female","1998-11-21","Purok 2, Brgy. Sample","09981230011",
                "Married","Self-Employed","Vendor","Middle","Catholic","Tagalog","Asthma","None"));
        residents.add(new ResidentRow("R-0003","Ken","M","Reyes","Male","1985-02-03","Purok 3, Brgy. Sample","09061239900",
                "Married","Unemployed","None","Low","INC","Cebuano","Hypertension","Tanod"));

        // seed users
        users.add(new UserRow("staff01","Staff","staff01@gmail.com","Active"));
        users.add(new UserRow("staff02","Staff","staff02@gmail.com","Active"));
        users.add(new UserRow("res01","Resident","res01@gmail.com","Active"));

        // seed login history
        addLoginHistory("staff01", "SUCCESS");
        addLoginHistory("staff01", "SUCCESS");
        addLoginHistory("staff02", "FAILED");
        addLoginHistory("res01", "SUCCESS");
    }

    public static List<ResidentRow> getResidents() {
        return residents;
    }

    public static ResidentRow findResidentById(String id) {
        for (ResidentRow r : residents) {
            if (r.residentID.equalsIgnoreCase(id)) return r;
        }
        return null;
    }

    public static void addResident(ResidentRow r) {
        residents.add(r);
    }

    public static boolean deleteResidentById(String id) {
        Iterator<ResidentRow> it = residents.iterator();
        while (it.hasNext()) {
            if (it.next().residentID.equalsIgnoreCase(id)) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    public static List<ResidentRow> searchResidents(String keyword, String sex, String purok) {
        String k = keyword == null ? "" : keyword.trim().toLowerCase();
        String sx = sex == null ? "All" : sex;
        String pr = purok == null ? "All" : purok;

        List<ResidentRow> out = new ArrayList<>();
        for (ResidentRow r : residents) {
            boolean ok = true;

            if (!k.isEmpty()) {
                String hay = (r.residentID + " " + r.firstName + " " + r.lastName + " " + r.address).toLowerCase();
                ok = hay.contains(k);
            }

            if (ok && !"All".equalsIgnoreCase(sx)) {
                ok = r.sex.equalsIgnoreCase(sx);
            }

            if (ok && !"All".equalsIgnoreCase(pr)) {
                ok = r.address.toLowerCase().contains(("purok " + pr).toLowerCase());
            }

            if (ok) out.add(r);
        }
        return out;
    }

    public static DefaultTableModel residentsTableModel(List<ResidentRow> list) {
        String[] cols = {"Resident ID","Last Name","First Name","M.I.","Sex","Birthday","Contact","Address","Marital","Employment","Occupation"};
        DefaultTableModel m = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        for (ResidentRow r : list) {
            m.addRow(new Object[]{
                    r.residentID, r.lastName, r.firstName, r.middleInitial, r.sex, r.birthday,
                    r.contactNumber, r.address, r.maritalStatus, r.employment, r.occupation
            });
        }
        return m;
    }

    public static List<UserRow> getUsers() {
        return users;
    }

    public static boolean removeUser(String username) {
        Iterator<UserRow> it = users.iterator();
        while (it.hasNext()) {
            if (it.next().username.equalsIgnoreCase(username)) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    public static DefaultTableModel usersTableModel() {
        String[] cols = {"Username","Role","Email","Status"};
        DefaultTableModel m = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        for (UserRow u : users) {
            m.addRow(new Object[]{u.username, u.role, u.email, u.status});
        }
        return m;
    }

    public static void addLoginHistory(String username, String result) {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        loginHistory.add(new String[]{LocalDateTime.now().format(f), username, result, "Desktop"});
    }

    public static DefaultTableModel loginHistoryModel() {
        String[] cols = {"Time","Username","Result","Device"};
        DefaultTableModel m = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        for (String[] row : loginHistory) m.addRow(row);
        return m;
    }

    // ---------- Reports ----------
    public static int totalResidents() {
        return residents.size();
    }

    public static Map<String,Integer> genderSummary() {
        Map<String,Integer> map = new LinkedHashMap<>();
        map.put("Male", 0);
        map.put("Female", 0);
        for (ResidentRow r : residents) {
            if ("female".equalsIgnoreCase(r.sex)) map.put("Female", map.get("Female")+1);
            else map.put("Male", map.get("Male")+1);
        }
        return map;
    }

    public static int householdCountApprox() {
        // dummy: count unique address "Purok X"
        Set<String> s = new HashSet<>();
        for (ResidentRow r : residents) {
            String lower = r.address.toLowerCase();
            if (lower.contains("purok 1")) s.add("Purok 1");
            else if (lower.contains("purok 2")) s.add("Purok 2");
            else if (lower.contains("purok 3")) s.add("Purok 3");
            else s.add("Other");
        }
        return s.size() * 12; // fake multiplier
    }

    public static Map<String,Integer> ageGroupSummary() {
        Map<String,Integer> map = new LinkedHashMap<>();
        map.put("0-17", 0);
        map.put("18-30", 0);
        map.put("31-59", 0);
        map.put("60+", 0);

        for (ResidentRow r : residents) {
            int age = calcAge(r.birthday);
            if (age <= 17) map.put("0-17", map.get("0-17")+1);
            else if (age <= 30) map.put("18-30", map.get("18-30")+1);
            else if (age <= 59) map.put("31-59", map.get("31-59")+1);
            else map.put("60+", map.get("60+")+1);
        }
        return map;
    }

    private static int calcAge(String birth) {
        try {
            String[] p = birth.split("-");
            int y = Integer.parseInt(p[0]);
            int m = Integer.parseInt(p[1]);
            int d = Integer.parseInt(p[2]);
            java.time.LocalDate b = java.time.LocalDate.of(y,m,d);
            return java.time.Period.between(b, java.time.LocalDate.now()).getYears();
        } catch (Exception e) {
            return 0;
        }
    }
}

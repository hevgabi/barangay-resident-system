package services.format;

import models.Resident;

public class ResidentRowFormatter {

    public String[] headers() {
        return new String[] {
                "Resident ID","Last Name","First Name","M.I.","Sex",
                "Birthday","Contact","Address","Marital","Employment","Occupation"
        };
    }

    public Object[] toRow(Resident r) {
        return new Object[] {
                safe(r.getResidentId()),
                safe(r.getLastName()),
                safe(r.getFirstName()),
                mi(r.getMiddleName()),
                safe(r.getSex()),
                (r.getDateOfBirth() == null) ? "" : r.getDateOfBirth().toString(),
                safe(r.getContactNumber()),
                safe(r.getAddress()),
                safe(r.getMaritalStatus()),
                safe(r.getEmploymentStatus()),
                safe(r.getOccupation())
        };
    }

    private String safe(String s) {
        return (s == null) ? "" : s.trim();
    }

    private String mi(String middle) {
        if (middle == null) return "";
        String t = middle.trim();
        return t.isEmpty() ? "" : t.substring(0, 1).toUpperCase();
    }
}

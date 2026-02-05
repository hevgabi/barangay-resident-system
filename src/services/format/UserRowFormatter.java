package services.format;

import models.User;
import java.time.LocalDateTime;

public class UserRowFormatter implements RowFormatter<User> {

    @Override
    public String[] headers() {
        return new String[] {
                "Username",
                "Full Name",
                "Role",
                "Email",
                "Active",
                "Created At",
                "Last Login"
        };
    }

    @Override
    public String[] toRow(User u) {
        return new String[] {
                safe(u.getUsername()),
                safe(u.getFullName()),
                safe(u.getRole()),
                safe(u.getEmail()),
                String.valueOf(u.isActive()),
                safeDateTime(u.getCreatedAt()),
                safeDateTime(u.getLastLoginAt())
        };
    }

    private String safe(String s) {
        return (s == null) ? "" : s.trim();
    }

    private String safeDateTime(LocalDateTime dt) {
        return (dt == null) ? "" : dt.toString();
    }
}

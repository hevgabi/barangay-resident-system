package models;

import java.time.LocalDateTime;

public class LoginRecord {
    private final LocalDateTime at;
    private final String username;
    private final String role;
    private final String status; // SUCCESS / FAIL
    private final String note;   // optional message

    public LoginRecord(LocalDateTime at, String username, String role, String status, String note) {
        this.at = at;
        this.username = username;
        this.role = role;
        this.status = status;
        this.note = note;
    }

    public LocalDateTime getAt() { return at; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public String getStatus() { return status; }
    public String getNote() { return note; }
}

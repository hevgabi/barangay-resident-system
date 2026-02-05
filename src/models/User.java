package models;

import java.time.LocalDateTime;

public class User extends BaseEntity {

    private String userId;        // U00001, U00002, ...
    private String username;
    private String passwordHash;
    private String role;
    private String fullName;
    private String residentId;    // R00002 (link only)
    private String email;
    private boolean isActive;
    private LocalDateTime lastLoginAt;

    // Constructor for NEW users (userId will be set by UserRepo)
    public User(String username, String passwordHash, String email, String role) {
        this.userId = "";
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.role = role;
        this.fullName = "";
        this.residentId = "";
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.lastLoginAt = null;
    }

    // Constructor for loading from TXT
    public User(
            String userId,
            String username,
            String passwordHash,
            String email,
            String fullName,
            String role,
            boolean isActive,
            String residentId,
            LocalDateTime createdAt,
            LocalDateTime lastLoginAt
    ) {
        this.userId = userId == null ? "" : userId.trim();
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.fullName = (fullName == null) ? "" : fullName;
        this.role = role;
        this.isActive = isActive;
        this.residentId = (residentId == null) ? "" : residentId.trim();
        this.createdAt = (createdAt == null) ? LocalDateTime.now() : createdAt;
        this.lastLoginAt = lastLoginAt;
    }

    // setters
    public void setUserId(String userId) {
        this.userId = (userId == null) ? "" : userId.trim();
    }

    public void setUsername(String username) { this.username = username; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setRole(String role) { this.role = role; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }

    public void setResidentId(String residentId) {
        this.residentId = (residentId == null) ? "" : residentId.trim();
    }

    public void setActive(boolean active) { isActive = active; }

    public void setLastLoginAt(LocalDateTime dt) {
        this.lastLoginAt = dt;
        this.updatedAt = LocalDateTime.now();
    }

    public void setCreatedAt(LocalDateTime dt) { this.createdAt = dt; }
    public void setUpdatedAt(LocalDateTime dt) { this.updatedAt = dt; }

    // getters
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getRole() { return role; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public boolean isActive() { return isActive; }
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public String getResidentId() { return residentId; }
}

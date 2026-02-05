package models;

import java.time.LocalDateTime;

public class StaffInvite {
    private String code;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private boolean used;
    private String usedBy;
    private LocalDateTime usedAt;

    public StaffInvite(String code, LocalDateTime createdAt, LocalDateTime expiresAt,
                       boolean used, String usedBy, LocalDateTime usedAt) {
        this.code = code;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.used = used;
        this.usedBy = usedBy == null ? "" : usedBy;
        this.usedAt = usedAt;
    }

    public String getCode() { return code; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public boolean isUsed() { return used; }
    public String getUsedBy() { return usedBy; }
    public LocalDateTime getUsedAt() { return usedAt; }

    public void markUsed(String username) {
        this.used = true;
        this.usedBy = (username == null) ? "" : username.trim();
        this.usedAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
}

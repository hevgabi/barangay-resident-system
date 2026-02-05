package repos;

import models.User;
import util.FileMaker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserRepo {

    private final String filePathStr = "data/user.txt";
    private final Path filePath;

    public UserRepo() {
        FileMaker.makeFileIfNotExists(filePathStr);
        this.filePath = Paths.get(filePathStr);
    }

    // U00001, U00002, ...
    public synchronized String generateUserId() {
        int last = 0;

        for (User u : getAll()) {
            String id = u.getUserId();
            if (id == null) continue;
            id = id.trim();
            if (!id.startsWith("U")) continue;

            try {
                int n = Integer.parseInt(id.substring(1));
                if (n > last) last = n;
            } catch (Exception ignore) { }
        }

        return String.format("U%05d", last + 1);
    }

    public synchronized List<User> getAll() {
        List<User> out = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                User u = parse(line);
                if (u != null) out.add(u);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return out;
    }

    public synchronized User findByUsername(String username) {
        if (username == null) return null;
        String key = username.trim();

        for (User u : getAll()) {
            if (u.getUsername() != null && u.getUsername().equalsIgnoreCase(key)) return u;
        }
        return null;
    }

    public synchronized boolean existsUsername(String username) {
        return findByUsername(username) != null;
    }

    public synchronized User findByResidentId(String residentId) {
        if (residentId == null) return null;
        String key = residentId.trim();

        for (User u : getAll()) {
            if (u.getResidentId() != null && u.getResidentId().equalsIgnoreCase(key)) {
                return u;
            }
        }
        return null;
    }

    public synchronized void save(User user) {
        if (user == null) throw new IllegalArgumentException("user is null");

        if (existsUsername(user.getUsername()))
            throw new IllegalStateException("Username already exists");

        if (user.getResidentId() != null && !user.getResidentId().trim().isEmpty()) {
            if (findByResidentId(user.getResidentId()) != null)
                throw new IllegalStateException("Resident already has an account");
        }

        // Assign userId if missing
        if (user.getUserId() == null || user.getUserId().trim().isEmpty()) {
            user.setUserId(generateUserId());
        }

        try (BufferedWriter bw = Files.newBufferedWriter(
                filePath,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        )) {
            bw.write(serialize(user));
            bw.newLine();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save user", e);
        }
    }

    public synchronized void update(User updated) {
        if (updated == null) return;

        List<User> all = getAll();
        boolean found = false;

        for (int i = 0; i < all.size(); i++) {
            User u = all.get(i);
            if (u.getUserId() != null && u.getUserId().equalsIgnoreCase(updated.getUserId())) {
                all.set(i, updated);
                found = true;
                break;
            }
        }

        // fallback: if old data has no userId
        if (!found) {
            for (int i = 0; i < all.size(); i++) {
                User u = all.get(i);
                if (u.getUsername() != null && u.getUsername().equalsIgnoreCase(updated.getUsername())) {
                    all.set(i, updated);
                    found = true;
                    break;
                }
            }
        }

        if (found) rewriteAll(all);
    }

    public synchronized boolean updateRoleByResidentId(String residentId, String newRole) {
        if (residentId == null || newRole == null) return false;

        List<User> all = getAll();
        boolean updated = false;

        for (User u : all) {
            if (u.getResidentId() != null && u.getResidentId().equalsIgnoreCase(residentId.trim())) {
                u.setRole(newRole.trim());
                updated = true;
                break;
            }
        }

        if (updated) rewriteAll(all);
        return updated;
    }

    public synchronized boolean updateLastLoginAt(String username, LocalDateTime dt) {
        if (username == null) return false;

        List<User> all = getAll();
        boolean updated = false;

        for (User u : all) {
            if (u.getUsername() != null && u.getUsername().equalsIgnoreCase(username.trim())) {
                u.setLastLoginAt(dt == null ? LocalDateTime.now() : dt);
                updated = true;
                break;
            }
        }

        if (updated) rewriteAll(all);
        return updated;
    }

    private void rewriteAll(List<User> all) {
        try (BufferedWriter bw = Files.newBufferedWriter(
                filePath,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        )) {
            for (User u : all) {
                bw.write(serialize(u));
                bw.newLine();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to rewrite users file", e);
        }
    }

    private User parse(String line) {
        String[] p = line.split("\\|", -1);

        // Old format:
        // username|passwordHash|role|email
        if (p.length == 4) {
            String username = p[0];
            String passHash = p[1];
            String role = p[2];
            String email = p[3];

            User u = new User(username, passHash, email, role);
            u.setUserId(""); // old data
            return u;
        }

        // New format:
        // userId|username|passwordHash|email|fullName|role|isActive|residentId|createdAt|lastLoginAt
        if (p.length < 10) return null;

        String userId = p[0];
        String username = p[1];
        String passwordHash = p[2];
        String email = p[3];
        String fullName = p[4];
        String role = p[5];
        boolean isActive = parseBool(p[6], true);
        String residentId = p[7];
        LocalDateTime createdAt = parseDateTime(p[8]);
        LocalDateTime lastLoginAt = parseDateTime(p[9]);

        return new User(
                userId,
                username,
                passwordHash,
                email,
                fullName,
                role,
                isActive,
                residentId,
                createdAt,
                lastLoginAt
        );
    }

    private String serialize(User u) {
        return safe(u.getUserId()) + "|" +
                safe(u.getUsername()) + "|" +
                safe(u.getPasswordHash()) + "|" +
                safe(u.getEmail()) + "|" +
                safe(u.getFullName()) + "|" +
                safe(u.getRole()) + "|" +
                u.isActive() + "|" +
                safe(u.getResidentId()) + "|" +
                safe(dtToStr(u.getCreatedAt())) + "|" +
                safe(dtToStr(u.getLastLoginAt()));
    }

    private String safe(String s) {
        return s == null ? "" : s.replace("|", " ");
    }

    private LocalDateTime parseDateTime(String s) {
        try {
            if (s == null || s.isBlank()) return null;
            return LocalDateTime.parse(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private String dtToStr(LocalDateTime dt) {
        return dt == null ? "" : dt.toString();
    }

    private boolean parseBool(String s, boolean def) {
        if (s == null || s.isBlank()) return def;
        return s.trim().equalsIgnoreCase("true");
    }
}

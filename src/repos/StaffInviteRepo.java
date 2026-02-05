package repos;

import models.StaffInvite;
import util.FileMaker;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StaffInviteRepo {

    private final String filePath = "data/staff_invites.txt";

    public StaffInviteRepo() {
        FileMaker.makeFileIfNotExists(filePath);
    }

    public void save(StaffInvite invite) {
        try (PrintWriter out = new PrintWriter(new FileWriter(filePath, true))) {
            out.println(toLine(invite));
        } catch (Exception e) {
            throw new RuntimeException("Failed saving staff invite", e);
        }
    }

    public StaffInvite findByCode(String code) {
        if (code == null || code.trim().isEmpty()) return null;
        String key = code.trim();

        for (StaffInvite inv : getAll()) {
            if (inv.getCode().equalsIgnoreCase(key)) return inv;
        }
        return null;
    }

    public void markUsed(String code, String usedByUsername) {
        List<StaffInvite> all = getAll();
        boolean updated = false;

        for (StaffInvite inv : all) {
            if (inv.getCode().equalsIgnoreCase(code.trim())) {
                inv.markUsed(usedByUsername);
                updated = true;
                break;
            }
        }

        if (!updated) return;

        rewrite(all);
    }

    public List<StaffInvite> getAll() {
        List<StaffInvite> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] p = line.split("\\|", -1);
                if (p.length < 6) continue;

                String code = p[0];
                LocalDateTime createdAt = parseDt(p[1]);
                LocalDateTime expiresAt = parseDt(p[2]);
                boolean used = Boolean.parseBoolean(p[3]);
                String usedBy = p[4];
                LocalDateTime usedAt = parseDt(p[5]);

                list.add(new StaffInvite(code, createdAt, expiresAt, used, usedBy, usedAt));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed reading staff invites", e);
        }

        return list;
    }

    private void rewrite(List<StaffInvite> all) {
        try (PrintWriter out = new PrintWriter(new FileWriter(filePath))) {
            for (StaffInvite inv : all) {
                out.println(toLine(inv));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed rewriting staff invites", e);
        }
    }

    private String toLine(StaffInvite inv) {
        return safe(inv.getCode()) + "|" +
               safeDt(inv.getCreatedAt()) + "|" +
               safeDt(inv.getExpiresAt()) + "|" +
               inv.isUsed() + "|" +
               safe(inv.getUsedBy()) + "|" +
               safeDt(inv.getUsedAt());
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private String safeDt(LocalDateTime dt) {
        return dt == null ? "" : dt.toString();
    }

    private LocalDateTime parseDt(String s) {
        if (s == null || s.isBlank()) return null;
        return LocalDateTime.parse(s);
    }
}

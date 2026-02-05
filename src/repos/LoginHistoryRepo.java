package repos;

import models.LoginRecord;
import util.FileMaker;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LoginHistoryRepo {

    private final String filePath = "data/login_history.txt";

    public LoginHistoryRepo() {
        FileMaker.makeFileIfNotExists(filePath);
    }

    // format: datetime|username|role|status|note
    public void append(LoginRecord r) {
        try (PrintWriter out = new PrintWriter(new FileWriter(filePath, true))) {
            out.println(toLine(r));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<LoginRecord> getAll() {
        List<LoginRecord> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] p = line.split("\\|", -1);
                if (p.length < 5) continue;

                LocalDateTime at = LocalDateTime.parse(p[0]);
                list.add(new LoginRecord(at, p[1], p[2], p[3], p[4]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private String toLine(LoginRecord r) {
        return safe(r.getAt()) + "|" +
               safe(r.getUsername()) + "|" +
               safe(r.getRole()) + "|" +
               safe(r.getStatus()) + "|" +
               safe(r.getNote());
    }

    private String safe(Object o) {
        return o == null ? "" : o.toString().replace("|", "/");
    }
}

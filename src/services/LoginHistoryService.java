package services;

import util.FileMaker;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoginHistoryService {

    private final String path = "data/login_history.txt";
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public LoginHistoryService() {
        FileMaker.makeFileIfNotExists(path);
    }

    public void logSuccess(String username, String role, String device) {
        append(now(), username, "SUCCESS", device);
    }

    public void logFail(String username, String note, String device) {
        // note hindi kasama sa file format mo, so pwede natin ilagay sa device column or ignore.
        // dito nilalagay ko note sa dulo ng device para may context.
        String d = device;
        if (note != null && !note.isBlank()) d = device + " (" + note + ")";
        append(now(), username, "FAIL", d);
    }

    public String getDefaultDeviceLabel() {
        // simple device label
        String os = System.getProperty("os.name");
        String user = System.getProperty("user.name");
        return os + " - " + user;
    }

    private String now() {
        return LocalDateTime.now().format(fmt);
    }

    private void append(String time, String username, String result, String device) {
        try (PrintWriter out = new PrintWriter(new FileWriter(path, true))) {
            out.println(safe(time) + "|" + safe(username) + "|" + safe(result) + "|" + safe(device));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String safe(String s) {
        if (s == null) return "";
        return s.replace("|", "/");
    }
}

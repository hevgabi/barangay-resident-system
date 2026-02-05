package util;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailSender {

    public static void sendOtp(String toEmail, String otp) throws Exception {
        // Gmail SMTP TLS settings :contentReference[oaicite:4]{index=4}
        String host = "smtp.gmail.com";
        String port = "587";

        // NOTE: Use an App Password (needs 2-Step Verification) :contentReference[oaicite:5]{index=5}
        final String fromEmail = "kilalamoako98@gmail.com";
        final String appPassword = "kfbzwtlhocrjigeo"; // app password
        // do not use normal password

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, appPassword);
            }
        });

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(fromEmail));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        msg.setSubject("Your OTP Code For Barangay Resident System");
        msg.setText("Your OTP is: " + otp + "\n\nThis code expires soon.");

        Transport.send(msg);
    }
}

package util;

import java.security.SecureRandom;

public class OTPHelper {

    private static final String DIGITS = "0123456789";
    private static final int OTP_LENGTH = 6;
    private static final SecureRandom random = new SecureRandom();

    private OTPHelper() {
        // utility class
    }

    public static String generateOTP() {
        StringBuilder otp = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            int index = random.nextInt(DIGITS.length());
            otp.append(DIGITS.charAt(index));
        }
        return otp.toString();
    }

    public static boolean verifyOTP(String inputOTP, String actualOTP) {
        if (inputOTP == null || actualOTP == null) return false;
        return inputOTP.trim().equals(actualOTP.trim());
    }

    public static void sendOTP(String toEmail, String otp) {
    if (toEmail == null || toEmail.trim().isEmpty()) {
        throw new IllegalArgumentException("Recipient email is empty.");
    }
    if (otp == null || otp.trim().isEmpty()) {
        throw new IllegalArgumentException("OTP is empty.");
    }

    try {
        EmailSender.sendOtp(toEmail.trim(), otp.trim());
    } catch (Exception e) {
        throw new RuntimeException("Failed to send OTP email", e);
    }
}

}

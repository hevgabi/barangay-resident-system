package services.auth;

import java.time.LocalDate;

public class SignupRequest {

    public String residentId;
    public LocalDate dateOfBirth;

    public String username;
    public String password;
    public String email;

    public SignupRequest(
            String residentId,
            LocalDate dateOfBirth,
            String username,
            String password,
            String email
    ) {
        this.residentId = residentId;
        this.dateOfBirth = dateOfBirth;
        this.username = username;
        this.password = password;
        this.email = email;
    }
}

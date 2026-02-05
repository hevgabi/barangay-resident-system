package services.auth;

public class StaffSignupRequest {
    public String inviteCode;
    public String fullName;
    public String username;
    public String password;
    public String email;

    public StaffSignupRequest(String inviteCode, String fullName, String username, String password, String email) {
        this.inviteCode = inviteCode;
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.email = email;
    }
}

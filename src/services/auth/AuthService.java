package services.auth;

import models.Resident;
import models.StaffInvite;
import models.User;
import repos.StaffInviteRepo;
import repos.UserRepo;
import services.ResidentService;
import services.common.ServiceResult;
import util.OTPHelper;
import util.PasswordHasher;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AuthService {

    private final UserRepo userRepo;
    private final ResidentService residentService;

    private final StaffInviteRepo staffInviteRepo = new StaffInviteRepo();
    private static final SecureRandom inviteRand = new SecureRandom();

    private User pendingUser;
    private String currentOtp;

    public AuthService(UserRepo userRepo, ResidentService residentService) {
        this.userRepo = userRepo;
        this.residentService = residentService;
    }

    public ServiceResult<Void> signupResident(SignupRequest req) {

        if (req == null ||
                isBlank(req.residentId) ||
                req.dateOfBirth == null ||
                isBlank(req.username) ||
                isBlank(req.password) ||
                isBlank(req.email)) {

            return ServiceResult.fail("All fields are required.");
        }

        String residentId = req.residentId.trim();
        String username = req.username.trim();
        String email = req.email.trim();

        if (userRepo.existsUsername(username)) {
            return ServiceResult.fail("Username already exists.");
        }

        ServiceResult<Resident> rr = residentService.findById(residentId);
        if (!rr.isSuccess() || rr.getData() == null) {
            return ServiceResult.fail("Resident ID not found.");
        }

        Resident resident = rr.getData();

        LocalDate dob = resident.getDateOfBirth();
        if (dob == null || !dob.equals(req.dateOfBirth)) {
            return ServiceResult.fail("Date of Birth does not match our record.");
        }

        if (userRepo.findByResidentId(residentId) != null) {
            return ServiceResult.fail("This resident already has an account.");
        }

        String hash = PasswordHasher.hash(req.password.trim());

        User user = new User(username, hash, email, "RESIDENT");
        user.setResidentId(residentId);

        String fullName = buildFullName(resident);
        user.setFullName(fullName);

        userRepo.save(user);

        String otp = OTPHelper.generateOTP();
        OTPHelper.sendOTP(user.getEmail(), otp);

        pendingUser = user;
        currentOtp = otp;

        return ServiceResult.ok(null, "OTP sent to your email.");
    }

    public ServiceResult<Void> login(String username, String password) {

        if (isBlank(username) || isBlank(password)) {
            return ServiceResult.fail("Enter username and password.");
        }

        User user = userRepo.findByUsername(username.trim());
        if (user == null) {
            return ServiceResult.fail("User not found.");
        }

        if (!user.isActive()) {
            return ServiceResult.fail("Account is disabled.");
        }

        if (!PasswordHasher.matches(password.trim(), user.getPasswordHash())) {
            return ServiceResult.fail("Wrong password.");
        }

        String otp = OTPHelper.generateOTP();
        OTPHelper.sendOTP(user.getEmail(), otp);

        pendingUser = user;
        currentOtp = otp;

        return ServiceResult.ok(null, "OTP sent to your email.");
    }

    public ServiceResult<User> verifyOtp(String input) {

        if (pendingUser == null || currentOtp == null) {
            return ServiceResult.fail("No pending verification.");
        }

        if (isBlank(input) || !OTPHelper.verifyOTP(input, currentOtp)) {
            return ServiceResult.fail("Invalid OTP.");
        }

        pendingUser.setLastLoginAt(LocalDateTime.now());
        userRepo.update(pendingUser);

        User loggedIn = pendingUser;

        pendingUser = null;
        currentOtp = null;

        return ServiceResult.ok(loggedIn, "Login successful.");
    }

    public ServiceResult<Void> resendOtp() {
        if (pendingUser == null) {
            return ServiceResult.fail("No pending user.");
        }

        String otp = OTPHelper.generateOTP();
        OTPHelper.sendOTP(pendingUser.getEmail(), otp);
        currentOtp = otp;

        return ServiceResult.ok(null, "OTP resent.");
    }

    public ServiceResult<String> generateStaffInvite(User currentUser, int minutesValid) {
        if (currentUser == null || !"STAFF".equalsIgnoreCase(currentUser.getRole())) {
            return ServiceResult.fail("Unauthorized. Staff only.");
        }
        if (minutesValid <= 0)
            minutesValid = 15;

        String code = "INV-" + (100000 + inviteRand.nextInt(900000));
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime exp = now.plusMinutes(minutesValid);

        StaffInvite inv = new StaffInvite(code, now, exp, false, "", null);
        staffInviteRepo.save(inv);

        return ServiceResult.ok(code, "Invite generated.");
    }

    public ServiceResult<Void> signupStaffWithInvite(StaffSignupRequest req) {

        if (req == null ||
                isBlank(req.inviteCode) ||
                isBlank(req.username) ||
                isBlank(req.password) ||
                isBlank(req.email)) {
            return ServiceResult.fail("All fields are required.");
        }

        boolean hasStaff = false;
        for (User u : userRepo.getAll()) {
            if ("STAFF".equalsIgnoreCase(u.getRole())) {
                hasStaff = true;
                break;
            }
        }

        String code = req.inviteCode.trim();

        if (!hasStaff && "BRGY-SETUP-2026".equalsIgnoreCase(code)) {

            if (userRepo.existsUsername(req.username.trim())) {
                return ServiceResult.fail("Username already exists.");
            }

            String hash = PasswordHasher.hash(req.password.trim());
            User user = new User(req.username.trim(), hash, req.email.trim(), "STAFF");
            user.setFullName(req.fullName == null ? "" : req.fullName.trim());

            userRepo.save(user);

            String otp = OTPHelper.generateOTP();
            OTPHelper.sendOTP(user.getEmail(), otp);

            pendingUser = user;
            currentOtp = otp;

            return ServiceResult.ok(null, "First staff created. OTP sent to staff email.");
        }

        StaffInvite inv = staffInviteRepo.findByCode(code);
        if (inv == null)
            return ServiceResult.fail("Invalid invite code.");
        if (inv.isUsed())
            return ServiceResult.fail("Invite code already used.");
        if (inv.isExpired())
            return ServiceResult.fail("Invite code expired.");

        if (userRepo.existsUsername(req.username.trim())) {
            return ServiceResult.fail("Username already exists.");
        }

        String hash = PasswordHasher.hash(req.password.trim());
        User user = new User(req.username.trim(), hash, req.email.trim(), "STAFF");
        user.setFullName(req.fullName == null ? "" : req.fullName.trim());

        userRepo.save(user);

        staffInviteRepo.markUsed(inv.getCode(), user.getUsername());

        String otp = OTPHelper.generateOTP();
        OTPHelper.sendOTP(user.getEmail(), otp);

        pendingUser = user;
        currentOtp = otp;

        return ServiceResult.ok(null, "OTP sent to staff email.");
    }

    public ServiceResult<Void> promoteResidentToStaff(User currentUser, String residentId) {
        if (currentUser == null || !"STAFF".equalsIgnoreCase(currentUser.getRole())) {
            return ServiceResult.fail("Unauthorized. Staff only.");
        }
        if (isBlank(residentId)) {
            return ServiceResult.fail("Resident ID is required.");
        }

        User u = userRepo.findByResidentId(residentId.trim());
        if (u == null) {
            return ServiceResult.fail("No account found for this Resident ID.");
        }

        if ("STAFF".equalsIgnoreCase(u.getRole())) {
            return ServiceResult.ok(null, "User is already STAFF.");
        }

        u.setRole("STAFF");
        userRepo.update(u);

        return ServiceResult.ok(null, "Resident promoted to STAFF.");
    }

    public List<User> getAllUsers() {
        return userRepo.getAll();
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String buildFullName(Resident r) {
        String fn = safe(r.getFirstName());
        String mn = safe(r.getMiddleName());
        String ln = safe(r.getLastName());

        String name = (fn + " " + mn + " " + ln).trim().replaceAll("\\s+", " ");
        return name.isBlank() ? "RESIDENT-" + safe(r.getResidentId()) : name;
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }
}

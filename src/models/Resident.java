package models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

public class Resident extends BaseEntity {

    private String residentId;
    private String firstName;
    private String middleName;
    private String lastName;

    private String sex;
    private LocalDate dateOfBirth;
    private String contactNumber;

    private String address;
    private String maritalStatus;
    private String employmentStatus;
    private String occupation;
    private String incomeBracket;
    private String religion;
    private String motherTongue;

    private String medicalCondition;
    private String position;

    private String status; // ACTIVE / MOVED / DECEASED

    public Resident(
            String residentId,
            String firstName,
            String middleName,
            String lastName,
            String sex,
            LocalDate dateOfBirth,
            String contactNumber,
            String address,
            String maritalStatus,
            String employmentStatus,
            String occupation,
            String incomeBracket,
            String religion,
            String motherTongue,
            String medicalCondition,
            String position,
            String status,
            LocalDateTime dateRegistered,
            LocalDateTime lastUpdated
    ) {
        this.residentId = residentId;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;

        this.sex = sex;
        this.dateOfBirth = dateOfBirth;
        this.contactNumber = contactNumber;

        this.address = address;
        this.maritalStatus = maritalStatus;
        this.employmentStatus = employmentStatus;
        this.occupation = occupation;
        this.incomeBracket = incomeBracket;
        this.religion = religion;
        this.motherTongue = motherTongue;

        this.medicalCondition = medicalCondition;
        this.position = position;

        this.status = (status == null || status.isBlank()) ? "ACTIVE" : status;

        // IMPORTANT: map to BaseEntity
        this.createdAt = (dateRegistered == null) ? LocalDateTime.now() : dateRegistered;
        this.updatedAt = (lastUpdated == null) ? this.createdAt : lastUpdated;
    }

    public int getAge() {
        if (dateOfBirth == null) return 0;
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    public String getResidentId() { return residentId; }
    public void setResidentId(String residentId) { this.residentId = residentId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getMaritalStatus() { return maritalStatus; }
    public void setMaritalStatus(String maritalStatus) { this.maritalStatus = maritalStatus; }

    public String getEmploymentStatus() { return employmentStatus; }
    public void setEmploymentStatus(String employmentStatus) { this.employmentStatus = employmentStatus; }

    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }

    public String getIncomeBracket() { return incomeBracket; }
    public void setIncomeBracket(String incomeBracket) { this.incomeBracket = incomeBracket; }

    public String getReligion() { return religion; }
    public void setReligion(String religion) { this.religion = religion; }

    public String getMotherTongue() { return motherTongue; }
    public void setMotherTongue(String motherTongue) { this.motherTongue = motherTongue; }

    public String getMedicalCondition() { return medicalCondition; }
    public void setMedicalCondition(String medicalCondition) { this.medicalCondition = medicalCondition; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // Bridge names for repo compatibility
    public LocalDateTime getDateRegistered() { return createdAt; }
    public void setDateRegistered(LocalDateTime dt) { this.createdAt = dt; }

    public LocalDateTime getLastUpdated() { return updatedAt; }
    public void setLastUpdated(LocalDateTime dt) { this.updatedAt = dt; }
}

package services;

import models.Resident;
import repos.ResidentRepo;
import services.common.ServiceResult;
import services.common.ValidationError;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ResidentService {

    private final ResidentRepo repo;

    public ResidentService(ResidentRepo repo) {
        this.repo = repo;
    }

    public ServiceResult<List<Resident>> getAll() {
        List<Resident> all = repo.getAll();
        if (all == null)
            all = new ArrayList<>();
        return ServiceResult.ok(all, "OK");
    }

    public ServiceResult<List<Resident>> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAll();
        }
        List<Resident> res = repo.search(keyword.trim());
        if (res == null)
            res = new ArrayList<>();
        return ServiceResult.ok(res, "OK");
    }

    public ServiceResult<Resident> findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return ServiceResult.fail("Resident ID is required.");
        }
        Resident r = repo.findById(id.trim());
        if (r == null)
            return ServiceResult.fail("Resident not found.");
        return ServiceResult.ok(r, "OK");
    }

    public ServiceResult<Resident> create(Resident input) {
        List<ValidationError> errors = new ArrayList<>();
        if (input == null)
            errors.add(new ValidationError("resident", "Resident is required."));
        if (input != null && input.getResidentId() != null && !input.getResidentId().trim().isEmpty())
            errors.add(new ValidationError("residentId", "Resident ID must not be set on create."));

        if (input != null)
            validateCommon(input, errors);

        if (!errors.isEmpty())
            return ServiceResult.fail("Validation failed.", errors);

        String newId = repo.generateResidentId();
        LocalDateTime now = LocalDateTime.now();

        input.setResidentId(newId);
        input.setDateRegistered(now);
        input.setLastUpdated(now);

        repo.addResident(input);
        return ServiceResult.ok(input, "Resident created.");
    }

    public ServiceResult<Resident> update(Resident input) {
        List<ValidationError> errors = new ArrayList<>();
        if (input == null)
            errors.add(new ValidationError("resident", "Resident is required."));
        if (input != null && (input.getResidentId() == null || input.getResidentId().trim().isEmpty()))
            errors.add(new ValidationError("residentId", "Resident ID is required for update."));

        if (input != null)
            validateCommon(input, errors);

        if (!errors.isEmpty())
            return ServiceResult.fail("Validation failed.", errors);

        Resident existing = repo.findById(input.getResidentId().trim());
        if (existing == null)
            return ServiceResult.fail("Resident not found.");

        input.setDateRegistered(existing.getDateRegistered());
        input.setLastUpdated(LocalDateTime.now());

        repo.update(input);
        return ServiceResult.ok(input, "Resident updated.");
    }

    public ServiceResult<Void> deleteById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return ServiceResult.fail("Resident ID is required.");
        }
        Resident existing = repo.findById(id.trim());
        if (existing == null)
            return ServiceResult.fail("Resident not found.");

        repo.deleteById(id.trim());
        return ServiceResult.ok(null, "Resident deleted.");
    }

    private void validateCommon(Resident r, List<ValidationError> errors) {
        if (blank(r.getFirstName()))
            errors.add(new ValidationError("firstName", "First name is required."));
        if (blank(r.getLastName()))
            errors.add(new ValidationError("lastName", "Last name is required."));
        if (blank(r.getSex()))
            errors.add(new ValidationError("sex", "Sex is required."));

        LocalDate dob = r.getDateOfBirth();
        if (dob == null)
            errors.add(new ValidationError("dateOfBirth", "Birthday is required."));
        else if (dob.isAfter(LocalDate.now()))
            errors.add(new ValidationError("dateOfBirth", "Birthday cannot be in the future."));

        if (blank(r.getAddress()))
            errors.add(new ValidationError("address", "Address is required."));

        if (!blank(r.getContactNumber())) {
            String digits = r.getContactNumber().trim().replaceAll("\\s+", "");
            if (!digits.matches("\\d+"))
                errors.add(new ValidationError("contactNumber", "Contact must be digits only."));
        }

        if (blank(r.getStatus()))
            r.setStatus("ACTIVE");
    }

    private boolean blank(String s) {
        return s == null || s.trim().isEmpty();
    }

}

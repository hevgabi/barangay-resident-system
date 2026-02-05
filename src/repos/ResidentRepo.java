package repos;

import util.FileMaker;
import models.Resident;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ResidentRepo {
    private final String path = "data/resident.txt";

    public ResidentRepo() {
        FileMaker.makeFileIfNotExists(path);
    }

    public String generateResidentId() {
        int lastNumber = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] parts = line.split("\\|");
                if (parts.length == 0) continue;

                String id = parts[0];
                if (id == null || !id.startsWith("R")) continue;

                try {
                    int num = Integer.parseInt(id.substring(1));
                    if (num > lastNumber) lastNumber = num;
                } catch (NumberFormatException e) {
                    // ignore bad id format
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        int nextNumber = lastNumber + 1;
        return String.format("R%05d", nextNumber);
    }

    public Resident addResident(Resident r) {
        // ✅ safety: require residentId (or generate if you want)
        if (r.getResidentId() == null || r.getResidentId().isBlank()) {
            // Option 1: reject
            throw new IllegalArgumentException("Resident ID is required.");
            // Option 2: auto-generate (if you prefer)
            // r.setResidentId(generateResidentId());
        }

        LocalDateTime now = LocalDateTime.now();
        if (r.getStatus() == null || r.getStatus().isBlank())
            r.setStatus("ACTIVE");
        if (r.getDateRegistered() == null)
            r.setDateRegistered(now);
        r.setLastUpdated(now);

        String line = toPipeLine(r);

        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(path, true)))) {
            writer.println(line);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return r;
    }

    private String toPipeLine(Resident r) {
        return String.join("|",
                safe(r.getResidentId()),
                safe(r.getFirstName()),
                safe(r.getMiddleName()),
                safe(r.getLastName()),
                safe(r.getSex()),
                r.getDateOfBirth() == null ? "" : r.getDateOfBirth().toString(),
                safe(r.getContactNumber()),
                safe(r.getAddress()),
                safe(r.getMaritalStatus()),
                safe(r.getEmploymentStatus()),
                safe(r.getOccupation()),
                safe(r.getIncomeBracket()),
                safe(r.getReligion()),
                safe(r.getMotherTongue()),
                safe(r.getMedicalCondition()),
                safe(r.getPosition()),
                safe(r.getStatus()),
                r.getDateRegistered() == null ? "" : r.getDateRegistered().toString(),
                r.getLastUpdated() == null ? "" : r.getLastUpdated().toString());
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }

    public List<Resident> getAll() {
        List<Resident> list = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] p = line.split("\\|", -1);
                if (p.length < 19) continue;

                Resident r = new Resident(
                        p[0], p[1], p[2], p[3], p[4],
                        p[5].isBlank() ? null : LocalDate.parse(p[5]),
                        p[6], p[7], p[8], p[9], p[10], p[11], p[12], p[13], p[14],
                        p[15], p[16],
                        p[17].isBlank() ? null : LocalDateTime.parse(p[17]),
                        p[18].isBlank() ? null : LocalDateTime.parse(p[18])
                );

                list.add(r);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    public Resident findById(String residentId) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] p = line.split("\\|", -1);
                if (p.length < 19) continue;

                if (!p[0].equals(residentId)) continue;

                return new Resident(
                        p[0], p[1], p[2], p[3], p[4],
                        p[5].isBlank() ? null : LocalDate.parse(p[5]),
                        p[6], p[7], p[8], p[9], p[10], p[11], p[12], p[13], p[14],
                        p[15], p[16],
                        p[17].isBlank() ? null : LocalDateTime.parse(p[17]),
                        p[18].isBlank() ? null : LocalDateTime.parse(p[18])
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean update(Resident updated) {
        boolean found = false;
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] p = line.split("\\|", -1);
                if (p.length < 19) {
                    lines.add(line);
                    continue;
                }

                if (p[0].equals(updated.getResidentId())) {
                    updated.setLastUpdated(LocalDateTime.now());
                    lines.add(toPipeLine(updated));
                    found = true;
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (!found) return false;

        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(path)))) {
            for (String l : lines) writer.println(l);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean deleteById(String residentId) {
        boolean found = false;
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] p = line.split("\\|", -1);
                if (p.length < 19) {
                    lines.add(line);
                    continue;
                }

                if (p[0].equals(residentId)) {
                    found = true;
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (!found) return false;

        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(path)))) {
            for (String l : lines) writer.println(l);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public List<Resident> search(String keyword) {
        List<Resident> results = new ArrayList<>();
        if (keyword == null) keyword = "";

        String key = keyword.toLowerCase();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] p = line.split("\\|", -1);
                if (p.length < 19) continue;

                String haystack = (p[0] + " " + p[1] + " " + p[2] + " " + p[3] + " " + p[7]).toLowerCase();
                if (!haystack.contains(key)) continue;

                Resident r = new Resident(
                        p[0], p[1], p[2], p[3], p[4],
                        p[5].isBlank() ? null : LocalDate.parse(p[5]),
                        p[6], p[7], p[8], p[9], p[10], p[11], p[12], p[13], p[14],
                        p[15], p[16],
                        p[17].isBlank() ? null : LocalDateTime.parse(p[17]),
                        p[18].isBlank() ? null : LocalDateTime.parse(p[18])
                );

                results.add(r);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return results;
    }
}

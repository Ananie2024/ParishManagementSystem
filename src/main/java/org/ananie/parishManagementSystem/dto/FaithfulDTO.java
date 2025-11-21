package org.ananie.parishManagementSystem.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
public class FaithfulDTO {

    private Long id; // Include ID for read operations

    // --- 1. BASIC PERSONAL INFO ---
    private String firstname; // NEW
    private String name;
    private String fatherName;
    private String motherName;
    private String godparentName;
    private String baptismMinister; // NEW

    // --- 2. DATES & SACRAMENT IDs (All IDs are now String) ---
    private LocalDate dateOfBirth;
    private LocalDate dateOfBaptism;
    private String baptismId; // UPDATED to String
    private LocalDate dateOfFirstCommunion;
    private LocalDate dateOfConfirmation;
    private String confirmationId; // UPDATED to String
    private LocalDate dateOfMatrimony;
    private String matrimonyId; // NEW
    private String spouseName;
    private String spouseBaptismId; // NEW

    // --- 3. ORDINATION ---
    private String level_diaconate;
    private LocalDate date_diaconate;
    private String level_priesthood;
    private LocalDate date_priesthood;
    private String level_episcopate;
    private LocalDate date_episcopate;

    // --- 4. RELIGIOUS PROFESSION ---
    private String congregationName;
    private String hasTemporalProfession;
    private LocalDate dateTemporalProfession;
    private String hasPermanentProfession;
    private LocalDate datePermanentProfession;

    // --- 5. MINISTRY / SERVICE (Uses List of DTOs) ---
    private List<MinistryDTO> ministries; // UPDATED to List<MinistryDTO>
    private String otherMinistryDetails;

    // --- 6. LAPSE HISTORY (Uses List of DTOs) ---
    private List<LapseEventDTO> lapseEvents; // UPDATED to List<LapseEventDTO>

    // --- 7. RELOCATION & DEATH STATUS ---
    private String hasRelocated;
    private String newParishName;
    private String isDeceased;
    private LocalDate dateOfDeath;

    // --- 8. CHURCH TERRITORY ADRESS ---
    private String diocese; // NEW
    private String parish;
    private String subparish;
    private String basicEcclesialCommunity;

    // --- 9. METADATA ---
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ===========================================
    // NESTED DTO FOR MINISTRY
    // ===========================================
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @Data
    public static class MinistryDTO {
        private Long id;
        private String ministryType; // e.g., 'lector', 'catechist'
        // No need for the Faithful reference here to prevent circular references.
    }

    // ===========================================
    // NESTED DTO FOR LAPSE EVENT
    // ===========================================
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @Data
    public static class LapseEventDTO {
        private Long id;
        private String lapseType;
        private LocalDate lapseDate;
        private String lapseReason;
        private LocalDate returnDate;
        // No need for the Faithful reference here.
    }
}
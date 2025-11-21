package org.ananie.parishManagementSystem.dto;

import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
public class CreateFaithfulRequest {

    // --- 1. BASIC PERSONAL INFO ---
    @NotBlank(message = "Baptismal name (firstname) is required")
    @Size(min = 2, max = 100, message = "Baptismal name must be between 2 and 100 characters")
    private String firstname; // Added

    @NotBlank(message = "Family name (name) is required")
    @Size(min = 2, max = 100, message = "Family name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Father's name is required")
    @Size(max = 100, message = "Father's name cannot exceed 100 characters")
    private String fatherName;

    @NotBlank(message = "Mother's name is required")
    @Size(max = 100, message = "Mother's name cannot exceed 100 characters")
    private String motherName;

    @Size(max = 100, message = "Godparent name cannot exceed 100 characters")
    private String godparentName;

    private String baptismMinister; // Added

    // --- 2. DATES & SACRAMENTS IDs ---
    @Past(message = "Date of birth must be in the past")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @PastOrPresent(message = "Baptism date cannot be in the future")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBaptism;

    // Note: If ID comes as string from form, Spring will attempt to convert it.
    private String baptismId; // Changed to String to match typical form input

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfFirstCommunion;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfConfirmation;

    private String confirmationId; // Changed to String to match typical form input

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfMatrimony;
    private String matrimonyId; // Added

    private String spouseName;
    private String spouseBaptismId; // Added (Changed type to String)

    // --- 3. ORDINATION (Priesthood/Diaconate) ---
    // Checkboxes send "true" if checked, which maps well to String/Boolean.
    private String level_diaconate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date_diaconate;

    private String level_priesthood;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date_priesthood;

    private String level_episcopate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date_episcopate;

    // --- 4. RELIGIOUS PROFESSION ---
    private String congregationName; // Added
    private String hasTemporalProfession;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateTemporalProfession;

    private String hasPermanentProfession;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate datePermanentProfession;

    // --- 5. MINISTRY / SERVICE ---
    // List<String> captures multiple checkbox values with the same name="ministry"
    private List<String> ministry; // Added
    private String otherMinistryDetails; // Added

    // --- 6. LAPSE HISTORY (Requires Nested DTO) ---
    private List<LapseEvent> lapseHistory; // Added

    // --- 7. RELOCATION & DEATH STATUS ---
    private String hasRelocated; // Added
    private String newParishName; // Added

    private String isDeceased; // Added
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfDeath; // Added

    // --- 8. CHURCH TERRITORY ADRESS ---
    @NotBlank(message = "Diocese is required")
    private String diocese;

    @NotBlank(message = "Parish is required")
    private String parish;
    private String subparish;
    private String basicEcclesialCommunity;

    // ===========================================
    // NESTED DTO FOR LAPSE HISTORY (Must be Static)
    // ===========================================
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @Data
    public static class LapseEvent {
        // Date when the event (lapse or irregular state) began
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate lapseDate;

        // The type selected in the dropdown
        private String lapseType;

        // Detailed reason/text input
        private String lapseReason;

        // Date when the faithful returned to full communion (can be null)
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate returnDate;
    }
}
package org.ananie.parishManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
@Entity
@Table(name = "faithfuls")
public class Faithful {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- 1. BASIC PERSONAL INFO ---
    @Column(name = "firstname", length = 100) // NEW
    private String firstname;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "father_name", length = 100)
    private String fatherName;

    @Column(name = "mother_name", length = 100)
    private String motherName;

    @Column(name = "godparent_name", length = 100)
    private String godparentName;

    // --- 2. DATES & SACRAMENT IDs (Updated IDs to String) ---
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "date_of_baptism")
    private LocalDate dateOfBaptism;

    @Column(name = "baptism_id", unique = true, length = 50)
    private String baptismId; // UPDATED to String

    @Column(name = "baptism_minister", length = 100)
    private String baptismMinister;

    @Column(name = "date_of_first_communion")
    private LocalDate dateOfFirstCommunion;

    @Column(name = "date_of_confirmation")
    private LocalDate dateOfConfirmation;

    @Column(name = "confirmation_id", unique = true, length = 50)
    private String confirmationId; // UPDATED to String

    @Column(name = "date_of_matrimony")
    private LocalDate dateOfMatrimony;

    @Column(name = "matrimony_id", unique = true, length = 50) // NEW
    private String matrimonyId;

    @Column(name = "spouse_name", length = 100)
    private String spouseName;

    @Column(name = "spouse_baptism_id", length = 50) // UPDATED to String
    private String spouseBaptismId;

    // --- 3. ORDINATION ---
    @Column(name = "level_diaconate") // Checkbox value 'true' or null
    private String level_diaconate;
    @Column(name = "date_diaconate")
    private LocalDate date_diaconate;

    @Column(name = "level_priesthood")
    private String level_priesthood;
    @Column(name = "date_priesthood")
    private LocalDate date_priesthood;

    @Column(name = "level_episcopate")
    private String level_episcopate;
    @Column(name = "date_episcopate")
    private LocalDate date_episcopate;

    // --- 4. RELIGIOUS PROFESSION ---
    @Column(name = "congregation_name", length = 100) // NEW
    private String congregationName;
    @Column(name = "has_temporal_profession")
    private String hasTemporalProfession;
    @Column(name = "date_temporal_profession")
    private LocalDate dateTemporalProfession;
    @Column(name = "has_permanent_profession")
    private String hasPermanentProfession;
    @Column(name = "date_permanent_profession")
    private LocalDate datePermanentProfession;

    // --- 5. MINISTRY / SERVICE (One-to-Many Relationship) ---
    @OneToMany(mappedBy = "faithful", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ministry> ministries; // Replaces List<String>

    @Column(name = "other_ministry_details", length = 255)
    private String otherMinistryDetails;

    // --- 6. LAPSE HISTORY (One-to-Many Relationship) ---
    @OneToMany(mappedBy = "faithful", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LapseEvent> lapseEvents; // Replaces List<LapseEvent> DTO

    // --- 7. RELOCATION & DEATH STATUS ---
    @Column(name = "has_relocated")
    private String hasRelocated;
    @Column(name = "new_parish_name", length = 100)
    private String newParishName;

    @Column(name = "is_deceased")
    private String isDeceased;
    @Column(name = "date_of_death")
    private LocalDate dateOfDeath;

    // --- 8. CHURCH TERRITORY ADRESS ---
    @Column(name = "diocese", length = 50)
    private String diocese;

    @Column(name = "parish", length = 100)
    private String parish;

    @Column(name = "subparish", length = 100)
    private String subparish;

    @Column(name = "basic_ecclesial_community", length = 100)
    private String basicEcclesialCommunity;

    // --- 9. METADATA ---
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
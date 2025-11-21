package org.ananie.parishManagementSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for providing essential faithful's sacramental information to clients.
 * Contains personal details and all sacrament-related data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaithfulSacramentInfoDTO {

    // --- PERSONAL INFORMATION ---
    private String firstname;
    private String name;
    private String fatherName;
    private String motherName;
    private String godparentName;
    private LocalDate dateOfBirth;

    // --- ADDRESS INFORMATION ---
    private String diocese;
    private String parish;
    private String subparish;
    private String basicEcclesialCommunity;

    // --- BAPTISM ---
    private LocalDate dateOfBaptism;
    private String baptismId;
    private String baptismMinister;

    // --- FIRST COMMUNION ---
    private LocalDate dateOfFirstCommunion;

    // --- CONFIRMATION ---
    private LocalDate dateOfConfirmation;
    private String confirmationId;

    // --- MATRIMONY ---
    private LocalDate dateOfMatrimony;
    private String matrimonyId;
    private String spouseName;
    private String spouseBaptismId;

    /**
     * Factory method to create DTO from Faithful entity
     */
    public static FaithfulSacramentInfoDTO fromEntity(org.ananie.parishManagementSystem.entity.Faithful faithful) {
        if (faithful == null) {
            return null;
        }

        return FaithfulSacramentInfoDTO.builder()
                .firstname(faithful.getFirstname())
                .name(faithful.getName())
                .fatherName(faithful.getFatherName())
                .motherName(faithful.getMotherName())
                .godparentName(faithful.getGodparentName())
                .dateOfBirth(faithful.getDateOfBirth())
                .diocese(faithful.getDiocese())
                .parish(faithful.getParish())
                .subparish(faithful.getSubparish())
                .basicEcclesialCommunity(faithful.getBasicEcclesialCommunity())
                .dateOfBaptism(faithful.getDateOfBaptism())
                .baptismId(faithful.getBaptismId())
                .baptismMinister(faithful.getBaptismMinister())
                .dateOfFirstCommunion(faithful.getDateOfFirstCommunion())
                .dateOfConfirmation(faithful.getDateOfConfirmation())
                .confirmationId(faithful.getConfirmationId())
                .dateOfMatrimony(faithful.getDateOfMatrimony())
                .matrimonyId(faithful.getMatrimonyId())
                .spouseName(faithful.getSpouseName())
                .spouseBaptismId(faithful.getSpouseBaptismId())
                .build();
    }
}
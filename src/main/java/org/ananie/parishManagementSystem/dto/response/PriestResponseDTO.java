package org.ananie.parishManagementSystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ananie.parishManagementSystem.utilities.PriestType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PriestResponseDTO {

    private Long id;
    private String names;
    private PriestType priestType;
    private LocalDate ordinationDate;
    private LocalDate birthDate;
    private String parishOfOrign;
    private String email;
    private String phone;
    private String profilePictureUrl;
    private Boolean isAssigned;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

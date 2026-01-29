package org.ananie.parishManagementSystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class IntentionListResponseDTO {

    private Long id;
    private String intentionType;
    private String intentionText;
    private LocalDate requestedDate;
    private Boolean isPaid;
    private String requestorName;
    private Long massId;
    private LocalDate massDate;
}

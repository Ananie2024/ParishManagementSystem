package org.ananie.parishManagementSystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ananie.parishManagementSystem.utilities.IntentionType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class IntentionResponseDTO {

    private Long id;
    private IntentionType intentionType;
    private String intentionText;
    private LocalDate requestedDate;
    private Boolean isPaid;
    private LocalDateTime createdAt;

    // Mass details
    private MassSummaryDTO mass;

    // Faithful/Requestor details
    private String requestorName; // Either from faithful or external name
    private Long faithfulId; // null if external
    private String externalFaithfulName; // null if registered faithful

    // Nested DTO for mass summary
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class MassSummaryDTO {
        private Long id;
        private LocalDate massDate;
        private String massType;
        private String mainCelebrantName;
    }
}

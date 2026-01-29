package org.ananie.parishManagementSystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ananie.parishManagementSystem.utilities.LiturgicalSeason;
import org.ananie.parishManagementSystem.utilities.MassType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MassResponseDTO {

    private Long id;
    private MassType massType;
    private LiturgicalSeason liturgicalSeason;
    private String readings;

    // Event fields
    private LocalDate massDate;
    private String location;

    // Main celebrant details
    private PriestSummaryDTO mainCelebrant;

    // Concelebrants
    private List<PriestSummaryDTO> concelebrants = new ArrayList<>();

    // Intentions
    private List<IntentionSummaryDTO> intentions = new ArrayList<>();

    // Metadata
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Nested DTO for priest summary
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class PriestSummaryDTO {
        private Long id;
        private String names;
        private String priestType;
        private String email;
        private String phone;
        private String profilePictureUrl;
    }

    // Nested DTO for intention summary
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class IntentionSummaryDTO {
        private Long id;
        private String intentionType;
        private String intentionText;
        private Boolean isPaid;
        private String requestorName; // Either faithful name or external name
    }
}

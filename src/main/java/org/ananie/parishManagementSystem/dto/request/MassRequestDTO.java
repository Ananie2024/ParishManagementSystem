package org.ananie.parishManagementSystem.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ananie.parishManagementSystem.utilities.LiturgicalSeason;
import org.ananie.parishManagementSystem.utilities.MassType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MassRequestDTO {

    @NotNull(message = "Mass type is required")
    private MassType massType;

    private LiturgicalSeason liturgicalSeason;

    private String readings;

    @NotNull(message = "Main celebrant is required")
    private Long mainCelebrantId;

    private List<Long> concelebrantIds = new ArrayList<>();

    // Event fields (from parent Event class)
    private LocalDate massDate;

    private String location;
}

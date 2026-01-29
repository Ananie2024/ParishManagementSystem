package org.ananie.parishManagementSystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ananie.parishManagementSystem.utilities.IntentionType;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class IntentionRequestDTO {

    @NotNull(message = "Intention type is required")
    private IntentionType intentionType;

    @NotBlank(message = "Intention text is required")
    private String intentionText;

    private LocalDate requestedDate;

    private Boolean isPaid;

    private Long massId;

    // Either faithfulId OR externalFaithfulName should be provided
    private Long faithfulId;

    private String externalFaithfulName;
}

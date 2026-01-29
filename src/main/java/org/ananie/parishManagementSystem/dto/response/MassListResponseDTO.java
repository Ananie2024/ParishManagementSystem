package org.ananie.parishManagementSystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MassListResponseDTO {

    private Long id;
    private String title;
    private String massType;
    private LocalDate massDate;
    private String location;
    private String mainCelebrantName;
    private Integer concelebrantCount;
    private Integer intentionCount;
    private String liturgicalSeason;
}

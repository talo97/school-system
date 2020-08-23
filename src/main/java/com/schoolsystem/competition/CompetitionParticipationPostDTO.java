package com.schoolsystem.competition;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CompetitionParticipationPostDTO {
    private String description;
    private Long studentId;
    private Long competitionId;
}

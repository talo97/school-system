package com.schoolsystem.competition;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CompetitionParticipationGetDTO {
    private Long id;
    private String descriptionCompetition;
    private String descriptionParticipation;
    private Long studentId;
    private String studentFirstName;
    private String studentLastName;
    private Long competitionId;
    private String competitionName;
}

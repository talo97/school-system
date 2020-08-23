package com.schoolsystem.competition;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CompetitionGetDTO {
    private Long id;
    private String name;
    private String description;
}

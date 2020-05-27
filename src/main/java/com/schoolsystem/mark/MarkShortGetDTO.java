package com.schoolsystem.mark;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MarkShortGetDTO {
    private Long id;
    private EnumGrade enumGrade;
    private String description;
    private Date lastChange;
}

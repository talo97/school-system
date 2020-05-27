package com.schoolsystem.mark;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StudentMarksGetDTO {
    private Long id;
    private String login;
    private String firstName;
    private String lastName;
    private List<MarkShortGetDTO> grades;
}

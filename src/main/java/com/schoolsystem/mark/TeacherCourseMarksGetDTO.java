package com.schoolsystem.mark;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TeacherCourseMarksGetDTO {
    private String course;
    private List<MarkShortGetDTO> grades;
}

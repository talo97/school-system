package com.schoolsystem.report;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Setter
@Getter
public class StudentAverageGradeDTO {
    private String firstName;
    private String lastName;
    private String averageGradeTotal;
    private List<CourseGradeDTO> courseGrades;
}

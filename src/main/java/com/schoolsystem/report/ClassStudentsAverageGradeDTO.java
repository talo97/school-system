package com.schoolsystem.report;

import com.schoolsystem.classes.EnumEducationStage;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ClassStudentsAverageGradeDTO {
    private String className = "";
    private EnumEducationStage educationStage;
    private List<StudentAverageGradeDTO> studentAverageGrades;

}

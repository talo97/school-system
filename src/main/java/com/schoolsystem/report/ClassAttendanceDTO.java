package com.schoolsystem.report;

import com.schoolsystem.classes.EnumEducationStage;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ClassAttendanceDTO {
    private String className = "";
    private EnumEducationStage educationStage;
    private long hoursPerWeek = 0L;
    private long totalHours = 0L;
    private List<StudentTotalAttendanceDTO> students;
}

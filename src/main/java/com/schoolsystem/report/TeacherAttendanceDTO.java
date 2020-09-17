package com.schoolsystem.report;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TeacherAttendanceDTO {
    private String firstName;
    private String lastName;
    private long attendedHours;
    private long totalHours;
    private long hoursPerWeek;
}

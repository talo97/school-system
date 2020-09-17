package com.schoolsystem.report;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StudentTotalAttendanceDTO {
    private String firstName;
    private String lastName;
    private long attendedHours;
}

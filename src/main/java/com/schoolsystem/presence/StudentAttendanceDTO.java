package com.schoolsystem.presence;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentAttendanceDTO {
    private Long studentId;
    private String studentFirstName;
    private String studentLastName;
    private Boolean isPresent;
}

package com.schoolsystem.presence;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClassAttendanceDTO {
    private String studentFirstName;
    private String studentLastName;
    private String studentId;
    List<UserAttendanceDTO> presences;
}

package com.schoolsystem.presence;

import com.schoolsystem.student.EntityStudent;
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
    private Long studentId;
    List<UserAttendanceDTO> attendanceList;

    ClassAttendanceDTO(EntityStudent student, List<UserAttendanceDTO> attendanceList){
        this.studentFirstName = student.getUsers().getFirstName();
        this.studentLastName = student.getUsers().getLastName();
        this.studentId = student.getId();
        this.attendanceList = attendanceList;
    }
}

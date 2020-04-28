package com.schoolsystem.user;

import com.schoolsystem.student.StudentGetDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParentStudentGetDTO {
    private UserGetDTO parent;
    private StudentGetDTO student;
}

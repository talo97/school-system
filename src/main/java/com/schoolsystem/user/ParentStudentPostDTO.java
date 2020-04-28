package com.schoolsystem.user;

import com.schoolsystem.student.StudentPostDTO;
import lombok.Getter;
import lombok.Setter;

//XDDD
@Getter
@Setter
public class ParentStudentPostDTO {
    private UserPostDTO parent;
    private StudentPostDTO student;

    public boolean isEmpty() {
        return parent.isEmpty() || student.isEmpty();
    }
}

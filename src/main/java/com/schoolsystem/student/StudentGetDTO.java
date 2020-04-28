package com.schoolsystem.student;

import com.schoolsystem.user.EnumUserType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StudentGetDTO {
    private String login;
    private String password;
    private String firstName;
    private String lastName;
    private long birthDate = 0;
    private Integer classId;
    private EnumUserType userType;

}

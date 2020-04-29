package com.schoolsystem.student;

import com.schoolsystem.classes.ClassGetDTO;
import com.schoolsystem.user.EnumUserType;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;


@Setter
@Getter
public class StudentGetDTO {
    private Long id;
    private String login;
    private String firstName;
    private String lastName;
    private Date birthDate;
    private ClassGetDTO entityClass;
    private EnumUserType userType;

}

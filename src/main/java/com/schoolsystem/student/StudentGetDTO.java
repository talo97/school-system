package com.schoolsystem.student;

import com.schoolsystem.classes.ClassGetDTO;
import com.schoolsystem.user.EnumUserType;
import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "Date format: yyyy-mm-dd")
    private Date birthDate;
    private ClassGetDTO studentClass;
    private EnumUserType userType;

}

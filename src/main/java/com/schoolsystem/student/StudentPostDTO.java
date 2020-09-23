package com.schoolsystem.student;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Setter
@Getter
public class StudentPostDTO {
    private String login;

    private String password;

    private String firstName;

    private String lastName;
    @ApiModelProperty(value = "Date format: yyyy-mm-dd")
    private Date birthDate;

    private Long classId;
    private String phoneNumber;

    private String email;

    public boolean isEmpty() {
        return login == null || login.isEmpty() || password == null || password.isEmpty() ||
                firstName == null || firstName.isEmpty() || lastName == null || lastName.isEmpty() ||
                classId == null || classId < 0;
    }

}

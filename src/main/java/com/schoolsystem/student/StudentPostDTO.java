package com.schoolsystem.student;

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

    private Date birthDate;

    private Long classId;

    public boolean isEmpty() {
        return login == null || login.isEmpty() || password == null || password.isEmpty() ||
                firstName == null || firstName.isEmpty() || lastName == null || lastName.isEmpty() ||
                classId == null || classId < 0;
    }

}

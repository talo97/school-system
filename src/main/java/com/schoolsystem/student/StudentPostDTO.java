package com.schoolsystem.student;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StudentPostDTO {
    private String login;

    private String password;

    private String firstName;

    private String lastName;

    private long birthDate = 0;

    private Integer classId;

    public boolean isEmpty() {
        return login == null || login.isEmpty() || password == null || password.isEmpty() ||
                firstName == null || firstName.isEmpty() || lastName == null || lastName.isEmpty() ||
                classId == null || classId < 0;
    }

}

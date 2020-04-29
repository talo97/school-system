package com.schoolsystem.user;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Setter
@Getter
public class UserPostDTO {
    private String login;

    private String password;

    private String firstName;

    private String lastName;

    private Date birthDate;

    public boolean isEmpty() {
        return login == null || login.isEmpty() || password == null || password.isEmpty() ||
                firstName == null || firstName.isEmpty() || lastName == null || lastName.isEmpty();
    }

}

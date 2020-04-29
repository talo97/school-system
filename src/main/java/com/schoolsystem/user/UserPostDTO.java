package com.schoolsystem.user;

import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "Date format: yyyy-mm-dd")
    private Date birthDate;

    public boolean isEmpty() {
        return login == null || login.isEmpty() || password == null || password.isEmpty() ||
                firstName == null || firstName.isEmpty() || lastName == null || lastName.isEmpty();
    }

}

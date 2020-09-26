package com.schoolsystem.user;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Setter
@Getter
public class UserPutDTO {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private Date birthDate;
}

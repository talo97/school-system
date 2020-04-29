package com.schoolsystem.user;

import lombok.Getter;
        import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
public class UserGetDTO {
    private Long id;
    private String login;
    private String firstName = "";
    private String lastName= "";
    private Date birthDate;
    private EnumUserType userType;
}

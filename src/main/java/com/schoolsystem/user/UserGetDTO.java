package com.schoolsystem.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserGetDTO {
    private EnumUserType userType;
    private String login;
    private String firstName = "";
    private String lastName= "";
    private long birthDate = 0;
}

package com.schoolsystem.user;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Setter
@Getter
public class StudentWithParentDTO {
    private Long userId;
    private String firstName = "";
    private String lastName= "";
    private String phoneNumber;
    private String email;
    private String parentFirstName = "";
    private String parentLastName= "";
    private Date birthDate;
    private String supervisorName;
    private String supervisorLastName;
    private String className;
}

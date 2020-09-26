package com.schoolsystem.user;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ParentWithStudentDTO {
    private Long userId;
    private String firstName = "";
    private String lastName = "";
    private String phoneNumber;
    private String email;
    private String studentFirstName = "";
    private String studentLastName = "";
}

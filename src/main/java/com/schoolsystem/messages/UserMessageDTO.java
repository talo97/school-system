package com.schoolsystem.messages;

import com.schoolsystem.user.EnumUserType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserMessageDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private EnumUserType userType;
}

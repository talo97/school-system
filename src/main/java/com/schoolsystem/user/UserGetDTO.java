package com.schoolsystem.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
        import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
public class UserGetDTO {
    private Long userId;
    private Long id;
    private String login;
    private String firstName = "";
    private String lastName= "";
    @ApiModelProperty(value = "Date format: yyyy-mm-dd")
    private Date birthDate;
    private EnumUserType userType;
    private Long classId = 0L;
}

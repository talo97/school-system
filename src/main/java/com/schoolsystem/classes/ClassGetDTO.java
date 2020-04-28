package com.schoolsystem.classes;

import com.schoolsystem.user.UserGetDTO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ClassGetDTO {
    private String name;
    private EnumEducationStage enumEducationStage;
    private UserGetDTO supervisor;
}

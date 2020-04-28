package com.schoolsystem.classes;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ClassPostDTO {
    private String name;
    private EnumEducationStage enumEducationStage;
    private int supervisorId;

}

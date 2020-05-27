package com.schoolsystem.classes;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ClassPostDTO {
    private String name;
    private EnumEducationStage enumEducationStage;
    private Long supervisorId;

    public boolean containsEmptyValues() {
        return name == null || name.isEmpty() || enumEducationStage == null || supervisorId == null;
    }

}

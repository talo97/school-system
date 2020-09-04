package com.schoolsystem.mark;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MarkPutDTO {

    private EnumGrade enumGrade;
    private String description;

    public boolean containsEmptyValue() {
        return enumGrade == null;
    }
}

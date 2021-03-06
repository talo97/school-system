package com.schoolsystem.mark;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class MarkPostDTO {
    private EnumGrade enumGrade;
    private String description;
    private Long studentId;
    private Long teacherCourseId;

    public boolean containsEmptyValue() {
        return enumGrade == null || studentId == null ||
                teacherCourseId == null;
    }
}

package com.schoolsystem.mark;

import com.schoolsystem.course.TeacherCourseGetDTO;
import com.schoolsystem.student.StudentGetDTO;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

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

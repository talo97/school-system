package com.schoolsystem.course;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TeacherCoursePostDTO {
    private Long teacherId = 0L;
    private Long courseId = 0L;
}

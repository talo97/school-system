package com.schoolsystem.lesson;

import com.schoolsystem.classes.ClassGetDTO;
import com.schoolsystem.course.TeacherCourseGetDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LessonGetDTO {

    private Long id;

    private EnumDayOfWeek dayOfWeek;

    private EnumLessonNumber lessonNumber;

    private TeacherCourseGetDTO teacherCourse;

    private ClassGetDTO entityClass;
}

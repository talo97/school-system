package com.schoolsystem.lesson;

import com.schoolsystem.classes.EntityClass;
import com.schoolsystem.common.CommonService;
import com.schoolsystem.course.EntityTeacherCourse;
import com.schoolsystem.teacher.EntityTeacher;

import java.util.List;

public interface ServiceLesson extends CommonService<EntityLesson> {
    List<EntityLesson> findAllByClass(EntityClass entityClass);

    Boolean doesLessonAlreadyExist(EnumDayOfWeek dayOfWeek, EnumLessonNumber lessonNumber, Long entityClassId);

    Boolean isTeacherAvailable(EntityTeacher entityTeacher, EnumDayOfWeek dayOfWeek, EnumLessonNumber lessonNumber);

    List<EntityLesson> findAllByTeacher(EntityTeacher entityTeacher);

    List<EntityLesson> findAllByTeacherAndClass(EntityTeacher entityTeacher, EntityClass studentClass);

    Boolean isThereLessonForTeacherCourseInClass(EntityTeacherCourse teacherCourse, EntityClass entityClass);

    List<EntityLesson> findAllActive();

    List<EntityTeacherCourse> findDistinctTeacherCoursesOfGivenClass(EntityClass entityClass);
}

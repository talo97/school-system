package com.schoolsystem.lesson;

import com.schoolsystem.classes.EntityClass;
import com.schoolsystem.common.CommonService;
import com.schoolsystem.course.EntityTeacherCourse;
import com.schoolsystem.teacher.EntityTeacher;

import java.util.List;
import java.util.Optional;

public interface ServiceLesson extends CommonService<EntityLesson> {
    List<EntityLesson> findAllByClass(EntityClass entityClass);

    Long getTotalAmountOfLessons(EntityClass entityClass);

    Long getTotalAmountOfLessons(List<EntityTeacherCourse> teacherCourses);

    Boolean doesLessonAlreadyExist(EnumDayOfWeek dayOfWeek, EnumLessonNumber lessonNumber, Long entityClassId);

    Boolean isTeacherAvailable(EntityTeacher entityTeacher, EnumDayOfWeek dayOfWeek, EnumLessonNumber lessonNumber);

    List<EntityLesson> findAllByTeacher(EntityTeacher entityTeacher);

    List<EntityLesson> findAllByTeacherCoursesInWithInactive(List<EntityTeacherCourse> teacherCourses);

    List<EntityLesson> findAllByTeacherAndClass(EntityTeacher entityTeacher, EntityClass studentClass);

    Boolean isThereLessonForTeacherCourseInClass(EntityTeacherCourse teacherCourse, EntityClass entityClass);

    List<EntityLesson> findAllActive();

    List<EntityTeacherCourse> findDistinctTeacherCoursesOfGivenClass(EntityClass entityClass);

    Optional<EntityLesson> find(List<EntityTeacherCourse> teacherCourses,EnumDayOfWeek dayOfWeek, EnumLessonNumber lessonNumber);
}

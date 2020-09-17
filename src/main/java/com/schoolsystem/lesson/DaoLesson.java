package com.schoolsystem.lesson;

import com.schoolsystem.classes.EntityClass;
import com.schoolsystem.course.EntityTeacherCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DaoLesson extends JpaRepository<EntityLesson, Long> {

    Long countByEntityClassAndActiveIsTrue(EntityClass entityClass);

    Long countByTeacherCourseInAndActiveIsTrue(List<EntityTeacherCourse> teacherCourses);

    List<EntityLesson> findAllByActiveIsTrue();

    List<EntityLesson> findAllByEntityClassAndActiveIsTrue(EntityClass entityClass);

    List<EntityLesson> findAllByTeacherCourseAndActiveIsTrue(EntityTeacherCourse teacherCourse);
    List<EntityLesson> findAllByTeacherCourseInAndActiveIsTrue(List<EntityTeacherCourse> teacherCourse);

    List<EntityLesson> findAllByTeacherCourseAndEntityClassAndActiveIsTrue(EntityTeacherCourse teacherCourse, EntityClass entityClass);

    Optional<EntityLesson> findByDayOfWeekAndLessonNumberAndEntityClassIdAndActiveIsTrue(EnumDayOfWeek dayOfWeek, EnumLessonNumber lessonNumber, Long entityClassId);

    Optional<EntityLesson> findByTeacherCourseAndDayOfWeekAndLessonNumberAndActiveIsTrue(EntityTeacherCourse teacherCourse, EnumDayOfWeek dayOfWeek, EnumLessonNumber lessonNumber);

    Optional<EntityLesson> findByTeacherCourseInAndDayOfWeekAndLessonNumberAndActiveIsTrue(List<EntityTeacherCourse> teacherCourses, EnumDayOfWeek dayOfWeek, EnumLessonNumber lessonNumber);
}

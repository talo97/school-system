package com.schoolsystem.lesson;

import com.schoolsystem.classes.EntityClass;
import com.schoolsystem.course.EntityTeacherCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DaoLesson extends JpaRepository<EntityLesson, Long> {

    List<EntityLesson> findAllByActiveIsTrue();

    List<EntityLesson> findAllByEntityClassIdAndActiveIsTrue(Long id);

    List<EntityLesson> findAllByTeacherCourseAndActiveIsTrue(EntityTeacherCourse teacherCourse);

    Optional<EntityLesson> findByDayOfWeekAndLessonNumberAndEntityClassIdAndActiveIsTrue(EnumDayOfWeek dayOfWeek, EnumLessonNumber lessonNumber, Long entityClassId);

    Optional<EntityLesson> findByTeacherCourseAndDayOfWeekAndLessonNumberAndActiveIsTrue(EntityTeacherCourse teacherCourse, EnumDayOfWeek dayOfWeek, EnumLessonNumber lessonNumber);
}

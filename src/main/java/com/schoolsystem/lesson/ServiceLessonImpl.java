package com.schoolsystem.lesson;

import com.schoolsystem.classes.EntityClass;
import com.schoolsystem.common.CommonServiceImpl;
import com.schoolsystem.course.EntityTeacherCourse;
import com.schoolsystem.course.ServiceTeacherCourse;
import com.schoolsystem.teacher.EntityTeacher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServiceLessonImpl extends CommonServiceImpl<EntityLesson, DaoLesson> implements ServiceLesson {

    private final ServiceTeacherCourse serviceTeacherCourse;

    public ServiceLessonImpl(DaoLesson repository, ServiceTeacherCourse serviceTeacherCourse) {
        super(repository);
        this.serviceTeacherCourse = serviceTeacherCourse;
    }

    @Override
    public List<EntityLesson> findAllByClass(EntityClass entityClass) {
        return repository.findAllByEntityClassAndActiveIsTrue(entityClass);
    }

    @Override
    public Long getTotalAmountOfLessons(EntityClass entityClass) {
        return repository.countByEntityClassAndActiveIsTrue(entityClass);
    }

    @Override
    public Long getTotalAmountOfLessons(List<EntityTeacherCourse> teacherCourses) {
        return repository.countByTeacherCourseInAndActiveIsTrue(teacherCourses);
    }

    @Override
    public Boolean doesLessonAlreadyExist(EnumDayOfWeek dayOfWeek, EnumLessonNumber lessonNumber, Long entityClassId) {
        return repository.findByDayOfWeekAndLessonNumberAndEntityClassIdAndActiveIsTrue(dayOfWeek, lessonNumber, entityClassId).isPresent();
    }

    @Override
    public Boolean isTeacherAvailable(EntityTeacher entityTeacher, EnumDayOfWeek dayOfWeek, EnumLessonNumber lessonNumber) {
        List<EntityTeacherCourse> teacherCourses = serviceTeacherCourse.findByTeacher(entityTeacher);
        boolean isTeacherAvailable = true;
        for (EntityTeacherCourse teacherCourse : teacherCourses) {
            if (repository.findByTeacherCourseAndDayOfWeekAndLessonNumberAndActiveIsTrue(teacherCourse, dayOfWeek, lessonNumber).isPresent()) {
                isTeacherAvailable = false;
                break;
            }
        }
        return isTeacherAvailable;
    }

    @Override
    public List<EntityLesson> findAllByTeacher(EntityTeacher entityTeacher) {
        List<EntityTeacherCourse> teacherCourses = serviceTeacherCourse.findByTeacher(entityTeacher);
        List<EntityLesson> lessons = new ArrayList<>();
        teacherCourses.forEach(e -> lessons.addAll(repository.findAllByTeacherCourseAndActiveIsTrue(e)));
        return lessons;
    }

    @Override
    public List<EntityLesson> findAllByTeacherCoursesInWithInactive(List<EntityTeacherCourse> teacherCourses) {
        return repository.findAllByTeacherCourseIn(teacherCourses);
    }

    @Override
    public List<EntityLesson> findAllByTeacherAndClass(EntityTeacher entityTeacher, EntityClass studentClass) {
        List<EntityTeacherCourse> teacherCourses = serviceTeacherCourse.findByTeacher(entityTeacher);
        List<EntityLesson> lessons = new ArrayList<>();
        teacherCourses.forEach(e -> lessons.addAll(repository.findAllByTeacherCourseAndEntityClassAndActiveIsTrue(e, studentClass)));
        return lessons;
    }

    @Override
    public Boolean isThereLessonForTeacherCourseInClass(EntityTeacherCourse teacherCourse, EntityClass entityClass) {
        return !repository.findAllByTeacherCourseAndEntityClassAndActiveIsTrue(teacherCourse, entityClass).isEmpty();
    }

    @Override
    public List<EntityLesson> findAllActive() {
        return repository.findAllByActiveIsTrue();
    }

    //it shouldn't be here... change later!!
    @Override
    public List<EntityTeacherCourse> findDistinctTeacherCoursesOfGivenClass(EntityClass entityClass) {
        return repository.findAllByEntityClassAndActiveIsTrue(entityClass).
                stream().map(EntityLesson::getTeacherCourse).distinct().collect(Collectors.toList());
    }

    @Override
    public Optional<EntityLesson> find(List<EntityTeacherCourse> teacherCourses, EnumDayOfWeek dayOfWeek, EnumLessonNumber lessonNumber) {
        return repository.findByTeacherCourseInAndDayOfWeekAndLessonNumberAndActiveIsTrue(teacherCourses, dayOfWeek, lessonNumber);
    }
}

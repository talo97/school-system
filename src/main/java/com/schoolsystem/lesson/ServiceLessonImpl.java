package com.schoolsystem.lesson;

import com.schoolsystem.common.CommonServiceImpl;
import com.schoolsystem.course.EntityTeacherCourse;
import com.schoolsystem.course.ServiceTeacherCourse;
import com.schoolsystem.teacher.EntityTeacher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ServiceLessonImpl extends CommonServiceImpl<EntityLesson, DaoLesson> implements ServiceLesson {

    private final ServiceTeacherCourse serviceTeacherCourse;

    public ServiceLessonImpl(DaoLesson repository, ServiceTeacherCourse serviceTeacherCourse) {
        super(repository);
        this.serviceTeacherCourse = serviceTeacherCourse;
    }

    @Override
    public List<EntityLesson> findAllByEntityClassId(Long id) {
        return repository.findAllByEntityClassIdAndActiveIsTrue(id);
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
        teacherCourses.forEach(e-> lessons.addAll(repository.findAllByTeacherCourseAndActiveIsTrue(e)));
        return lessons;
    }

    @Override
    public List<EntityLesson> findAllActive() {
        return repository.findAllByActiveIsTrue();
    }
}

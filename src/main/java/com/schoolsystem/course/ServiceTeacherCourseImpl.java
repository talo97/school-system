package com.schoolsystem.course;

import com.schoolsystem.common.CommonServiceImpl;
import com.schoolsystem.teacher.EntityTeacher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServiceTeacherCourseImpl extends CommonServiceImpl<EntityTeacherCourse, DaoTeacherCourse> implements ServiceTeacherCourse{
    public ServiceTeacherCourseImpl(DaoTeacherCourse repository) {
        super(repository);
    }

    @Override
    public Optional<EntityTeacherCourse> findByCourseIdAndTeacherId(Long courseId, Long teacherId) {
        return repository.findByCourseIdAndTeacherId(courseId, teacherId);
    }

    @Override
    public List<EntityTeacherCourse> findByCourse(EntityCourse entityCourse) {
        return repository.findByCourse(entityCourse);
    }

    @Override
    public List<EntityTeacherCourse> findByTeacher(EntityTeacher entityTeacher) {
        return repository.findByTeacher(entityTeacher);
    }
}

package com.schoolsystem.course;

import com.schoolsystem.classes.EntityClass;
import com.schoolsystem.common.CommonService;
import com.schoolsystem.teacher.EntityTeacher;

import java.util.List;
import java.util.Optional;

public interface ServiceTeacherCourse extends CommonService<EntityTeacherCourse> {
    Optional<EntityTeacherCourse> findByCourseIdAndTeacherId(Long courseId, Long teacherId);

    List<EntityTeacherCourse> findByCourse(EntityCourse entityCourse);

    List<EntityTeacherCourse> findByTeacher(EntityTeacher entityTeacher);
}

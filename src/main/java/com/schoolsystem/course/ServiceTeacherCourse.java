package com.schoolsystem.course;

import com.schoolsystem.common.CommonService;

import java.util.List;
import java.util.Optional;

public interface ServiceTeacherCourse extends CommonService<EntityTeacherCourse> {
    Optional<EntityTeacherCourse> findByCourseIdAndTeacherId(Long courseId, Long teacherId);
    List<EntityTeacherCourse> findByCourse(EntityCourse entityCourse);
}

package com.schoolsystem.course;

import com.schoolsystem.teacher.EntityTeacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DaoTeacherCourse extends JpaRepository<EntityTeacherCourse, Long> {
    Optional<EntityTeacherCourse> findByCourseIdAndTeacherId(Long courseId, Long teacherId);
    List<EntityTeacherCourse> findByCourse(EntityCourse entityCourse);
    List<EntityTeacherCourse> findByTeacher(EntityTeacher entityTeacher);
}

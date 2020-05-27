package com.schoolsystem.mark;

import com.schoolsystem.course.EntityTeacherCourse;
import com.schoolsystem.student.EntityStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DaoMark extends JpaRepository<EntityMark, Long> {
    List<EntityMark> findAllByStudent(EntityStudent student);
    List<EntityMark> findAllByStudentIn(List<EntityStudent> students);
    List<EntityMark> findAllByStudentAndTeacherCourse(EntityStudent student, EntityTeacherCourse teacherCourse);
}

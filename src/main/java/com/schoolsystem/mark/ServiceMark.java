package com.schoolsystem.mark;

import com.schoolsystem.classes.EntityClass;
import com.schoolsystem.common.CommonService;
import com.schoolsystem.course.EntityTeacherCourse;
import com.schoolsystem.student.EntityStudent;

import java.util.List;

public interface ServiceMark extends CommonService<EntityMark> {

    EntityMark save(MarkPostDTO markPostDTO, EntityStudent student, EntityTeacherCourse teacherCourse);

    List<EntityMark> findAllByStudent(EntityStudent student);

    List<EntityMark> findAllByStudentAndTeacherCourse(EntityStudent student, EntityTeacherCourse teacherCourse);

    List<EntityMark> findAllByClass(EntityClass entityClass);

    List<StudentMarksGetDTO> getStudentsMarksByClassAndCourse(EntityClass entityClass, EntityTeacherCourse teacherCourse);

    List<TeacherCourseMarksGetDTO> getStudentMarks(EntityStudent student);
}

package com.schoolsystem.mark;

import com.schoolsystem.course.EntityTeacherCourse;
import com.schoolsystem.course.TeacherCourseGetDTO;
import com.schoolsystem.student.EntityStudent;
import com.schoolsystem.student.StudentGetDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MarkGetDTO {
    private Long id;
    private EnumGrade enumGrade;
    private String description;
    private Date lastChange;
    private StudentGetDTO student;
    private TeacherCourseGetDTO teacherCourse;

}

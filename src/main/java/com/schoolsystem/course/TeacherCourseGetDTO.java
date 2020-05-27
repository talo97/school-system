package com.schoolsystem.course;

import com.schoolsystem.user.UserGetDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TeacherCourseGetDTO {
    private Long id;
    private CourseGetDTO course;
    private UserGetDTO teacher;
}

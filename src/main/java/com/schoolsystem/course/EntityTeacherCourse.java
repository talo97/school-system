package com.schoolsystem.course;

import com.schoolsystem.common.CommonEntity;
import com.schoolsystem.teacher.EntityTeacher;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "teacher_course")
@Getter
@Setter
public class EntityTeacherCourse extends CommonEntity {

    @ManyToOne
    @JoinColumn(name = "teacher")
    private EntityTeacher teacher;

    @ManyToOne
    @JoinColumn(name = "course")
    private EntityCourse course;
}

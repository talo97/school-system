package com.schoolsystem.mark;

import com.schoolsystem.common.CommonEntity;
import com.schoolsystem.course.EntityTeacherCourse;
import com.schoolsystem.student.EntityStudent;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "mark")
@Getter
@Setter
public class EntityMark extends CommonEntity {

    @Column(name = "value")
    private EnumGrade enumGrade;

    @Column(name = "description")
    private String description;

    @Column(name = "last_change")
    private Date lastChange;

    @ManyToOne
    @JoinColumn(name = "student")
    private EntityStudent student;

    @ManyToOne
    @JoinColumn(name = "teacher_course")
    private EntityTeacherCourse teacherCourse;



}

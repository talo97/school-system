package com.schoolsystem.lesson;

import com.schoolsystem.common.CommonEntity;
import com.schoolsystem.course.EntityTeacherCourse;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "substitution")
@Getter
@Setter
public class EntitySubstitution extends CommonEntity {

    @Column(name = "date")
    private long date;

    @ManyToOne
    @JoinColumn(name = "planned_lesson")
    private EntityLesson lesson;

    @ManyToOne
    @JoinColumn(name = "completed_course")
    private EntityTeacherCourse teacherCourse;

}

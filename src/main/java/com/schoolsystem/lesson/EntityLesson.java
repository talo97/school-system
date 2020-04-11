package com.schoolsystem.lesson;

import com.schoolsystem.common.CommonEntity;
import com.schoolsystem.course.EntityTeacherCourse;
import com.schoolsystem.classes.EntityClass;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "lesson")
@Getter
@Setter
public class EntityLesson extends CommonEntity {

    @Column(name = "day_of_week")
    private EnumDayOfWeek dayOfWeek;

    @Column(name = "lesson_number")
    private EnumLessonNumber lessonNumber;

    @ManyToOne
    @JoinColumn(name = "class")
    private EntityClass entityClass;

    @ManyToOne
    @JoinColumn(name = "teacher_course")
    private EntityTeacherCourse teacherCourse;

}

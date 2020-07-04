package com.schoolsystem.presence;

import com.schoolsystem.common.CommonEntity;
import com.schoolsystem.lesson.EntityLesson;
import com.schoolsystem.student.EntityStudent;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "presence")
@Getter
@Setter
public class EntityPresence extends CommonEntity {

    @Column(name = "was_present")
    private Boolean wasPresent;

    @Column(name = "description")
    private String description = "";

    @Column(name = "date")
    private Date date;

    @ManyToOne
    @JoinColumn(name = "student")
    private EntityStudent student;

    @ManyToOne
    @JoinColumn(name = "lesson")
    private EntityLesson lesson;
}

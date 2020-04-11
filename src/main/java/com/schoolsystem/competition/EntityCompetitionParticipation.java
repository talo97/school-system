package com.schoolsystem.competition;

import com.schoolsystem.common.CommonEntity;
import com.schoolsystem.student.EntityStudent;
import com.schoolsystem.teacher.EntityTeacher;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "competition_participation")
@Setter
@Getter
public class EntityCompetitionParticipation extends CommonEntity {

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "competition")
    private EntityCompetition competition;

    @ManyToOne
    @JoinColumn(name = "student")
    private EntityStudent student;

    @ManyToOne
    @JoinColumn(name = "teacher")
    private EntityTeacher teacher;
}

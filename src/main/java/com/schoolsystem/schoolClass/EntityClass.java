package com.schoolsystem.schoolClass;

import com.schoolsystem.common.CommonEntity;
import com.schoolsystem.teacher.EntityTeacher;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "class")
@Getter
@Setter
public class EntityClass extends CommonEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "education_stage")
    private EnumEducationStage enumEducationStage;

    @OneToOne
    @JoinColumn(name = "supervisor")
    private EntityTeacher supervisor;

}

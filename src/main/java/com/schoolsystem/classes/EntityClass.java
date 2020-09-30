package com.schoolsystem.classes;

import com.schoolsystem.common.CommonEntity;
import com.schoolsystem.teacher.EntityTeacher;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "class")
@Getter
@Setter
public class EntityClass extends CommonEntity implements Comparable<EntityClass> {

    @Column(name = "name")
    private String name;
    @Column(name = "education_stage")
    private EnumEducationStage enumEducationStage;
    @OneToOne
    @JoinColumn(name = "supervisor")
    private EntityTeacher supervisor;

    @Override
    public int compareTo(EntityClass entityClass) {
        return this.getId().compareTo(entityClass.getId());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EntityClass) {
            return this.getId().equals(((EntityClass) obj).getId());
        } else {
            return false;
        }
    }
}

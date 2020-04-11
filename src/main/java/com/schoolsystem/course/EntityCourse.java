package com.schoolsystem.course;

import com.schoolsystem.common.CommonEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "course")
@Getter
@Setter
public class EntityCourse extends CommonEntity {

    @Column(name = "name")
    private String name;
}

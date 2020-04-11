package com.schoolsystem.competition;

import com.schoolsystem.common.CommonEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "competition")
@Setter
@Getter
public class EntityCompetition extends CommonEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;
}

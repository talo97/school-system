package com.schoolsystem.common;

import lombok.Getter;

import javax.persistence.*;

@MappedSuperclass
@Getter
public abstract class CommonEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}

package com.schoolsystem.user;

import com.schoolsystem.common.CommonEntity;
import com.schoolsystem.common.EnumUserGroup;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "user")
@Getter
@Setter
public class EntityUser extends CommonEntity {

    @Column(name = "login", unique = true)
    private String login;

    @Column(name = "password")
    private String password;

    @Column(name = "user_group")
    private EnumUserGroup userGroup;

    @Column(name = "first_name")
    private String firstName = "";

    @Column(name = "last_name")
    private String lastName= "";

    @Column(name = "birth_date")
    private long birthDate = 0;
}

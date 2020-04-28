package com.schoolsystem.user;

import com.schoolsystem.common.CommonEntity;
import com.schoolsystem.common.EnumUserGroup;
import com.schoolsystem.parent.EntityParent;
import com.schoolsystem.student.EntityStudent;
import com.schoolsystem.teacher.EntityTeacher;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
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

    @Column(name = "birthdate")
    private long birthDate = 0;

    @Column(name = "user_type")
    private EnumUserType userType;

    @OneToOne(mappedBy="user")
    private EntityStudent entityStudent;

    @OneToOne(mappedBy="user")
    private EntityTeacher entityTeacher;

    @OneToOne(mappedBy="user")
    private EntityParent entityParent;
}

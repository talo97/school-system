package com.schoolsystem.user;

import com.schoolsystem.common.CommonEntity;
import com.schoolsystem.parent.EntityParent;
import com.schoolsystem.student.EntityStudent;
import com.schoolsystem.teacher.EntityTeacher;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.sql.Date;

@Entity
@Table(name = "users")
@Getter
@Setter
public class EntityUser extends CommonEntity {

    @Column(name = "login", unique = true)
    private String login;

    @Column(name = "password")
    private String password;

    @Column(name = "first_name")
    private String firstName = "";

    @Column(name = "last_name")
    private String lastName = "";

    @Column(name = "birthdate")
    private Date birthDate;

    @Column(name = "phone_number")
    private String phoneNumber = "";

    @Column(name = "email")
    private String email = "";

    @Column(name = "user_type")
    private EnumUserType userType;

    @OneToOne(mappedBy = "users")
    private EntityStudent entityStudent;

    @OneToOne(mappedBy = "users")
    private EntityTeacher entityTeacher;

    @OneToOne(mappedBy = "users")
    private EntityParent entityParent;
}

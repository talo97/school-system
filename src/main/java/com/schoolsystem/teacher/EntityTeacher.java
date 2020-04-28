package com.schoolsystem.teacher;

import com.schoolsystem.common.CommonEntity;
import lombok.Getter;
import lombok.Setter;
import com.schoolsystem.user.EntityUser;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "teacher")
@Getter
@Setter
public class EntityTeacher extends CommonEntity {

    @OneToOne
    @JoinColumn(name="users")
    private EntityUser users;

}

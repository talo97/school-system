package com.schoolsystem.student;

import com.schoolsystem.common.CommonEntity;
import com.schoolsystem.classes.EntityClass;
import lombok.Getter;
import lombok.Setter;
import com.schoolsystem.parent.EntityParent;
import com.schoolsystem.user.EntityUser;

import javax.persistence.*;

@Entity
@Table(name = "student")
@Getter
@Setter
public class EntityStudent extends CommonEntity {

    @OneToOne
    @JoinColumn(name = "users")
    private EntityUser users;

    @OneToOne
    @JoinColumn(name = "parent")
    private EntityParent parent;

    @ManyToOne
    @JoinColumn(name = "student_class")
    private EntityClass studentClass;
}

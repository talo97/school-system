package com.schoolsystem.parent;

import com.schoolsystem.common.CommonEntity;
import com.schoolsystem.student.EntityStudent;
import com.schoolsystem.user.EntityUser;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "parent")
@Getter
@Setter
public class EntityParent  extends CommonEntity {

    @OneToOne
    @JoinColumn(name="users")
    private EntityUser users;

    @OneToOne(mappedBy="parent")
    private EntityStudent entityStudent;

}

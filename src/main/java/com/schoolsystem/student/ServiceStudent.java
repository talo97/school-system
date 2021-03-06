package com.schoolsystem.student;

import com.schoolsystem.classes.EntityClass;
import com.schoolsystem.common.CommonService;
import com.schoolsystem.parent.EntityParent;
import com.schoolsystem.teacher.EntityTeacher;
import com.schoolsystem.user.UserPostDTO;

import java.util.List;

public interface ServiceStudent extends CommonService<EntityStudent> {
    EntityStudent save(StudentPostDTO studentPostDTO, EntityParent entityParent, EntityClass entityClass);
    List<EntityStudent> findAllByStudentClass(EntityClass entityClass);
}

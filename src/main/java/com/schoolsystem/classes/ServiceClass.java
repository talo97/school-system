package com.schoolsystem.classes;

import com.schoolsystem.common.CommonService;
import com.schoolsystem.teacher.EntityTeacher;

import java.util.List;
import java.util.Optional;

public interface ServiceClass extends CommonService<EntityClass> {
    Optional<EntityClass> save(ClassPostDTO classPostDTO);
    /**
     * finds and returns all classes that given teacher teaches.
     * @param teacher current user
     * @return List<EntityClass>
     */
    List<EntityClass> findDistinctByTeacher(EntityTeacher teacher);
    Optional<EntityClass> findBySupervisor(EntityTeacher supervisor);

}

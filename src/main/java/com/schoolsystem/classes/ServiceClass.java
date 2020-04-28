package com.schoolsystem.classes;

import com.schoolsystem.common.CommonService;

import java.util.Optional;

public interface ServiceClass extends CommonService<EntityClass> {
    Optional<EntityClass> save(ClassPostDTO classPostDTO);
}

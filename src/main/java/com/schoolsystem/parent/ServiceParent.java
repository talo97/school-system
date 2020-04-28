package com.schoolsystem.parent;

import com.schoolsystem.common.CommonService;
import com.schoolsystem.teacher.EntityTeacher;
import com.schoolsystem.user.UserPostDTO;

public interface ServiceParent extends CommonService<EntityParent> {
    EntityParent save(UserPostDTO user);
}

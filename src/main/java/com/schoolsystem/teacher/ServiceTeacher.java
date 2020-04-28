package com.schoolsystem.teacher;

import com.schoolsystem.common.CommonService;
import com.schoolsystem.user.EntityUser;
import com.schoolsystem.user.UserPostDTO;

import java.util.Optional;

public interface ServiceTeacher extends CommonService<EntityTeacher> {
    EntityTeacher save(UserPostDTO user);
}

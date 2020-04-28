package com.schoolsystem.user;

import com.schoolsystem.common.CommonService;

import java.util.Optional;

public interface ServiceUser extends CommonService<EntityUser> {
    Optional<EntityUser> findByLogin(String login);

    EntityUser save(UserPostDTO user, EnumUserType userType);
}

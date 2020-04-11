package com.schoolsystem.user;

import com.schoolsystem.common.CommonService;

import java.util.Optional;

public interface ServiceUser extends CommonService<EntityUser> {
    Optional<EntityUser> getByLogin(String login);

    Optional<EntityUser> save(UserPostDTO user);
}

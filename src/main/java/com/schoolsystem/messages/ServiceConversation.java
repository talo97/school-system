package com.schoolsystem.messages;

import com.schoolsystem.common.CommonService;
import com.schoolsystem.user.EntityUser;

import java.util.List;

public interface ServiceConversation extends CommonService<EntityConversation> {
    public List<EntityConversation> findAllByUserFirstOrUserSecond(EntityUser userFirst, EntityUser userSecond);
}

package com.schoolsystem.messages;

import com.schoolsystem.common.CommonService;
import com.schoolsystem.common.CommonServiceImpl;
import com.schoolsystem.user.EntityUser;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceConversationImpl extends CommonServiceImpl<EntityConversation, DaoConversation> implements ServiceConversation {

    public ServiceConversationImpl(DaoConversation repository) {
        super(repository);
    }

    @Override
    public List<EntityConversation> findAllByUserFirstOrUserSecond(EntityUser userFirst, EntityUser userSecond) {
        return repository.findAllByUserFirstOrUserSecondOrderByLastAnswerDateAsc(userFirst, userSecond);
    }
}

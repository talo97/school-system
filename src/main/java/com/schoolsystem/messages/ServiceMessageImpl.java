package com.schoolsystem.messages;

import com.schoolsystem.common.CommonServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ServiceMessageImpl extends CommonServiceImpl<EntityMessage, DaoMessage> implements ServiceMessage {

    public ServiceMessageImpl(DaoMessage repository) {
        super(repository);
    }
}

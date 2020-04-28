package com.schoolsystem.classes;

import com.schoolsystem.common.CommonServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ServiceClassImpl extends CommonServiceImpl<EntityClass, DaoClass> implements ServiceClass {
    public ServiceClassImpl(DaoClass repository) {
        super(repository);
    }
}

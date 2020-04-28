package com.schoolsystem.parent;

import com.schoolsystem.common.CommonServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ServiceParentImpl extends CommonServiceImpl<EntityParent, DaoParent> implements ServiceParent {

    public ServiceParentImpl(DaoParent repository) {
        super(repository);
    }
}

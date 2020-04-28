package com.schoolsystem.parent;

import com.schoolsystem.common.CommonServiceImpl;
import com.schoolsystem.teacher.EntityTeacher;
import com.schoolsystem.user.EnumUserType;
import com.schoolsystem.user.ServiceUser;
import com.schoolsystem.user.UserPostDTO;
import org.springframework.stereotype.Service;

@Service
public class ServiceParentImpl extends CommonServiceImpl<EntityParent, DaoParent> implements ServiceParent {
    private final ServiceUser serviceUser;

    public ServiceParentImpl(DaoParent repository, ServiceUser serviceUser) {
        super(repository);
        this.serviceUser = serviceUser;
    }

    @Override
    public EntityParent save(UserPostDTO user) {
        EntityParent entityParent = new EntityParent();
        entityParent.setUsers(serviceUser.save(user,EnumUserType.PARENT));
        save(entityParent);
        return entityParent;
    }
}

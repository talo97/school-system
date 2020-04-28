package com.schoolsystem.teacher;

import com.schoolsystem.common.CommonServiceImpl;
import com.schoolsystem.user.EnumUserType;
import com.schoolsystem.user.ServiceUser;
import com.schoolsystem.user.UserPostDTO;
import org.springframework.stereotype.Service;


@Service
public class ServiceTeacherImpl extends CommonServiceImpl<EntityTeacher, DaoTeacher> implements ServiceTeacher {

    private final ServiceUser serviceUser;

    public ServiceTeacherImpl(DaoTeacher repository, ServiceUser serviceUser) {
        super(repository);
        this.serviceUser = serviceUser;
    }

    @Override
    public EntityTeacher save(UserPostDTO user) {
        EntityTeacher entityTeacher =new EntityTeacher();
        entityTeacher.setUsers(serviceUser.save(user, EnumUserType.TEACHER));
        save(entityTeacher);
        return entityTeacher;
    }
}

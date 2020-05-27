package com.schoolsystem.student;

import com.schoolsystem.classes.EntityClass;
import com.schoolsystem.common.CommonServiceImpl;
import com.schoolsystem.parent.EntityParent;
import com.schoolsystem.teacher.EntityTeacher;
import com.schoolsystem.user.EnumUserType;
import com.schoolsystem.user.ServiceUser;
import com.schoolsystem.user.UserGetDTO;
import com.schoolsystem.user.UserPostDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceStudentImpl extends CommonServiceImpl<EntityStudent, DaoStudent> implements ServiceStudent {
    private final ServiceUser serviceUser;
    private final ModelMapper modelMapper;
    public ServiceStudentImpl(DaoStudent repository,ServiceUser serviceUser, ModelMapper modelMapper) {
        super(repository);
        this.serviceUser = serviceUser;
        this.modelMapper = modelMapper;
    }

    @Override
    public EntityStudent save(StudentPostDTO studentPostDTO, EntityParent entityParent, EntityClass entityClass) {
        EntityStudent entityStudent = new EntityStudent();
        entityStudent.setUsers(serviceUser.save(modelMapper.map(studentPostDTO, UserPostDTO.class), EnumUserType.STUDENT));
        entityStudent.setParent(entityParent);
        entityStudent.setStudentClass(entityClass);
        save(entityStudent);
        return entityStudent;
    }

    @Override
    public List<EntityStudent> findAllByStudentClass(EntityClass entityClass) {
        return repository.findAllByStudentClass(entityClass);
    }
}

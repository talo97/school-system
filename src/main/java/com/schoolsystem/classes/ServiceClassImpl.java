package com.schoolsystem.classes;

import com.schoolsystem.common.CommonServiceImpl;
import com.schoolsystem.teacher.EntityTeacher;
import com.schoolsystem.teacher.ServiceTeacher;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ServiceClassImpl extends CommonServiceImpl<EntityClass, DaoClass> implements ServiceClass {

    private final ServiceTeacher serviceTeacher;
    private final ModelMapper modelMapper;

    public ServiceClassImpl(DaoClass repository, ServiceTeacher serviceTeacher, ModelMapper modelMapper) {
        super(repository);
        this.serviceTeacher = serviceTeacher;
        this.modelMapper = modelMapper;
    }


    @Override
    public Optional<EntityClass> save(ClassPostDTO classPostDTO) {
        Optional<EntityTeacher> supervisor = serviceTeacher.get(classPostDTO.getSupervisorId());
        Optional<EntityClass> toSave = Optional.empty();
        if (supervisor.isPresent()) {
            toSave = Optional.of(modelMapper.map(classPostDTO, EntityClass.class));
            toSave.get().setSupervisor(supervisor.get());
        }
        toSave.ifPresent(this::save);
        return toSave;
    }
}

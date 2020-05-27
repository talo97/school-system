package com.schoolsystem.classes;

import com.schoolsystem.common.CommonServiceImpl;
import com.schoolsystem.course.ServiceTeacherCourse;
import com.schoolsystem.lesson.EntityLesson;
import com.schoolsystem.lesson.ServiceLesson;
import com.schoolsystem.teacher.EntityTeacher;
import com.schoolsystem.teacher.ServiceTeacher;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServiceClassImpl extends CommonServiceImpl<EntityClass, DaoClass> implements ServiceClass {

    private final ServiceTeacher serviceTeacher;
    private final ServiceLesson serviceLesson;
    private final ModelMapper modelMapper;

    public ServiceClassImpl(DaoClass repository, ServiceTeacher serviceTeacher, ServiceLesson serviceLesson, ModelMapper modelMapper) {
        super(repository);
        this.serviceTeacher = serviceTeacher;
        this.serviceLesson = serviceLesson;
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

    @Override
    public List<EntityClass> findDistinctByTeacher(EntityTeacher teacher) {
        return serviceLesson.findAllByTeacher(teacher).stream().map(EntityLesson::getEntityClass).sorted().distinct().collect(Collectors.toList());
    }

    @Override
    public Optional<EntityClass> findBySupervisor(EntityTeacher supervisor) {
        return repository.findBySupervisor(supervisor);
    }
}

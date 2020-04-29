package com.schoolsystem.course;

import com.schoolsystem.common.CommonServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ServiceCourseImpl extends CommonServiceImpl<EntityCourse, DaoCourse> implements ServiceCourse{
    public ServiceCourseImpl(DaoCourse repository) {
        super(repository);
    }
}

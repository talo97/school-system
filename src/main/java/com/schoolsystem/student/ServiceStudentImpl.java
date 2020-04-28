package com.schoolsystem.student;

import com.schoolsystem.common.CommonServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ServiceStudentImpl extends CommonServiceImpl<EntityStudent, DaoStudent> implements ServiceStudent {

    public ServiceStudentImpl(DaoStudent repository) {
        super(repository);
    }
}

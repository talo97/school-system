package com.schoolsystem.lesson;

import com.schoolsystem.common.CommonService;
import com.schoolsystem.common.CommonServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ServiceLessonImpl extends CommonServiceImpl<EntityLesson, DaoLesson> implements ServiceLesson {

    public ServiceLessonImpl(DaoLesson repository) {
        super(repository);
    }
}

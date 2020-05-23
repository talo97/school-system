package com.schoolsystem.lesson;

import com.schoolsystem.classes.EntityClass;
import com.schoolsystem.common.CommonService;
import com.schoolsystem.teacher.EntityTeacher;

import java.util.List;
import java.util.Optional;

public interface ServiceLesson extends CommonService<EntityLesson> {
    List<EntityLesson> findAllByEntityClassId(Long id);

    Boolean doesLessonAlreadyExist(EnumDayOfWeek dayOfWeek, EnumLessonNumber lessonNumber, Long entityClassId);

    Boolean isTeacherAvailable(EntityTeacher entityTeacher, EnumDayOfWeek dayOfWeek, EnumLessonNumber lessonNumber);

    List<EntityLesson> findAllByTeacher(EntityTeacher entityTeacher);

    List<EntityLesson> findAllActive();
}

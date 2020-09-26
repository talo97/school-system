package com.schoolsystem.presence;

import com.schoolsystem.common.CommonService;
import com.schoolsystem.common.Pair;
import com.schoolsystem.lesson.EntityLesson;
import com.schoolsystem.student.EntityStudent;

import java.sql.Date;
import java.util.List;

public interface ServicePresence extends CommonService<EntityPresence> {

    List<EntityPresence> getPresenceFromLessons(List<EntityLesson> lessons);
    List<EntityPresence> getPresenceFromLessons(List<EntityLesson> lessons, Date dateFrom, Date dateTo);
    void save(EntityStudent student, Boolean isPresent, EntityLesson lesson, Date date);
    void saveOrUpdateAll(List<Pair<Boolean, EntityStudent>> studentsPresence, EntityLesson entityLesson);
    List<EntityPresence> find(EntityLesson lesson, Date date);
    List<EntityPresence> find(EntityStudent student);
    List<EntityPresence> find(EntityStudent student, Date dateFrom, Date dateTo);
    List<EntityPresence> find(EntityStudent student, boolean wasPresent);
}

package com.schoolsystem.presence;

import com.schoolsystem.common.CommonServiceImpl;
import com.schoolsystem.common.Pair;
import com.schoolsystem.common.SchoolTimeUtil;
import com.schoolsystem.lesson.EntityLesson;
import com.schoolsystem.student.EntityStudent;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class ServicePresenceImpl extends CommonServiceImpl<EntityPresence, DaoPresence> implements ServicePresence {

    private SchoolTimeUtil schoolTimeUtil;
    public ServicePresenceImpl(DaoPresence repository, SchoolTimeUtil schoolTimeUtil) {
        super(repository);
        this.schoolTimeUtil = schoolTimeUtil;
    }

    //TODO::change so it will update if already exist
    @Override
    public void saveOrUpdateAll(List<Pair<Boolean, EntityStudent>> studentsPresence, EntityLesson entityLesson) {
        List<EntityPresence> presences = find(entityLesson, schoolTimeUtil.getCurrentSqlDate());
        if(presences.isEmpty()){
            studentsPresence.forEach(studentPair -> {
                save(studentPair.getSecond(), studentPair.getFirst(), entityLesson, schoolTimeUtil.getCurrentSqlDate());
            });
        }else{
            presences.forEach(presence -> {
                studentsPresence.forEach(studentPair -> {
                    if(studentPair.getSecond().getId().equals(presence.getStudent().getId())){
                        presence.setWasPresent(studentPair.getFirst());
                        update(presence);
                    }
                });
            });
        }
    }

    @Override
    public List<EntityPresence> find(EntityLesson lesson, Date date) {
        return repository.findAllByLessonAndDate(lesson, date);
    }

    @Override
    public List<EntityPresence> find(EntityStudent student) {
        return repository.findAllByStudent(student);
    }

    @Override
    public List<EntityPresence> find(EntityStudent student, Date dateFrom, Date dateTo) {
        return repository.findAllByStudentAndDateGreaterThanEqualAndDateLessThan(student, dateFrom, dateTo);
    }

    @Override
    public List<EntityPresence> find(EntityStudent student, boolean wasPresent) {
        return repository.findAllByStudentAndWasPresent(student, wasPresent);
    }

    @Override
    public List<EntityPresence> getPresenceFromLessons(List<EntityLesson> lessons) {
        return repository.findAllByLessonIn(lessons);
    }

    @Override
    public List<EntityPresence> getPresenceFromLessons(List<EntityLesson> lessons, Date dateFrom, Date dateTo) {
        return repository.findAllByLessonInAndDateGreaterThanEqualAndDateLessThan(lessons, dateFrom, dateTo);
    }

    @Override
    public void save(EntityStudent student, Boolean isPresent, EntityLesson lesson, Date date) {
        EntityPresence presence = new EntityPresence();
        presence.setDate(date);
        presence.setLesson(lesson);
        presence.setStudent(student);
        presence.setWasPresent(isPresent);
        save(presence);
    }

}

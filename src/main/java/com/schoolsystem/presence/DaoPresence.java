package com.schoolsystem.presence;

import com.schoolsystem.lesson.EntityLesson;
import com.schoolsystem.student.EntityStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface DaoPresence extends JpaRepository<EntityPresence, Long> {
    List<EntityPresence> findAllByLessonIn(List<EntityLesson> lessons);
    List<EntityPresence> findAllByLessonInAndDateGreaterThanEqualAndDateLessThan(List<EntityLesson> lessons, Date dateFrom, Date dateTo);
    List<EntityPresence> findAllByLessonAndDate(EntityLesson lesson, Date date);
    List<EntityPresence> findAllByStudent(EntityStudent student);
    List<EntityPresence> findAllByStudentAndDateGreaterThanEqualAndDateLessThan(EntityStudent student, Date dateFrom, Date dateTo);
    List<EntityPresence> findAllByStudentAndWasPresent(EntityStudent student, boolean wasPresent);
}

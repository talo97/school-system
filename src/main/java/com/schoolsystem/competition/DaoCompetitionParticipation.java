package com.schoolsystem.competition;

import com.schoolsystem.student.EntityStudent;
import com.schoolsystem.teacher.EntityTeacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DaoCompetitionParticipation extends JpaRepository<EntityCompetitionParticipation, Long> {
    List<EntityCompetitionParticipation> findAllByStudent(EntityStudent student);
    List<EntityCompetitionParticipation> findAllByTeacher(EntityTeacher teacher);
    List<EntityCompetitionParticipation> findAllByCompetitionId(Long competitionId);
}

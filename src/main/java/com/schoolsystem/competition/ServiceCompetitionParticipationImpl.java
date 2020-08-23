package com.schoolsystem.competition;

import com.schoolsystem.common.CommonServiceImpl;
import com.schoolsystem.student.EntityStudent;
import com.schoolsystem.teacher.EntityTeacher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceCompetitionParticipationImpl extends CommonServiceImpl<EntityCompetitionParticipation, DaoCompetitionParticipation> implements ServiceCompetitionParticipation {
    public ServiceCompetitionParticipationImpl(DaoCompetitionParticipation repository) {
        super(repository);
    }

    @Override
    public List<EntityCompetitionParticipation> findAllByStudent(EntityStudent student) {
        return repository.findAllByStudent(student);
    }

    @Override
    public List<EntityCompetitionParticipation> findAllByTeacher(EntityTeacher teacher) {
        return repository.findAllByTeacher(teacher);
    }
}

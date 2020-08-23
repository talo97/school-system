package com.schoolsystem.competition;


import com.schoolsystem.common.CommonService;
import com.schoolsystem.student.EntityStudent;
import com.schoolsystem.teacher.EntityTeacher;

import java.util.List;

public interface ServiceCompetitionParticipation extends CommonService<EntityCompetitionParticipation>{
    List<EntityCompetitionParticipation> findAllByStudent(EntityStudent student);
    List<EntityCompetitionParticipation> findAllByTeacher(EntityTeacher teacher);
}

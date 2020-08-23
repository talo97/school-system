package com.schoolsystem.competition;

import com.schoolsystem.common.CommonServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ServiceCompetitionImpl extends CommonServiceImpl<EntityCompetition,DaoCompetition> implements ServiceCompetition{
    public ServiceCompetitionImpl(DaoCompetition repository) {
        super(repository);
    }
}

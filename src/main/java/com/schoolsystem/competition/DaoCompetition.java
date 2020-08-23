package com.schoolsystem.competition;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DaoCompetition extends JpaRepository<EntityCompetition, Long> {
}

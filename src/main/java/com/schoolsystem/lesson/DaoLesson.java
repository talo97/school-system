package com.schoolsystem.lesson;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DaoLesson extends JpaRepository<EntityLesson, Long> {
}

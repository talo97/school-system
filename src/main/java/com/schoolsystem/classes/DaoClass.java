package com.schoolsystem.classes;

import com.schoolsystem.teacher.EntityTeacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DaoClass extends JpaRepository<EntityClass, Long> {
    Optional<EntityClass> findBySupervisor(EntityTeacher supervisor);
}

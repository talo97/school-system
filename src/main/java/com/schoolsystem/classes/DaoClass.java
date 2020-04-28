package com.schoolsystem.classes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DaoClass extends JpaRepository<EntityClass, Long> {
}

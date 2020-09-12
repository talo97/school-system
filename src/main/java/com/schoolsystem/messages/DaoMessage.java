package com.schoolsystem.messages;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DaoMessage extends JpaRepository<EntityMessage, Long> {
}


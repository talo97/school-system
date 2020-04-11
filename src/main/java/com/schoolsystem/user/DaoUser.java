package com.schoolsystem.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DaoUser extends JpaRepository<EntityUser, Long> {
    Optional<EntityUser> findByLogin(String login);
}

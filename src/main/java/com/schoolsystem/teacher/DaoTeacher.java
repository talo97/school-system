package com.schoolsystem.teacher;

import com.schoolsystem.user.EntityUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DaoTeacher extends JpaRepository<EntityTeacher, Long> {
}

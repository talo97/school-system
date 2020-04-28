package com.schoolsystem.student;

import com.schoolsystem.user.EntityUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DaoStudent  extends JpaRepository<EntityStudent, Long> {
}

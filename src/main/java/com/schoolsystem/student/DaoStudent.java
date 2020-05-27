package com.schoolsystem.student;

import com.schoolsystem.classes.EntityClass;
import com.schoolsystem.user.EntityUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DaoStudent  extends JpaRepository<EntityStudent, Long> {
    List<EntityStudent> findAllByStudentClass(EntityClass entityClass);
}

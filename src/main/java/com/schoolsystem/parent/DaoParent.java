package com.schoolsystem.parent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DaoParent extends JpaRepository<EntityParent, Long> {

}

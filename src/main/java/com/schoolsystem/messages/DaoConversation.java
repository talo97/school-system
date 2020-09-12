package com.schoolsystem.messages;

import com.schoolsystem.user.EntityUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DaoConversation extends JpaRepository<EntityConversation, Long> {
    public List<EntityConversation> findAllByUserFirstOrUserSecondOrderByLastAnswerDateAsc(EntityUser userFirst, EntityUser userSecond);
}

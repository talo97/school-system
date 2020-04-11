package com.schoolsystem.messages;

import com.schoolsystem.common.CommonEntity;
import com.schoolsystem.user.EntityUser;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "conversation")
@Setter
@Getter
public class EntityConversation extends CommonEntity {

    @Column(name = "topic_name")
    private String topicName;

    @Column(name = "topic_text")
    private String topicText;

    @Column(name = "last_answer_date")
    private long lastAnswerDate;

    @ManyToOne
    @JoinColumn(name = "user_first")
    private EntityUser userFirst;

    @ManyToOne
    @JoinColumn(name = "user_second")
    private EntityUser userSecond;
}

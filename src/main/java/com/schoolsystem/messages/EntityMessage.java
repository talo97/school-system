package com.schoolsystem.messages;

import com.schoolsystem.common.CommonEntity;
import com.schoolsystem.user.EntityUser;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "message")
@Setter
@Getter
public class EntityMessage extends CommonEntity {

    @Column(name = "answer_text")
    private String answerText;

    @OneToOne
    @JoinColumn(name = "next_answer")
    private EntityMessage nextAnswer;

    @ManyToOne
    @JoinColumn(name = "owner")
    private EntityUser owner;

    @Column(name = "answer_date")
    private Timestamp answerDate;
}

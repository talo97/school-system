package com.schoolsystem.messages;

import com.schoolsystem.common.CommonEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

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
}

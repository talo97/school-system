package com.schoolsystem.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ConversationGetDTO {

    private Long id;
    private String topicName;
    private String topicText;
    private Timestamp lastAnswerDate;
    private UserMessageDTO userFirst;
    private UserMessageDTO userSecond;

}

package com.schoolsystem.messages;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MessagePostDTO {
    private String answerText;
    private Long conversationId;
}

package com.schoolsystem.messages;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MessageGetDTO {
    private String answerText;
    private UserMessageDTO owner;
}

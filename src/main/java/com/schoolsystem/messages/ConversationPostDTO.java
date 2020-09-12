package com.schoolsystem.messages;

import com.schoolsystem.user.EnumUserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ConversationPostDTO {
    private String topicName;
    private String topicText;
    private Long recipientId;
}

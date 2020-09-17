package com.schoolsystem.messages;

import com.schoolsystem.common.SchoolTimeUtil;
import com.schoolsystem.parent.EntityParent;
import com.schoolsystem.parent.ServiceParent;
import com.schoolsystem.student.EntityStudent;
import com.schoolsystem.student.ServiceStudent;
import com.schoolsystem.teacher.EntityTeacher;
import com.schoolsystem.teacher.ServiceTeacher;
import com.schoolsystem.user.EntityUser;
import com.schoolsystem.user.ServiceUser;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class MessageController {

    private ServiceMessage serviceMessage;
    private ServiceConversation serviceConversation;
    private ServiceUser serviceUser;
    private ServiceTeacher serviceTeacher;
    private ServiceParent serviceParent;
    private ServiceStudent serviceStudent;
    private SchoolTimeUtil schoolTimeUtil;

    public MessageController(ServiceMessage serviceMessage, ServiceConversation serviceConversation, ServiceUser serviceUser, ServiceTeacher serviceTeacher, ServiceParent serviceParent, ServiceStudent serviceStudent, SchoolTimeUtil schoolTimeUtil) {
        this.serviceMessage = serviceMessage;
        this.serviceConversation = serviceConversation;
        this.serviceUser = serviceUser;
        this.serviceTeacher = serviceTeacher;
        this.serviceParent = serviceParent;
        this.serviceStudent = serviceStudent;
        this.schoolTimeUtil = schoolTimeUtil;
    }

    public UserMessageDTO convertUserToUserMessageDTO(EntityUser user) {
        UserMessageDTO userMessageDTO = new UserMessageDTO();
        userMessageDTO.setFirstName(user.getFirstName());
        userMessageDTO.setLastName(user.getLastName());
        userMessageDTO.setId(user.getId());
        userMessageDTO.setUserType(user.getUserType());
        return userMessageDTO;
    }

    public ConversationGetDTO convertConversationsToDTO(EntityConversation conversation) {
        ConversationGetDTO conversationGetDTO = new ConversationGetDTO();
        conversationGetDTO.setId(conversation.getId());
        conversationGetDTO.setLastAnswerDate(conversation.getLastAnswerDate());
        conversationGetDTO.setTopicName(conversation.getTopicName());
        conversationGetDTO.setTopicText(conversation.getTopicText());
        conversationGetDTO.setUserFirst(convertUserToUserMessageDTO(conversation.getUserFirst()));
        conversationGetDTO.setUserSecond(convertUserToUserMessageDTO(conversation.getUserSecond()));
        return conversationGetDTO;
    }

    public List<ConversationGetDTO> convertConversationsToListDTO(List<EntityConversation> conversations) {
        List<ConversationGetDTO> conversationGetDTOS = new ArrayList<>();
        conversations.forEach(entityConversation -> {
            conversationGetDTOS.add(convertConversationsToDTO(entityConversation));
        });
        return conversationGetDTOS;
    }


    @GetMapping("/conversations")
    @ApiOperation(value = "List all conversations of currentUser, order by date asc")
    public ResponseEntity<List<ConversationGetDTO>> getConversations() {
        EntityUser currentUser = serviceUser.getCurrentUserFromToken().get();
        return ResponseEntity.ok(convertConversationsToListDTO(serviceConversation.findAllByUserFirstOrUserSecond(currentUser, currentUser)));
    }

    @GetMapping("/messages/{conversationId}")
    @ApiOperation(value = "List all answers of given by ID conversation")
    public ResponseEntity<List<MessageGetDTO>> getMessages(@Valid @PathVariable Long conversationId) {
        Optional<EntityConversation> conversation = serviceConversation.get(conversationId);
        EntityUser currentUser = serviceUser.getCurrentUserFromToken().get();
        if (conversation.isPresent()
                && (conversation.get().getUserFirst().getId().equals(currentUser.getId())
                || conversation.get().getUserSecond().getId().equals(currentUser.getId()))) {
            EntityMessage entityMessage = conversation.get().getFirstAnswer();
            List<MessageGetDTO> messages = new ArrayList<>();
            MessageGetDTO firstAnswer = new MessageGetDTO();
            firstAnswer.setOwner(convertUserToUserMessageDTO(conversation.get().getUserFirst()));
            firstAnswer.setAnswerText(conversation.get().getTopicText());
            messages.add(firstAnswer);
            while (entityMessage != null) {
                MessageGetDTO messageGetDTO = new MessageGetDTO();
                messageGetDTO.setAnswerText(entityMessage.getAnswerText());
                messageGetDTO.setOwner(convertUserToUserMessageDTO(entityMessage.getOwner()));
                messages.add(messageGetDTO);
                entityMessage = entityMessage.getNextAnswer();
            }
            return ResponseEntity.ok(messages);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    //TODO::PODMIENIC DATE Z LONGA NA SQL DATE W BAZIE DANYCH!!!!!!!!!!!!!!!
    @PostMapping("/conversations")
    @ApiOperation(value = "Add new conversation")
    public ResponseEntity<ConversationGetDTO> addConversation(@Valid @RequestBody ConversationPostDTO conversation) {
        EntityUser currentUser = serviceUser.getCurrentUserFromToken().get();
        EntityConversation entityConversation = new EntityConversation();
        entityConversation.setFirstAnswer(null);
        entityConversation.setLastAnswerDate(schoolTimeUtil.getCurrentSqlTimestamp());
        entityConversation.setTopicName(conversation.getTopicName());
        entityConversation.setTopicText(conversation.getTopicText());
        entityConversation.setUserFirst(currentUser);
        Optional<EntityUser> recipient = serviceUser.get(conversation.getRecipientId());
        if (recipient.isPresent()) {
            entityConversation.setUserSecond(recipient.get());
            serviceConversation.save(entityConversation);
            return ResponseEntity.ok(convertConversationsToDTO(entityConversation));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/messages")
    @ApiOperation(value = "Add new answer to conversation")
    public ResponseEntity<MessageGetDTO> addAnswer(@Valid @RequestBody MessagePostDTO messagePostDTO) {
        EntityUser currentUser = serviceUser.getCurrentUserFromToken().get();
        Optional<EntityConversation> conversation = serviceConversation.get(messagePostDTO.getConversationId());
        if (conversation.isPresent()
                && (conversation.get().getUserFirst().getId().equals(currentUser.getId())
                || conversation.get().getUserSecond().getId().equals(currentUser.getId()))) {
            EntityMessage message = new EntityMessage();
            message.setAnswerText(messagePostDTO.getAnswerText());
            message.setNextAnswer(null);
            message.setOwner(currentUser);
            message.setAnswerDate(schoolTimeUtil.getCurrentSqlTimestamp());
            serviceMessage.save(message);
            //linking new message to old one or conversation.
            EntityMessage previousMessage = conversation.get().getFirstAnswer();
            //if there is no first message in the conversation
            if (previousMessage == null) {
                conversation.get().setFirstAnswer(message);
                serviceConversation.save(conversation.get());
            } else {
                //check until there is no next message
                while (previousMessage.getNextAnswer() != null) {
                    previousMessage = previousMessage.getNextAnswer();
                }
                previousMessage.setNextAnswer(message);
                serviceMessage.save(previousMessage);
            }

            MessageGetDTO toReturn = new MessageGetDTO();
            toReturn.setAnswerText(message.getAnswerText());
            toReturn.setOwner(convertUserToUserMessageDTO(currentUser));
            return ResponseEntity.ok(toReturn);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}

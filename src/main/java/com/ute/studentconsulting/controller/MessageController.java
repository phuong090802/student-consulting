package com.ute.studentconsulting.controller;

import com.ute.studentconsulting.entity.Message;
import com.ute.studentconsulting.model.MessageModel;
import com.ute.studentconsulting.payload.request.MessageRequest;
import com.ute.studentconsulting.payload.response.ErrorResponse;
import com.ute.studentconsulting.service.ConversationService;
import com.ute.studentconsulting.service.MessageService;
import com.ute.studentconsulting.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MessageController {
    private final MessageService messageService;
    private final ConversationService conversationService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final UserService userService;

    @MessageMapping("/conservations/{id}")
    public void sendMessageToConversation(@DestinationVariable("id") String id, @Payload MessageRequest request, Principal principal) {
        log.info(request.toString());
        if (principal != null) {
            log.info("principal: {}", principal.getName());
            var user = userService.findByPhone(principal.getName());
            var conversation = conversationService.findById(id);
            var message = messageService.save(new Message(request.getMessageText(), new Date(),
                    false, conversation, user));
            var simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            var sendMessage = new MessageModel(message.getId(), message.getMessageText(),
                    simpleDateFormat.format(message.getSentAt()), message.getSeen(), message.getSender().getId());
            simpMessagingTemplate.convertAndSend("/chat/conversations/%s".formatted(id), sendMessage);
        } else {
            simpMessagingTemplate.convertAndSend("/chat/conversations/%s/error".formatted(id), new ErrorResponse("Xác thực không thành công", "Gửi tin nhắn không thành công do không xác thực được", 100003));
        }

    }


}

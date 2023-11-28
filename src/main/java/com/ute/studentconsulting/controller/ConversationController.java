package com.ute.studentconsulting.controller;

import com.ute.studentconsulting.model.ConversationModel;
import com.ute.studentconsulting.model.MessageModel;
import com.ute.studentconsulting.payload.response.ApiSuccessResponse;
import com.ute.studentconsulting.service.ConversationService;
import com.ute.studentconsulting.service.MessageService;
import com.ute.studentconsulting.service.UserService;
import com.ute.studentconsulting.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
@Slf4j
public class ConversationController {
    private final ConversationService conversationService;
    private final UserService userService;
    private final AuthUtils authUtils;
    private final MessageService messageService;

    @GetMapping
    private ResponseEntity<?> getAllConversations() {
        return handleGetAllConversations();
    }

    private ResponseEntity<?> handleGetAllConversations() {
        var user = authUtils.getCurrentUser();
        var listConversation = conversationService.findAllByUser(user);
        var ids = listConversation.stream()
                .flatMap(conversation -> Stream.of(conversation.getStaff().getId(), conversation.getUser().getId()))
                .filter(id -> !id.equals(user.getId()))
                .distinct().toList();
        var conversations = userService.findAllByIdIn(ids).stream().map(userItem ->
                        new ConversationModel(userItem.getId(), userItem.getPhone(), user.getAvatar()))
                .toList();
        return ResponseEntity.ok(new ApiSuccessResponse<>(conversations));
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getAllMessage(@PathVariable("id") String id) {
        return handleGetAllMessage(id);
    }

    private ResponseEntity<?> handleGetAllMessage(String id) {
        var conversation = conversationService.findById(id);
        var simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        var messages = messageService.findAllByConversationOrderBySentAtAsc(conversation).stream().map(message ->
                new MessageModel(message.getMessageText(), message.getMessageText(),
                        simpleDateFormat.format(message.getSentAt()),
                        true, message.getSender().getId())).toList();
        return ResponseEntity.ok(new ApiSuccessResponse<>(messages));
    }
}

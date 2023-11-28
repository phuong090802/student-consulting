package com.ute.studentconsulting.service.impl;

import com.ute.studentconsulting.entity.Conversation;
import com.ute.studentconsulting.entity.Message;
import com.ute.studentconsulting.repository.MessageRepository;
import com.ute.studentconsulting.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;

    @Override
    public Message save(Message message) {
        return messageRepository.save(message);
    }

    @Override
    public List<Message> findAllByConversationOrderBySentAtAsc(Conversation conversation) {
        return messageRepository.findAllByConversationOrderBySentAtAsc(conversation);
    }


}

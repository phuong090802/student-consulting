package com.ute.studentconsulting.service;

import com.ute.studentconsulting.entity.Conversation;
import com.ute.studentconsulting.entity.Message;

import java.util.List;

public interface MessageService {
    Message save(Message message);
    List<Message> findAllByConversationOrderBySentAtAsc(Conversation conversation);

}

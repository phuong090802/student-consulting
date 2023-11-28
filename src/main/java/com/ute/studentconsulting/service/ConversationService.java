package com.ute.studentconsulting.service;

import com.ute.studentconsulting.entity.Conversation;
import com.ute.studentconsulting.entity.User;

import java.util.List;

public interface ConversationService {
    Conversation save(Conversation conversation);

    List<Conversation> findAllByUser(User user);

    Conversation findByStaffIsAndUserIs(User staff, User user);

    Conversation findById(String id);
}

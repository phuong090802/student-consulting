package com.ute.studentconsulting.repository;

import com.ute.studentconsulting.entity.Conversation;
import com.ute.studentconsulting.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, String> {
    List<Message> findAllByConversationOrderBySentAtAsc(Conversation conversation);
}

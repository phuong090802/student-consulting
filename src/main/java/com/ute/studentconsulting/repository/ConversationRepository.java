package com.ute.studentconsulting.repository;

import com.ute.studentconsulting.entity.Conversation;
import com.ute.studentconsulting.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, String> {
    List<Conversation> findAllByStaffIsOrUserIs(User staff, User user);

    Optional<Conversation> findByStaffIsAndUserIs(User staff, User user);
}

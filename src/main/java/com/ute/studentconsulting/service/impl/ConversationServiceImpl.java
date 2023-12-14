package com.ute.studentconsulting.service.impl;

import com.ute.studentconsulting.entity.Conversation;
import com.ute.studentconsulting.entity.User;
import com.ute.studentconsulting.exception.NotFoundException;
import com.ute.studentconsulting.repository.ConversationRepository;
import com.ute.studentconsulting.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {
    private final ConversationRepository conversationRepository;

    @Override
    public Conversation findById(String id) {
        return conversationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Không tìm thấy cuộc đối thoại",
                        "Không tìm thấy cuộc đối thoại với mã: %s".formatted(id), 10013));
    }

    @Override
    @Transactional
    public Conversation save(Conversation conversation) {
        return conversationRepository.save(conversation);
    }

    @Override
    public List<Conversation> findAllByUser(User user) {
        return conversationRepository.findAllByStaffIsOrUserIs(user, user);
    }

    @Override
    public Conversation findByStaffIsAndUserIs(User staff, User user) {
        return conversationRepository.findByStaffIsAndUserIs(staff, user)
                .orElse(null);
    }
}

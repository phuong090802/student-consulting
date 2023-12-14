package com.ute.studentconsulting.service.impl;

import com.ute.studentconsulting.entity.ForwardQuestion;
import com.ute.studentconsulting.repository.ForwardQuestionRepository;
import com.ute.studentconsulting.service.ForwardQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ForwardQuestionServiceImpl implements ForwardQuestionService {
    private final ForwardQuestionRepository forwardQuestionRepository;

    @Override
    @Transactional
    public void save(ForwardQuestion forwardQuestion) {
        forwardQuestionRepository.save(forwardQuestion);
    }
}

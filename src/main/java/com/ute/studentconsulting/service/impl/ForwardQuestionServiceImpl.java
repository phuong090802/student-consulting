package com.ute.studentconsulting.service.impl;

import com.ute.studentconsulting.entity.ForwardQuestion;
import com.ute.studentconsulting.repository.ForwardQuestionRepository;
import com.ute.studentconsulting.service.ForwardQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ForwardQuestionServiceImpl implements ForwardQuestionService {
    private final ForwardQuestionRepository forwardQuestionRepository;
    @Override
    public void save(ForwardQuestion forwardQuestion) {
        forwardQuestionRepository.save(forwardQuestion);
    }
}

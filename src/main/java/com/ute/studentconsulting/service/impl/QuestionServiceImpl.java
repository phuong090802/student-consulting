package com.ute.studentconsulting.service.impl;

import com.ute.studentconsulting.entity.Question;
import com.ute.studentconsulting.repository.QuestionRepository;
import com.ute.studentconsulting.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;
    @Override
    public Question save(Question question) {
        return questionRepository.save(question);
    }
}

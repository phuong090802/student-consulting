package com.ute.studentconsulting.service.impl;

import com.ute.studentconsulting.entity.Answer;
import com.ute.studentconsulting.repository.AnswerRepository;
import com.ute.studentconsulting.service.AnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {
    private final AnswerRepository answerRepository;

    @Override
    public void save(Answer answer) {
        answerRepository.save(answer);
    }
}

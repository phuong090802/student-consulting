package com.ute.studentconsulting.service.impl;

import com.ute.studentconsulting.entity.Answer;
import com.ute.studentconsulting.exception.NotFoundException;
import com.ute.studentconsulting.repository.AnswerRepository;
import com.ute.studentconsulting.service.AnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {
    private final AnswerRepository answerRepository;

    @Override
    public Page<Answer> findAllByApprovedIs(boolean approved, Pageable pageable) {
        return answerRepository.findAllByApprovedIs(approved, pageable);
    }

    @Override
    public void save(Answer answer) {
        answerRepository.save(answer);
    }

    @Override
    public Answer findById(String id) {
        return answerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy câu trả lời",
                        "Không tìm thấy câu trả lời với mã: %s".formatted(id), 10080));
    }
}

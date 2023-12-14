package com.ute.studentconsulting.service.impl;

import com.ute.studentconsulting.entity.Answer;
import com.ute.studentconsulting.entity.Question;
import com.ute.studentconsulting.entity.User;
import com.ute.studentconsulting.exception.NotFoundException;
import com.ute.studentconsulting.repository.AnswerRepository;
import com.ute.studentconsulting.service.AnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {
    private final AnswerRepository answerRepository;

    @Override
    @Transactional
    public void deleteByQuestion(Question question) {
        answerRepository.deleteByQuestion(question);
    }

    @Override
    public List<Answer> findAllByStaffInAndApprovedIs(Collection<User> staffs, Boolean approved) {
        return answerRepository.findAllByStaffInAndApprovedIs(staffs, approved);
    }

    @Override
    @Transactional
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

package com.ute.studentconsulting.service.impl;

import com.ute.studentconsulting.entity.Feedback;
import com.ute.studentconsulting.entity.User;
import com.ute.studentconsulting.exception.NotFoundException;
import com.ute.studentconsulting.repository.FeedbackRepository;
import com.ute.studentconsulting.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {
    private final FeedbackRepository feedbackRepository;

    @Override
    @Transactional
    public void deleteAllByUser(User user) {
        feedbackRepository.deleteAllByUser(user);
    }

    @Override
    public List<Feedback> findAllByUserIs(User user) {
        return feedbackRepository.findAllByUserIs(user);
    }

    @Override
    public Feedback findById(String id) {
        return feedbackRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Không tìm thấy phản hồi",
                        "Không tìm thấy phản hồi với mã: %s".formatted(id), 10087));
    }

    @Override
    public Page<Feedback> findAllByUserIs(User user, Pageable pageable) {
        return feedbackRepository.findAllByUserIs(user, pageable);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        feedbackRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void save(Feedback feedback) {
        feedbackRepository.save(feedback);
    }
}

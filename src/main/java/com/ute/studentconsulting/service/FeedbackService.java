package com.ute.studentconsulting.service;

import com.ute.studentconsulting.entity.Feedback;
import com.ute.studentconsulting.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FeedbackService {
    void save(Feedback feedback);
    Page<Feedback> findAllByUserIs(User user, Pageable pageable);
    void deleteAllByUser(User user);
    List<Feedback> findAllByUserIs(User user);
    Feedback findById(String id);
    void deleteById(String id);
}

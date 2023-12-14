package com.ute.studentconsulting.repository;

import com.ute.studentconsulting.entity.Feedback;
import com.ute.studentconsulting.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, String> {
    Page<Feedback> findAllByUserIs(User user, Pageable pageable);
    void deleteAllByUser(User user);
    List<Feedback> findAllByUserIs(User user);
}

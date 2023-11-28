package com.ute.studentconsulting.service;

import com.ute.studentconsulting.entity.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AnswerService {
    void save(Answer answer);
    Answer findById(String id);
    Page<Answer> findAllByApprovedIs(boolean approved, Pageable pageable);
}

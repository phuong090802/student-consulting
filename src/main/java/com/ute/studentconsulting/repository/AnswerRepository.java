package com.ute.studentconsulting.repository;

import com.ute.studentconsulting.entity.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, String> {
    Page<Answer> findAllByApprovedIs(Boolean approved, Pageable pageable);
}

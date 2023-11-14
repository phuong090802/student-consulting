package com.ute.studentconsulting.repository;

import com.ute.studentconsulting.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, String> {
}

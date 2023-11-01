package com.ute.studentconsulting.repository;

import com.ute.studentconsulting.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, String> {
}

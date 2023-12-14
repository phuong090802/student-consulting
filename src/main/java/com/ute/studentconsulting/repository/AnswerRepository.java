package com.ute.studentconsulting.repository;

import com.ute.studentconsulting.entity.Answer;
import com.ute.studentconsulting.entity.Question;
import com.ute.studentconsulting.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, String> {
    List<Answer> findAllByStaffInAndApprovedIs(Collection<User> staffs, Boolean approved);
    void deleteByQuestion(Question question);
}

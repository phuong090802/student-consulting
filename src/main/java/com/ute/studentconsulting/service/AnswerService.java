package com.ute.studentconsulting.service;

import com.ute.studentconsulting.entity.Answer;
import com.ute.studentconsulting.entity.Question;
import com.ute.studentconsulting.entity.User;

import java.util.Collection;
import java.util.List;

public interface AnswerService {
    void save(Answer answer);

    Answer findById(String id);

    List<Answer> findAllByStaffInAndApprovedIs(Collection<User> staffs, Boolean approved);

    void deleteByQuestion(Question question);
}

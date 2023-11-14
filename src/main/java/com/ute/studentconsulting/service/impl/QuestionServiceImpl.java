package com.ute.studentconsulting.service.impl;

import com.ute.studentconsulting.entity.Department;
import com.ute.studentconsulting.entity.Field;
import com.ute.studentconsulting.entity.Question;
import com.ute.studentconsulting.exception.QuestionException;
import com.ute.studentconsulting.repository.QuestionRepository;
import com.ute.studentconsulting.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    @Override
    public Page<Question> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndDepartmentIs(String value, Department department, Pageable pageable) {
        return questionRepository
                .findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndDepartmentIs(value, department, pageable);
    }

    @Override
    public Page<Question> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndFieldIs(String value, Field field, Pageable pageable) {
        return questionRepository
                .findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndFieldIs(value, field, pageable);
    }

    @Override
    public Page<Question> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String value, Pageable pageable) {
        return questionRepository
                .findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(value, value, pageable);
    }

    @Override
    public Question findById(String id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new QuestionException("Không tìm thấy câu hỏi"));
    }

    @Override
    public Page<Question> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndDepartmentIsAndFieldIs(String value, Department department, Field field, Pageable pageable) {
        return
                questionRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndDepartmentIsAndFieldIs(value, department, field, pageable);
    }

    private final QuestionRepository questionRepository;

    @Override
    @Transactional
    public void save(Question question) {
        questionRepository.save(question);
    }

    @Override
    public Page<Question> findByStatusIsAndFieldIs(Boolean status, Field field, Pageable pageable) {
        return questionRepository.findByStatusIsAndFieldIs(status, field, pageable);
    }

    @Override
    public Page<Question> findAllByFieldIs(Field field, Pageable pageable) {
        return questionRepository
                .findAllByFieldIs(field, pageable);
    }

    @Override
    public Page<Question> findByStatusIsAndFieldIdIn(Boolean status, Collection<String> ids, Pageable pageable) {
        return questionRepository
                .findByStatusIsAndFieldIdIn(status, ids, pageable);
    }


    @Override
    public boolean existsByStatusIsAndFieldIdIn(Boolean status, Collection<String> ids) {
        return questionRepository
                .existsByStatusIsAndFieldIdIn(status, ids);
    }

    @Override
    public boolean existsByStatusIsAndFieldIs(Boolean status, Field field) {
        return questionRepository
                .existsByStatusIsAndFieldIs(status, field);
    }

    @Override
    public Page<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable);
    }

    @Override
    public Page<Question> findAllByDepartmentIsAndFieldIs(Department department, Field field, Pageable pageable) {
        return questionRepository
                .findAllByDepartmentIsAndFieldIs(department, field, pageable);
    }
}

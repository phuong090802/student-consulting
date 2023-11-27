package com.ute.studentconsulting.service;

import com.ute.studentconsulting.entity.Department;
import com.ute.studentconsulting.entity.Field;
import com.ute.studentconsulting.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;

public interface QuestionService {
    void save(Question question);

    Page<Question> findByStatusIsAndFieldIs(int status, Field field, Pageable pageable);

    Page<Question> findByStatusIsAndFieldIdIn(int status, Collection<String> ids, Pageable pageable);

    boolean existsByStatusIsAndFieldIdIn(int status, Collection<String> ids);

    boolean existsByStatusIsAndFieldIs(int status, Field field);

    Page<Question> findAll(Pageable pageable);

    Page<Question> findAllByDepartmentIsAndFieldIs(Department department, Field field, Pageable pageable);

    Page<Question> findAllByFieldIs(Field field, Pageable pageable);

    Page<Question> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndDepartmentIs
            (String value, Department department, Pageable pageable);

    Page<Question> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndDepartmentIsAndFieldIs
            (String value, Department department, Field field, Pageable pageable);

    Page<Question> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndFieldIs
            (String value, Field field, Pageable pageable);

    Page<Question> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase
            (String value, Pageable pageable);
    Question findById(String id);
}

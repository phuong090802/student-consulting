package com.ute.studentconsulting.service;

import com.ute.studentconsulting.entity.Answer;
import com.ute.studentconsulting.entity.Department;
import com.ute.studentconsulting.entity.Field;
import com.ute.studentconsulting.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface QuestionService {
    void save(Question question);

    Page<Question> findByStatusIsAndFieldIs(int status, Field field, Pageable pageable);

    Page<Question> findByStatusIsAndFieldIdIn(int status, Collection<String> ids, Pageable pageable);

    boolean existsByStatusIsAndFieldIdIn(int status, Collection<String> ids);

    boolean existsByStatusIsAndFieldIs(int status, Field field);

    Page<Question> findAllByStatusIsNot(int status, Pageable pageable);

    Page<Question> findAllByFieldIsAndStatusIsNot(Field field, int status, Pageable pageable);

    Page<Question> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndDepartmentIsAndStatusIsNot
            (String value, Department department, int status, Pageable pageable);

    Page<Question> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndDepartmentIsAndFieldIsAndStatusIsNot
            (String value, Department department, Field field, int status, Pageable pageable);

    Page<Question> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndFieldIsAndStatusIsNot
            (String value, Field field, int status, Pageable pageable);

    Page<Question> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndStatusIsNot
            (String value, int status, Pageable pageable);

    Question findById(String id);

    Question findByIdAndStatusIsNot(String id, int status);

    Page<Question> findAllByDepartmentIsAndStatusIsNot(Department department, Integer status, Pageable pageable);

    Page<Question> findAllByFieldIsAndDepartmentIsAndStatusIsNot
            (Field field, Department department, Integer status, Pageable pageable);

    List<Question> findAllByDateBefore(Date date);

    void deleteById(String id);

    Page<Question> findAllByAnswerInAndStatusIs(Collection<Answer> answers, Integer status, Pageable pageable);
    Question findByIdAndStatusIs(String id, Integer status);


}

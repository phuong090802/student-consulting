package com.ute.studentconsulting.service.impl;

import com.ute.studentconsulting.entity.Answer;
import com.ute.studentconsulting.entity.Department;
import com.ute.studentconsulting.entity.Field;
import com.ute.studentconsulting.entity.Question;
import com.ute.studentconsulting.exception.NotFoundException;
import com.ute.studentconsulting.repository.QuestionRepository;
import com.ute.studentconsulting.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    @Override
    public Page<Question> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndDepartmentIsAndStatusIsNot
            (String value, Department department, int status, Pageable pageable) {
        return questionRepository
                .findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndDepartmentIsAndStatusIsNot
                        (value, department, status, pageable);
    }

    @Override
    public Question findByIdAndStatusIs(String id, Integer status) {
        return questionRepository.findByIdAndStatusIs(id, status)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy câu hỏi",
                        "Không tìm thấy câu hỏi với mã: %s và trạng thái là: %s".formatted(id, status), 10089));
    }

    @Override
    public Page<Question> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndFieldIsAndStatusIsNot
            (String value, Field field, int status, Pageable pageable) {
        return questionRepository
                .findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndFieldIsAndStatusIsNot(value, field, status, pageable);
    }

    @Override
    public Page<Question> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndStatusIsNot
            (String value, int status, Pageable pageable) {
        return questionRepository
                .findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndStatusIsNot(value, value, status, pageable);
    }

    @Override
    public Question findById(String id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy câu hỏi",
                        "Không tìm thấy câu hỏi với mã: %s".formatted(id), 10004));
    }

    @Override
    public Question findByIdAndStatusIsNot(String id, int status) {
        return questionRepository.findByIdAndStatusIsNot(id, status)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy câu hỏi",
                        "Không tìm thấy câu hỏi với mã: %s và trạng thái không phải là: %s".formatted(id, status), 10004));
    }

    @Override
    public Page<Question> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndDepartmentIsAndFieldIsAndStatusIsNot
            (String value, Department department, Field field, int status, Pageable pageable) {
        return questionRepository
                .findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndDepartmentIsAndFieldIsAndStatusIsNot
                        (value, department, field, status, pageable);
    }

    private final QuestionRepository questionRepository;

    @Override
    public Page<Question> findAllByAnswerInAndStatusIs(Collection<Answer> answers, Integer status, Pageable pageable) {
        return questionRepository.findAllByAnswerInAndStatusIs(answers, status, pageable);
    }

    @Override
    @Transactional
    public void save(Question question) {
        questionRepository.save(question);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        questionRepository.deleteById(id);
    }

    @Override
    public Page<Question> findByStatusIsAndFieldIs(int status, Field field, Pageable pageable) {
        return questionRepository.findByStatusIsAndFieldIs(status, field, pageable);
    }

    @Override
    public Page<Question> findAllByFieldIsAndStatusIsNot(Field field, int status, Pageable pageable) {
        return questionRepository
                .findAllByFieldIsAndStatusIsNot(field, status, pageable);
    }

    @Override
    public Page<Question> findByStatusIsAndFieldIdIn(int status, Collection<String> ids, Pageable pageable) {
        return questionRepository
                .findByStatusIsAndFieldIdIn(status, ids, pageable);
    }


    @Override
    public boolean existsByStatusIsAndFieldIdIn(int status, Collection<String> ids) {
        return questionRepository
                .existsByStatusIsAndFieldIdIn(status, ids);
    }

    @Override
    public Page<Question> findAllByDepartmentIsAndStatusIsNot
            (Department department, Integer status, Pageable pageable) {
        return questionRepository.findAllByDepartmentIsAndStatusIsNot
                (department, status, pageable);
    }

    @Override
    public Page<Question> findAllByFieldIsAndDepartmentIsAndStatusIsNot
            (Field field, Department department, Integer status, Pageable pageable) {
        return questionRepository
                .findAllByFieldIsAndDepartmentIsAndStatusIsNot
                        (field, department, status, pageable);
    }

    @Override
    public List<Question> findAllByDateBefore(Date date) {
        return questionRepository.findAllByDateBefore(date);
    }

    @Override
    public boolean existsByStatusIsAndFieldIs(int status, Field field) {
        return questionRepository
                .existsByStatusIsAndFieldIs(status, field);
    }

    @Override
    public Page<Question> findAllByStatusIsNot(int status, Pageable pageable) {
        return questionRepository.findAllByStatusIsNot(status, pageable);
    }

}

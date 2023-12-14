package com.ute.studentconsulting.repository;

import com.ute.studentconsulting.entity.Answer;
import com.ute.studentconsulting.entity.Department;
import com.ute.studentconsulting.entity.Field;
import com.ute.studentconsulting.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, String> {

    Page<Question> findByStatusIsAndFieldIs(Integer status, Field field, Pageable pageable);

    Page<Question> findByStatusIsAndFieldIdIn(Integer status, Collection<String> ids, Pageable pageable);

    Boolean existsByStatusIsAndFieldIdIn(Integer status, Collection<String> ids);

    Boolean existsByStatusIsAndFieldIs(Integer status, Field field);

    Page<Question> findAllByFieldIsAndStatusIsNot(Field field, Integer status, Pageable pageable);

    @Query("SELECT q FROM Question q WHERE (LOWER(q.title) LIKE %:value% OR LOWER(q.content) LIKE %:value%) " +
            "AND q.department = :department AND q.status <> :status ")
    Page<Question> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndDepartmentIsAndStatusIsNot
            (@Param("value") String value, @Param("department") Department department, @Param("status") Integer status, Pageable pageable);

    @Query("SELECT q FROM Question q WHERE (LOWER(q.title) LIKE %:value% OR LOWER(q.content) LIKE %:value%) " +
            "AND q.department = :department AND q.field = :field AND q.status <> :status ")
    Page<Question> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndDepartmentIsAndFieldIsAndStatusIsNot
            (@Param("value") String value, @Param("department") Department department, @Param("field") Field field, @Param("status") Integer status, Pageable pageable);

    @Query("SELECT q FROM Question q WHERE (LOWER(q.title) LIKE %:value% OR LOWER(q.content) LIKE %:value%) " +
            "AND q.field = :field AND q.status <> :status ")
    Page<Question> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndFieldIsAndStatusIsNot
            (@Param("value") String value, @Param("field") Field field, @Param("status") Integer status, Pageable pageable);

    Page<Question> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndStatusIsNot
            (String value1, String value2, Integer status, Pageable pageable);

    Optional<Question> findByIdAndStatusIsNot(String id, Integer status);

    Page<Question> findAllByStatusIsNot(Integer status, Pageable pageable);

    Page<Question> findAllByDepartmentIsAndStatusIsNot(Department department, Integer status, Pageable pageable);
    Page<Question> findAllByFieldIsAndDepartmentIsAndStatusIsNot
            (Field field, Department department, Integer status, Pageable pageable);
    List<Question> findAllByDateBefore(Date date);
    Page<Question> findAllByAnswerInAndStatusIs(Collection<Answer> answers, Integer status, Pageable pageable);

    Optional<Question> findByIdAndStatusIs(String id, Integer status);

}

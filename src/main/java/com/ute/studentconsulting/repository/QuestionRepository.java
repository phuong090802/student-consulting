package com.ute.studentconsulting.repository;

import com.ute.studentconsulting.entity.Department;
import com.ute.studentconsulting.entity.Field;
import com.ute.studentconsulting.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface QuestionRepository extends JpaRepository<Question, String> {

    Page<Question> findByStatusIsAndFieldIs(Boolean status, Field field, Pageable pageable);

    Page<Question> findByStatusIsAndFieldIdIn(Boolean status, Collection<String> ids, Pageable pageable);

    Boolean existsByStatusIsAndFieldIdIn(Boolean status, Collection<String> ids);

    Boolean existsByStatusIsAndFieldIs(Boolean status, Field field);

    Page<Question> findAllByDepartmentIsAndFieldIs(Department department, Field field, Pageable pageable);

    Page<Question> findAllByFieldIs(Field field, Pageable pageable);

    @Query("SELECT q FROM Question q WHERE (LOWER(q.title) LIKE %:value% OR LOWER(q.content) LIKE %:value%) " +
            "AND q.department = :department ")
    Page<Question> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndDepartmentIs
            (@Param("value") String value, @Param("department") Department department, Pageable pageable);

    @Query("SELECT q FROM Question q WHERE (LOWER(q.title) LIKE %:value% OR LOWER(q.content) LIKE %:value%) " +
            "AND q.department = :department AND q.field = :field ")
    Page<Question> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndDepartmentIsAndFieldIs
            (@Param("value") String value, @Param("department") Department department, @Param("field") Field field, Pageable pageable);

    @Query("SELECT q FROM Question q WHERE (LOWER(q.title) LIKE %:value% OR LOWER(q.content) LIKE %:value%) " +
            "AND q.field = :field ")
    Page<Question> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndFieldIs
            (@Param("value") String value, @Param("field") Field field, Pageable pageable);

    Page<Question> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase
            (String value1, String value2, Pageable pageable);
}

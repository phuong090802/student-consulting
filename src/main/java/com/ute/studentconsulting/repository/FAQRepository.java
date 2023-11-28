package com.ute.studentconsulting.repository;

import com.ute.studentconsulting.entity.Department;
import com.ute.studentconsulting.entity.FAQ;
import com.ute.studentconsulting.entity.Field;
import com.ute.studentconsulting.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FAQRepository extends JpaRepository<FAQ, String> {
    Page<FAQ> findAllByFieldIs(Field field, Pageable pageable);

    @Query("SELECT f FROM FAQ f WHERE (LOWER(f.title) LIKE %:value% OR LOWER(f.content) LIKE %:value%) " +
            "AND f.field = :field ")
    Page<FAQ> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndFieldIs
            (@Param("value") String value, @Param("field") Field field, Pageable pageable);

    Page<FAQ> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String value1, String value2, Pageable pageable);

    Page<FAQ> findAllByFieldIsAndDepartmentIs(Field field, Department department, Pageable pageable);

    Page<FAQ> findAllByDepartmentIs(Department department, Pageable pageable);

    @Query("SELECT f FROM FAQ f WHERE (LOWER(f.title) LIKE %:value% OR LOWER(f.content) LIKE %:value%) " +
            "AND f.department = :department ")
    Page<FAQ> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndDepartmentIs
            (@Param("value") String value, @Param("department") Department department, Pageable pageable);

    @Query("SELECT f FROM FAQ f WHERE (LOWER(f.title) LIKE %:value% OR LOWER(f.content) LIKE %:value%) " +
            "AND f.field = :field AND f.department = :department ")
    Page<FAQ> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndFieldIsAndDepartmentIs
            (@Param("value") String value, @Param("field") Field field, @Param("department") Department department, Pageable pageable);

}

package com.ute.studentconsulting.repository;

import com.ute.studentconsulting.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, String> {
    Boolean existsByName(String name);

    Page<Department> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase
            (String value1, String value2, Pageable pageable);

    Boolean existsByNameAndIdIsNot(String name, String id);

    @Query("SELECT d FROM Department d " +
            "WHERE (LOWER(d.name) LIKE LOWER(concat('%', :value, '%')) " +
            "OR LOWER(d.description) LIKE LOWER(concat('%', :value, '%'))) " +
            "AND d.status = :status")
    Page<Department> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndStatusIs
            (@Param("value") String value, @Param("status") Boolean status, Pageable pageable);

    Page<Department> findAllByStatusIs(Boolean status, Pageable pageable);

    List<Department> findAllByStatusIs(Boolean status);

    Optional<Department> findByIdAndStatusIs(String id, Boolean status);

}

package com.ute.studentconsulting.service;

import com.ute.studentconsulting.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DepartmentService {
    boolean existsByName(String name);

    void save(Department department);

    Page<Department> findAll(Pageable pageable);

    Page<Department> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String value, Pageable pageable);

    Department findById(String id);

    boolean existsByNameAndIdIsNot(String name, String id);

    Page<Department> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndStatusIsTrue(String value, Pageable pageable);

    Page<Department> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndStatusIsFalse(String value, Pageable pageable);

    Page<Department> findAllByStatusIsTrue(Pageable pageable);

    Page<Department> findAllByStatusIsFalse(Pageable pageable);

    List<Department> findAllByStatusIsTrue();

    Department findByIdAndStatusIsTrue(String id);
}

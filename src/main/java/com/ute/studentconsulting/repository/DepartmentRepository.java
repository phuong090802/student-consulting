package com.ute.studentconsulting.repository;

import com.ute.studentconsulting.entity.Department;
import com.ute.studentconsulting.entity.Field;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, String> {
    Boolean existsByName(String name);

    Page<Department> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String value1, String value2, Pageable pageable);

    Boolean existsByNameAndIdIsNot(String name, String id);

    Page<Department> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndStatusIsTrue(String value1, String value2, Pageable pageable);

    Page<Department> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndStatusIsFalse(String value1, String value2, Pageable pageable);

    Page<Department> findAllByStatusIsTrue(Pageable pageable);

    Page<Department> findAllByStatusIsFalse(Pageable pageable);

    List<Department> findAllByIdIsNotInAndStatusIsTrue(Collection<String> ids);

    @NonNull
    List<Department> findAllByStatusIsTrue();

}

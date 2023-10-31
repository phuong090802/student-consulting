package com.ute.studentconsulting.service.impl;

import com.ute.studentconsulting.entity.Department;
import com.ute.studentconsulting.exception.DepartmentException;
import com.ute.studentconsulting.repository.DepartmentRepository;
import com.ute.studentconsulting.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepository;

    @Override
    public boolean existsByName(String name) {
        return departmentRepository.existsByName(name);
    }

    @Override
    public void save(Department department) {
        departmentRepository.save(department);
    }

    @Override
    public Page<Department> findAll(Pageable pageable) {
        return departmentRepository.findAll(pageable);
    }

    @Override
    public Page<Department> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String value, Pageable pageable) {
        return departmentRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(value, value, pageable);
    }

    @Override
    public Department findById(String id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new DepartmentException("Không tìm thấy phòng ban."));
    }

    @Override
    public boolean existsByNameAndIdIsNot(String name, String id) {
        return departmentRepository.existsByNameAndIdIsNot(name, id);
    }

    @Override
    public Page<Department> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndStatusIsTrue(String value, Pageable pageable) {
        return departmentRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndStatusIsTrue(value, value, pageable);
    }

    @Override
    public Page<Department> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndStatusIsFalse(String value, Pageable pageable) {
        return departmentRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndStatusIsFalse(value, value, pageable);
    }

    @Override
    public Page<Department> findAllByStatusIsTrue(Pageable pageable) {
        return departmentRepository.findAllByStatusIsTrue(pageable);
    }

    @Override
    public Page<Department> findAllByStatusIsFalse(Pageable pageable) {
        return departmentRepository.findAllByStatusIsFalse(pageable);
    }

    @Override
    public List<Department> findAllByIdIsNotInAndStatusIsTrue(Collection<String> ids) {
        return departmentRepository.findAllByIdIsNotInAndStatusIsTrue(ids);
    }

    @Override
    public List<Department> findAllByStatusIsTrue() {
        return departmentRepository.findAllByStatusIsTrue();
    }
}

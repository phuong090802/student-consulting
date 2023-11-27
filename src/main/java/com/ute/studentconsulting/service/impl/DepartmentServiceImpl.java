package com.ute.studentconsulting.service.impl;

import com.ute.studentconsulting.entity.Department;
import com.ute.studentconsulting.exception.DepartmentException;
import com.ute.studentconsulting.repository.DepartmentRepository;
import com.ute.studentconsulting.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
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
                .orElseThrow(() -> new DepartmentException(
                        "Không tìm thấy khoa",
                        "Không tìm thấy khoa với id: " + id, 10001));
    }

    @Override
    public boolean existsByNameAndIdIsNot(String name, String id) {
        return departmentRepository.existsByNameAndIdIsNot(name, id);
    }

    @Override
    public Page<Department> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndStatusIsTrue(String value, Pageable pageable) {
        return departmentRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndStatusIsTrue(
                        value, value, pageable);
    }

    @Override
    public Page<Department> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndStatusIsFalse(String value, Pageable pageable) {
        return departmentRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndStatusIsFalse
                        (value, value, pageable);
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
    public Department findByIdAndStatusIsTrue(String id) {
        return departmentRepository.findByIdAndStatusIsTrue(id)
                .orElseThrow(() -> new DepartmentException(
                        "Không tìm thấy khoa có trạng thái hoạt động",
                        "Không tìm thấy khoa có trạng thái hoạt động với id: " + id, 10002));
    }

    @Override
    public List<Department> findAllByStatusIsTrue() {
        return departmentRepository.findAllByStatusIsTrue();
    }
}

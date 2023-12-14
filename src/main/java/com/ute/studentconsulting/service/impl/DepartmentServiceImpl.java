package com.ute.studentconsulting.service.impl;

import com.ute.studentconsulting.entity.Department;
import com.ute.studentconsulting.exception.NotFoundException;
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
    public Page<Department> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase
            (String value, Pageable pageable) {
        return departmentRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(value, value, pageable);
    }

    @Override
    public Department findById(String id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Không tìm thấy khoa",
                        "Không tìm thấy khoa với mã: %s".formatted(id), 10001));
    }

    @Override
    public boolean existsByNameAndIdIsNot(String name, String id) {
        return departmentRepository.existsByNameAndIdIsNot(name, id);
    }

    @Override
    public Page<Department> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndStatusIs
            (String value, boolean status, Pageable pageable) {
        return departmentRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndStatusIs
                        (value, status, pageable);
    }


    @Override
    public Page<Department> findAllByStatusIs(boolean status, Pageable pageable) {
        return departmentRepository.findAllByStatusIs(status, pageable);
    }


    @Override
    public Department findByIdAndStatusIs(String id, boolean status) {
        return departmentRepository.findByIdAndStatusIs(id, status)
                .orElseThrow(() -> new NotFoundException(
                        "Không tìm thấy khoa có trạng thái hoạt động",
                        "Không tìm thấy khoa có trạng thái hoạt động với id: %s".formatted(id), 10002));
    }

    @Override
    public List<Department> findAllByStatusIs(boolean status) {
        return departmentRepository.findAllByStatusIs(status);
    }
}

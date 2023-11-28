package com.ute.studentconsulting.service;

import com.ute.studentconsulting.entity.Department;
import com.ute.studentconsulting.entity.FAQ;
import com.ute.studentconsulting.entity.Field;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FAQService {
    void save(FAQ faq);

    FAQ findById(String id);

    void deleteById(String id);

    Page<FAQ> findAllByFieldIs(Field field, Pageable pageable);

    Page<FAQ> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndFieldIs
            (String value, Field field, Pageable pageable);

    Page<FAQ> findAll(Pageable pageable);

    Page<FAQ> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String value, Pageable pageable);

    Page<FAQ> findAllByFieldIsAndDepartmentIs(Field field, Department department, Pageable pageable);

    Page<FAQ> findAllByDepartmentIs(Department department, Pageable pageable);

    Page<FAQ> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndDepartmentIs
            (String value, Department department, Pageable pageable);

    Page<FAQ> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndFieldIsAndDepartmentIs
            (String value, Field field, Department department, Pageable pageable);

}

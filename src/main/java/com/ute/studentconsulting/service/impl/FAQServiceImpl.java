package com.ute.studentconsulting.service.impl;

import com.ute.studentconsulting.entity.Department;
import com.ute.studentconsulting.entity.FAQ;
import com.ute.studentconsulting.entity.Field;
import com.ute.studentconsulting.exception.NotFoundException;
import com.ute.studentconsulting.repository.FAQRepository;
import com.ute.studentconsulting.service.FAQService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FAQServiceImpl implements FAQService {
    private final FAQRepository faqRepository;

    @Override
    public Page<FAQ> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndDepartmentIs
            (String value, Department department, Pageable pageable) {
        return faqRepository
                .findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndDepartmentIs(value, department, pageable);
    }

    @Override
    public Page<FAQ> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndFieldIsAndDepartmentIs
            (String value, Field field, Department department, Pageable pageable) {
        return faqRepository
                .findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndFieldIsAndDepartmentIs
                        (value, field, department, pageable);
    }

    @Override
    public Page<FAQ> findAllByDepartmentIs(Department department, Pageable pageable) {
        return faqRepository.findAllByDepartmentIs(department, pageable);
    }

    @Override
    public Page<FAQ> findAllByFieldIsAndDepartmentIs(Field field, Department department, Pageable pageable) {
        return faqRepository
                .findAllByFieldIsAndDepartmentIs(field, department, pageable);
    }

    @Override
    public FAQ findById(String id) {
        return faqRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy câu hỏi chung",
                        "Không tìm thấy câu hỏi chung với mã %s: ".formatted(id), 10079));
    }

    @Override
    public Page<FAQ> findAll(Pageable pageable) {
        return faqRepository.findAll(pageable);
    }

    @Override
    public Page<FAQ> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String value, Pageable pageable) {
        return faqRepository
                .findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(value, value, pageable);
    }

    @Override
    public void deleteById(String id) {
        faqRepository.deleteById(id);
    }

    @Override
    public Page<FAQ> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndFieldIs
            (String value, Field field, Pageable pageable) {
        return faqRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndFieldIs(value, field, pageable);
    }

    @Override
    public Page<FAQ> findAllByFieldIs(Field field, Pageable pageable) {
        return faqRepository.findAllByFieldIs(field, pageable);
    }

    @Override
    @Transactional
    public void save(FAQ faq) {
        faqRepository.save(faq);
    }
}

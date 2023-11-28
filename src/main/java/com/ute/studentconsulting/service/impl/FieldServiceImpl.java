package com.ute.studentconsulting.service.impl;

import com.ute.studentconsulting.entity.Field;
import com.ute.studentconsulting.exception.NotFoundException;
import com.ute.studentconsulting.repository.FieldRepository;
import com.ute.studentconsulting.service.FieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Service
public class FieldServiceImpl implements FieldService {
    private final FieldRepository fieldRepository;

    @Override
    public Page<Field> findAllByIdIn(Pageable pageable, Collection<String> ids) {
        return fieldRepository.findAllByIdIn(pageable, ids);
    }

    @Override
    public boolean existsByName(String name) {
        return fieldRepository.existsByName(name);
    }

    @Override
    public List<Field> findAllByIdInAndStatusIs(Collection<String> ids, boolean status) {
        return fieldRepository.findAllByIdInAndStatusIs(ids, status);
    }

    @Override
    public List<Field> findAllByIdIn(Collection<String> ids) {
        return fieldRepository.findAllByIdIn(ids);
    }

    @Override
    @Transactional
    public void save(Field field) {
        fieldRepository.save(field);
    }

    @Override
    public Boolean existsByNameAndIdIsNot(String name, String id) {
        return fieldRepository.existsByNameAndIdIsNot(name, id);
    }

    @Override
    public List<Field> findAllByIdIsNotInAndStatusIs(Collection<String> ids, boolean status) {
        return fieldRepository.findAllByIdIsNotInAndStatusIs(ids, status);
    }

    @Override
    public List<Field> findAllByStatusIs(boolean status) {
        return fieldRepository.findAllByStatusIs(status);
    }

    @Override
    public Page<Field> findAllByStatusIs(boolean status, Pageable pageable) {
        return fieldRepository.findAllByStatusIs(status, pageable);
    }

    @Override
    public Page<Field> findByNameContainingIgnoreCase(String value, Pageable pageable) {
        return fieldRepository.findByNameContainingIgnoreCase(value, pageable);
    }

    @Override
    public Page<Field> findByNameContainingIgnoreCaseAndStatusIs
            (String value, boolean status, Pageable pageable) {
        return fieldRepository
                .findByNameContainingIgnoreCaseAndStatusIs(value, status, pageable);
    }

    @Override
    public Field findById(String id) {
        return fieldRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Không tìm thấy lĩnh vực",
                        "Không tìm thấy lĩnh vực với id: %s" .formatted(id),
                        10003));
    }


    @Override
    public Page<Field> findByNameContainingIgnoreCaseAndIdIn(String value, Collection<String> ids, Pageable pageable) {
        return fieldRepository.findByNameContainingIgnoreCaseAndIdIn(value, ids, pageable);
    }

    @Override
    public Page<Field> findAll(Pageable pageable) {
        return fieldRepository.findAll(pageable);
    }

}

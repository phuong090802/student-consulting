package com.ute.studentconsulting.service.impl;

import com.ute.studentconsulting.entity.Field;
import com.ute.studentconsulting.exception.FieldException;
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
    public List<Field> findAllByIdIsNotIn(Collection<String> ids) {
        return fieldRepository.findAllByIdIsNotIn(ids);
    }

    @Override
    public List<Field> findAll() {
        return fieldRepository.findAll();
    }

    @Override
    public Field findById(String id) {
        return fieldRepository.findById(id)
                .orElseThrow(() -> new FieldException("Không tìm thấy lĩnh vực"));
    }


    @Override
    public Page<Field> findByNameContainingAndIdIn(String value, Collection<String> ids, Pageable pageable) {
        return fieldRepository.findByNameContainingAndIdIn(value, ids, pageable);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        fieldRepository.deleteById(id);
    }

    @Override
    public Page<Field> findByNameContaining(String value, Pageable pageable) {
        return fieldRepository.findByNameContaining(value, pageable);
    }

    @Override
    public Page<Field> findAll(Pageable pageable) {
        return fieldRepository.findAll(pageable);
    }

}

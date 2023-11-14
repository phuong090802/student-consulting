package com.ute.studentconsulting.service;

import com.ute.studentconsulting.entity.Field;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

public interface FieldService {
    boolean existsByName(String name);

    void save(Field field);

    Boolean existsByNameAndIdIsNot(String name, String id);

    Field findById(String id);

    void deleteById(String id);

    Page<Field> findByNameContaining(String value, Pageable pageable);

    Page<Field> findAll(Pageable pageable);

    List<Field> findAllByIdIn(Collection<String> ids);

    Page<Field> findAllByIdIn(Pageable pageable, Collection<String> ids);

    Page<Field> findByNameContainingAndIdIn(String value, Collection<String> ids, Pageable pageable);

    List<Field> findAllByIdIsNotIn(Collection<String> ids);
    List<Field> findAll();

}

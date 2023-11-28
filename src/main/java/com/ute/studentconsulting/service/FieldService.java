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

    Page<Field> findAll(Pageable pageable);

    List<Field> findAllByIdInAndStatusIs(Collection<String> ids, boolean status);

    Page<Field> findAllByIdIn(Pageable pageable, Collection<String> ids);

    Page<Field> findByNameContainingIgnoreCaseAndIdIn
            (String value, Collection<String> ids, Pageable pageable);

    List<Field> findAllByIdIsNotInAndStatusIs(Collection<String> ids, boolean status);

    List<Field> findAllByStatusIs(boolean status);

    Page<Field> findAllByStatusIs(boolean status, Pageable pageable);

    Page<Field> findByNameContainingIgnoreCaseAndStatusIs(String value, boolean status, Pageable pageable);

    Page<Field> findByNameContainingIgnoreCase(String value, Pageable pageable);

    List<Field> findAllByIdIn(Collection<String> ids);

}

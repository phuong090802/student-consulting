package com.ute.studentconsulting.repository;

import com.ute.studentconsulting.entity.Field;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface FieldRepository extends JpaRepository<Field, String> {
    Boolean existsByName(String name);

    Boolean existsByNameAndIdIsNot(String name, String id);

    Page<Field> findByNameContaining(String value, Pageable pageable);

    List<Field> findAllByIdIn(Collection<String> ids);

    Page<Field> findAllByIdIn(Pageable pageable, Collection<String> ids);

    Page<Field> findByNameContainingAndIdIn(String value, Collection<String> ids, Pageable pageable);
    List<Field> findAllByIdIsNotIn(Collection<String> ids);
}

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

    List<Field> findAllByIdInAndStatusIs(Collection<String> ids, Boolean status);

    Page<Field> findAllByIdIn(Pageable pageable, Collection<String> ids);

    Page<Field> findByNameContainingIgnoreCaseAndIdIn(String value, Collection<String> ids, Pageable pageable);

    List<Field> findAllByIdIsNotInAndStatusIs(Collection<String> ids, Boolean status);

    Page<Field> findAllByStatusIs(Boolean status, Pageable pageable);

    Page<Field> findByNameContainingIgnoreCaseAndStatusIs(String value, Boolean status, Pageable pageable);

    Page<Field> findByNameContainingIgnoreCase(String value, Pageable pageable);

    List<Field> findAllByStatusIs(Boolean status);

    List<Field> findAllByIdIn(Collection<String> ids);
}

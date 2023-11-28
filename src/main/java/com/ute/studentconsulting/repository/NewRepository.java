package com.ute.studentconsulting.repository;

import com.ute.studentconsulting.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewRepository extends JpaRepository<News, String> {
    Page<News> findAllByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String value1, String value2, Pageable pageable);

}

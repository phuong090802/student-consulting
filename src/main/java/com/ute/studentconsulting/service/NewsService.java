package com.ute.studentconsulting.service;

import com.ute.studentconsulting.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NewsService {
    void save(News news);
    News findById(String id);
    void deleteById(String id);
    Page<News> findAllByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String value, Pageable pageable);
    Page<News> findAll(Pageable pageable);
}

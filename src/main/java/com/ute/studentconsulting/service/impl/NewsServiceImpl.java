package com.ute.studentconsulting.service.impl;

import com.ute.studentconsulting.entity.News;
import com.ute.studentconsulting.exception.NotFoundException;
import com.ute.studentconsulting.repository.NewRepository;
import com.ute.studentconsulting.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {
    private final NewRepository newRepository;

    @Override
    public Page<News> findAll(Pageable pageable) {
        return newRepository.findAll(pageable);
    }

    @Override
    public Page<News> findAllByTitleContainingIgnoreCaseOrContentContainingIgnoreCase
            (String value, Pageable pageable) {
        return newRepository.findAllByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(value, value, pageable);
    }

    @Override
    public void deleteById(String id) {
        newRepository.deleteById(id);
    }

    @Override
    public News findById(String id) {
        return newRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy thông báo", "Không tìm thấy thông báo với mã: %s".formatted(id), 10084));
    }

    @Override
    @Transactional
    public void save(News news) {
        newRepository.save(news);
    }
}

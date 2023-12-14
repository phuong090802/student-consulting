package com.ute.studentconsulting.service.impl;

import com.ute.studentconsulting.entity.RefreshToken;
import com.ute.studentconsulting.entity.User;
import com.ute.studentconsulting.repository.RefreshTokenRepository;
import com.ute.studentconsulting.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void deleteById(String id) {
        refreshTokenRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }

    @Override
    @Transactional
    public RefreshToken save(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken findById(String token) {
        return refreshTokenRepository.findById(token)
                .orElse(null);
    }

    @Override
    @Transactional
    public void deleteByParent(RefreshToken parent) {
        refreshTokenRepository.deleteByParent(parent);
    }

}

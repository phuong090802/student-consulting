package com.ute.studentconsulting.service;

import com.ute.studentconsulting.entity.RefreshToken;

public interface RefreshTokenService {
    RefreshToken save(RefreshToken refreshToken);
    RefreshToken findById(String token);
    void deleteById(String id);
    void deleteByParent(RefreshToken parent);
}

package com.ute.studentconsulting.service;

import com.ute.studentconsulting.entity.RefreshToken;
import com.ute.studentconsulting.entity.User;

public interface RefreshTokenService {
    RefreshToken save(RefreshToken refreshToken);

    RefreshToken findById(String token);

    void deleteByParent(RefreshToken parent);

    void deleteByUser(User user);
    void deleteById(String id);
}

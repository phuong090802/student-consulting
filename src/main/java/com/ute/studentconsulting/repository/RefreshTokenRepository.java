package com.ute.studentconsulting.repository;

import com.ute.studentconsulting.entity.RefreshToken;
import com.ute.studentconsulting.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    void deleteByUser(User user);
    void deleteByParent(RefreshToken parent);
}

package com.ute.studentconsulting.repository;

import com.ute.studentconsulting.entity.RefreshToken;
import com.ute.studentconsulting.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    void deleteByParent(RefreshToken parent);
    void deleteByUser(User user);
}

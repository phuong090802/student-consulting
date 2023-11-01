package com.ute.studentconsulting.repository;

import com.ute.studentconsulting.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    void deleteByParent(RefreshToken parent);
}

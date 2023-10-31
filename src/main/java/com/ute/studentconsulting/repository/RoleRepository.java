package com.ute.studentconsulting.repository;

import com.ute.studentconsulting.entity.Role;
import com.ute.studentconsulting.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(RoleName name);
}


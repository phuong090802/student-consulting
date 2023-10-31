package com.ute.studentconsulting.service;

import com.ute.studentconsulting.entity.Role;
import com.ute.studentconsulting.entity.RoleName;

public interface RoleService {
    Role findByName(RoleName name);
}

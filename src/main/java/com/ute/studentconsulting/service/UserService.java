package com.ute.studentconsulting.service;

import com.ute.studentconsulting.entity.Department;
import com.ute.studentconsulting.entity.Role;
import com.ute.studentconsulting.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface UserService {
    void save(User user);

    User findByPhone(String phone);

    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);

    User findById(String id);

    Page<User> findAllByRoleIsNot(Pageable pageable, Role admin);

    Page<User> findAllByRoleIsNotAndOccupationNotIn
            (Pageable pageable, Role admin, Collection<String> occupations);

    Page<User> findAllByRoleIsNotAndOccupationEqualsIgnoreCase
            (Pageable pageable, Role admin, String occupation);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNot
            (String value, Pageable pageable, Role admin);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationNotIn
            (String value, Role admin, Collection<String> occupations, Pageable pageable);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationEqualsIgnoreCase
            (String value, Pageable pageable, Role admin, String occupation);

    Page<User> findAllByRoleIsNotAndRoleIs(Pageable pageable, Role admin, Role role);

    Page<User> findAllByRoleIsNotAndRoleIsAndOccupationNotIn
            (Pageable pageable, Role admin, Role role, Collection<String> occupations);

    Page<User> findAllByRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCase
            (Pageable pageable, Role admin, Role role, String occupation);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIs
            (String value, Role admin,Role role, Pageable pageable);
    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationNotIn
            (String value, Role admin, Collection<String> occupations, Role role, Pageable pageable);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCase
            (String value, Pageable pageable, Role admin, Role role, String occupation);

    Page<User> findAllByRoleIsNotAndEnabledIs(Pageable pageable, Role admin, boolean enabled);


    Page<User> findAllByRoleIsNotAndOccupationNotInAndEnabledIs
            (Pageable pageable, Role admin, Collection<String> occupations, boolean enabled);


    Page<User> findAllByRoleIsNotAndOccupationEqualsIgnoreCaseAndEnabledIs
            (Pageable pageable, Role admin, String occupation, boolean enabled);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndEnabledIs
            (String value, Role admin, boolean enabled, Pageable pageable);


    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationNotInAndEnableIs
            (String value, Role admin, Collection<String> occupations, Role role, boolean enabled, Pageable pageable);


    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCaseAndEnabledIs
            (String value, Role role, String occupation, boolean enabled, Pageable pageable);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationNotInAndEnabledIs
            (String value, Collection<String> occupations, boolean enabled, Pageable pageable);


    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationEqualsIgnoreCaseAndEnabledIs
            (String value, String occupation, boolean enabled, Pageable pageable);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndEnabledIs
            (String value, Role role, boolean enabled, Pageable pageable);

    Page<User> findAllByRoleIsNotAndRoleIsAndEnabledIs(
            Pageable pageable, Role admin, Role role, boolean enabled);


    Page<User> findAllByRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCaseAndEnabledIs
            (Pageable pageable, Role admin, Role role, String occupation, boolean enabled);

    Page<User> findAllRoleIsNotAndRoleIsAndOccupationNotInAndEnabledIs
            (Role role, Collection<String> occupations, boolean enabled, Pageable pageable);

    User findByIdAndRoleIsNot(String id, Role role);

    Page<User> findAllByDepartmentIsAndIdIsNot(Pageable pageable, Department department, String id);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndDepartmentIsAndIdIsNot
            (String value, Department department, String id, Pageable pageable);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndDepartmentIsAndIdIsNotAndEnabledIs
            (String value, Department department, String id, boolean enabled, Pageable pageable);


    Page<User> findAllByDepartmentIsAndIdIsNotAndEnabledIs
            (Pageable pageable, Department department, String id, boolean enabled);

    User findByIdAndDepartmentIs(String id, Department department);

    User findByDepartmentAndRole(Department department, Role role);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndDepartmentIsAndIdIsNot
            (String value, Department department, String id, Pageable pageable);

    User findByIdAndEnabledIs(String id, boolean enabled);

    Page<User> findAllByRoleIsAndDepartmentIsNull(Pageable pageable, Role role);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndRoleIsAndDepartmentIsNull
            (String value, Role role, Pageable pageable);

    Page<User> findAllByDepartmentIs(Pageable pageable, Department department);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndDepartmentIs
            (String value, Department department, Pageable pageable);

    List<User> findAllByIdIn(Collection<String> ids);

    User findByEmailAndEnabledIs(String email, Boolean enabled);

    User findByResetPasswordTokenAndResetPasswordExpireAfter(String resetPasswordToken, Date current);

    List<User> findAllByDepartmentIsAndRoleIsNot(Department department, Role role);
}

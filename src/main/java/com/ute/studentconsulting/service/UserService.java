package com.ute.studentconsulting.service;

import com.ute.studentconsulting.entity.Department;
import com.ute.studentconsulting.entity.Role;
import com.ute.studentconsulting.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;

public interface UserService {
    void save(User user);

    User findByPhone(String phone);

    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);

    User findById(String id);

    Page<User> findAllByRoleIsNot(Pageable pageable, Role admin);

    Page<User> findAllByRoleIsNotAndOccupationNotIn(Pageable pageable, Role admin, Collection<String> occupations);

    Page<User> findAllByRoleIsNotAndOccupationEqualsIgnoreCase(Pageable pageable, Role admin, String occupation);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNot
            (String value, Pageable pageable, Role admin);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationNotIn
            (String value, Collection<String> occupations, Pageable pageable);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationEqualsIgnoreCase
            (String value, Pageable pageable, Role admin, String occupation);

    Page<User> findAllByRoleIsNotAndRoleIs(Pageable pageable, Role admin, Role role);

    Page<User> findAllByRoleIsNotAndRoleIsAndOccupationNotIn(Pageable pageable, Role admin, Role role, Collection<String> occupations);

    Page<User> findAllByRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCase(Pageable pageable, Role admin, Role role, String occupation);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIs
            (String value, Pageable pageable, Role admin, Role role);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationNotIn
            (String value, Collection<String> occupations, Role role, Pageable pageable);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCase
            (String value, Pageable pageable, Role admin, Role role, String occupation);

    Page<User> findAllByRoleIsNotAndEnabledIsTrue(Pageable pageable, Role admin);

    Page<User> findAllByRoleIsNotAndEnabledIsFalse(Pageable pageable, Role admin);

    Page<User> findAllByRoleIsNotAndOccupationNotInAndEnabledIsTrue(Pageable pageable, Role admin, Collection<String> occupations);

    Page<User> findAllByRoleIsNotAndOccupationNotInAndEnabledIsFalse(Pageable pageable, Role admin, Collection<String> occupations);

    Page<User> findAllByRoleIsNotAndOccupationEqualsIgnoreCaseAndEnabledIsTrue(Pageable pageable, Role admin, String occupation);

    Page<User> findAllByRoleIsNotAndOccupationEqualsIgnoreCaseAndEnabledIsFalse(Pageable pageable, Role admin, String occupation);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndEnabledIsTrue
            (String value, Pageable pageable);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndEnabledIsFalse
            (String value, Pageable pageable);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationNotInAndEnableIsTrue
            (String value, Collection<String> occupations, Role role, Pageable pageable);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationNotInAndEnableIsFalse
            (String value, Collection<String> occupations, Role role, Pageable pageable);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCaseAndEnabledIsFalse
            (String value, Role role, String occupation, Pageable pageable);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCaseAndEnabledIsTrue
            (String value, Role role, String occupation, Pageable pageable);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationNotInAndEnabledIsFalse
            (String value, Collection<String> occupations, Pageable pageable);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationNotInAndEnabledIsTrue
            (String value, Collection<String> occupations, Pageable pageable);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationEqualsIgnoreCaseAndEnabledIsFalse
            (String value, String occupation, Pageable pageable);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationEqualsIgnoreCaseAndEnabledIsTrue
            (String value, String occupation, Pageable pageable);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndEnabledIsTrue
            (String value, Role role, Pageable pageable);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndEnabledIsFalse
            (String value, Role role, Pageable pageable);

    Page<User> findAllByRoleIsNotAndRoleIsAndEnabledIsFalse(Pageable pageable, Role admin, Role role);

    Page<User> findAllByRoleIsNotAndRoleIsAndEnabledIsTrue(Pageable pageable, Role admin, Role role);

    Page<User> findAllByRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCaseAndEnabledIsFalse(Pageable pageable, Role admin, Role role, String occupation);

    Page<User> findAllByRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCaseAndEnabledIsTrue(Pageable pageable, Role admin, Role role, String occupation);

    Page<User> findAllRoleIsNotAndRoleIsAndOccupationNotInAndEnabledIsFalse
            (Role role, Collection<String> occupations, Pageable pageable);

    Page<User> findAllRoleIsNotAndRoleIsAndOccupationNotInAndEnabledIsTrue
            (Role role, Collection<String> occupations, Pageable pageable);

    User findByIdAndRoleIsNot(String id, Role admin);

    Page<User> findAllByDepartmentIsAndIdIsNot(Pageable pageable, Department department, String id);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndDepartmentIsAndIdIsNot
            (String value, Department department, String id, Pageable pageable);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndDepartmentIsAndIdIsNotAndEnabledIsTrue
            (String value, Department department, String id, Pageable pageable);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndDepartmentIsAndIdIsNotAndEnabledIsFalse
            (String value, Department department, String id, Pageable pageable);

    Page<User> findAllByDepartmentIsAndIdIsNotAndEnabledIsTrue(Pageable pageable, Department department, String id);

    Page<User> findAllByDepartmentIsAndIdIsNotAndEnabledIsFalse(Pageable pageable, Department department, String id);

    User findByIdAndDepartmentIs(String id, Department department);

    User findByDepartmentAndRole(Department department, Role role);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndDepartmentIsAndIdIsNotAndEnabledIsTrue
            (String value, Department department, String id, Pageable pageable);

    User findByIdAndEnabledIsTrue(String id);
}

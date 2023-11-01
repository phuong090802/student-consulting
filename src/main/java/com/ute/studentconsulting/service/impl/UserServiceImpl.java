package com.ute.studentconsulting.service.impl;

import com.ute.studentconsulting.entity.Department;
import com.ute.studentconsulting.entity.Role;
import com.ute.studentconsulting.entity.User;
import com.ute.studentconsulting.exception.UserException;
import com.ute.studentconsulting.repository.UserRepository;
import com.ute.studentconsulting.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public User findByPhone(String phone) {
        return userRepository.findByPhone(phone)
                .orElseThrow(() -> new UserException("Không tìm thấy người dùng."));
    }

    @Override
    public boolean existsByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User findById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserException("Không tìm thấy người dùng."));
    }

    @Override
    public Page<User> findAllByRoleIsNot(Pageable pageable, Role admin) {
        return userRepository
                .findAllByRoleIsNot(pageable, admin);
    }

    @Override
    public Page<User> findAllByRoleIsNotAndOccupationNotIn(Pageable pageable, Role admin, Collection<String> occupations) {
        return userRepository
                .findAllByRoleIsNotAndOccupationNotIn(pageable, admin, occupations);
    }

    @Override
    public Page<User> findAllByRoleIsNotAndOccupationEqualsIgnoreCase(Pageable pageable, Role admin, String occupation) {
        return userRepository
                .findAllByRoleIsNotAndOccupationEqualsIgnoreCase(pageable, admin, occupation);
    }

    @Override
    public Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNot(String value, Pageable pageable, Role admin) {
        return userRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNot(value, value, value, pageable, admin);
    }

    @Override
    public Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationNotIn(String value, Collection<String> occupations, Role role, Pageable pageable) {
        return userRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationNotIn(value, occupations, role, pageable);
    }

    @Override
    public Page<User> findAllByRoleIsNotAndOccupationEqualsIgnoreCaseAndEnabledIsTrue
            (Pageable pageable, Role admin, String occupation) {
        return userRepository
                .findAllByRoleIsNotAndOccupationEqualsIgnoreCaseAndEnabledIsTrue
                        (pageable, admin, occupation);
    }

    @Override
    public Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndEnabledIsFalse(String value, Pageable pageable) {
        return userRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndEnabledIsFalse(value, pageable);
    }

    @Override
    public Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndEnabledIsTrue
            (String value, Pageable pageable) {
        return userRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndEnabledIsTrue
                        (value, pageable);
    }

    @Override
    public Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationNotInAndEnableIsFalse
            (String value, Collection<String> occupations, Role role, Pageable pageable) {
        return userRepository.
                findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationNotInAndEnableIsFalse
                        (value, occupations, role, pageable);
    }

    @Override
    public Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCaseAndEnabledIsTrue(String value, Role role, String occupation, Pageable pageable) {
        return userRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCaseAndEnabledIsTrue
                        (value, role, occupation, pageable);
    }

    @Override
    public Page<User> findAllByRoleIsNotAndRoleIsAndEnabledIsTrue(Pageable pageable, Role admin, Role role) {
        return userRepository
                .findAllByRoleIsNotAndRoleIsAndEnabledIsTrue(pageable, admin, role);
    }

    @Override
    public Page<User> findAllRoleIsNotAndRoleIsAndOccupationNotInAndEnabledIsTrue
            (Role role, Collection<String> occupations, Pageable pageable) {
        return userRepository
                .findAllRoleIsNotAndRoleIsAndOccupationNotInAndEnabledIsTrue
                        (role, occupations, pageable);
    }

    @Override
    public User findByIdAndRoleIsNot(String id, Role admin) {
        return userRepository
                .findByIdAndRoleIsNot(id, admin)
                .orElseThrow(() -> new UserException("Không tìm thấy người dùng."));
    }

    @Override
    public Page<User> findAllByDepartmentIsAndIdIsNot(Pageable pageable, Department department, String id) {
        return userRepository
                .findAllByDepartmentIsAndIdIsNot(pageable, department, id);
    }

    @Override
    public Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndDepartmentIsAndIdIsNot
            (String value, Department department, String id, Pageable pageable) {
        return userRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndDepartmentIsAndIdIsNot
                        (value, department, id, pageable);
    }

    @Override
    public Page<User> findAllByDepartmentIsAndIdIsNotAndEnabledIsTrue
            (Pageable pageable, Department department, String id) {
        return userRepository
                .findAllByDepartmentIsAndIdIsNotAndEnabledIsTrue
                        (pageable, department, id);
    }

    @Override
    public User findByIdAndDepartmentIs(String id, Department department) {
        return userRepository.findByIdAndDepartmentIs(id, department)
                .orElseThrow(() -> new UserException("Không tìm thấy người dùng."));
    }

    @Override
    public User findByDepartmentAndRole(Department department, Role role) {
        return userRepository.findByDepartmentAndRole(department, role)
                .orElseThrow(() -> new UserException("Không tìm thấy người dùng."));
    }

    @Override
    public Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndDepartmentIsAndIdIsNotAndEnabledIsTrue
            (String value, Department department, String id, Pageable pageable) {
        return userRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndDepartmentIsAndIdIsNotAndEnabledIsTrue
                        (value, department, id, pageable);
    }

    @Override
    public User findByIdAndEnabledIsTrue(String id) {
        return userRepository.findByIdAndEnabledIsTrue(id)
                .orElseThrow(() -> new UserException("Không tìm thấy người dùng."));
    }

    @Override
    public Page<User> findAllByRoleIsAndDepartmentIsNullAndEnabledIsTrue(Pageable pageable, Role role) {
        return userRepository
                .findAllByRoleIsAndDepartmentIsNullAndEnabledIsTrue(pageable, role);
    }

    @Override
    public Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndRoleIsAndDepartmentIsNullAndEnabledIsTrue
            (String value, Role role, Pageable pageable) {
        return userRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndRoleIsAndDepartmentIsNullAndEnabledIsTrue
                        (value, role, pageable);
    }

    @Override
    public Page<User> findAllByDepartmentIsAndIdIsNotAndEnabledIsFalse
            (Pageable pageable, Department department, String id) {
        return userRepository
                .findAllByDepartmentIsAndIdIsNotAndEnabledIsFalse
                        (pageable, department, id);
    }

    @Override
    public Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndDepartmentIsAndIdIsNotAndEnabledIsTrue
            (String value, Department department, String id, Pageable pageable) {
        return userRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndDepartmentIsAndIdIsNotAndEnabledIsTrue
                        (value, department, id, pageable);
    }

    @Override
    public Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndDepartmentIsAndIdIsNotAndEnabledIsFalse
            (String value, Department department, String id, Pageable pageable) {
        return userRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndDepartmentIsAndIdIsNotAndEnabledIsFalse
                        (value, department, id, pageable);
    }


    @Override
    public Page<User> findAllRoleIsNotAndRoleIsAndOccupationNotInAndEnabledIsFalse
            (Role role, Collection<String> occupations, Pageable pageable) {
        return userRepository
                .findAllRoleIsNotAndRoleIsAndOccupationNotInAndEnabledIsFalse
                        (role, occupations, pageable);
    }

    @Override
    public Page<User> findAllByRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCaseAndEnabledIsTrue
            (Pageable pageable, Role admin, Role role, String occupation) {
        return userRepository
                .findAllByRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCaseAndEnabledIsTrue
                        (pageable, admin, role, occupation);
    }

    @Override
    public Page<User> findAllByRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCaseAndEnabledIsFalse
            (Pageable pageable, Role admin, Role role, String occupation) {
        return userRepository
                .findAllByRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCaseAndEnabledIsFalse
                        (pageable, admin, role, occupation);
    }

    @Override
    public Page<User> findAllByRoleIsNotAndRoleIsAndEnabledIsFalse(Pageable pageable, Role admin, Role role) {
        return userRepository
                .findAllByRoleIsNotAndRoleIsAndEnabledIsFalse(pageable, admin, role);
    }

    @Override
    public Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationNotInAndEnabledIsFalse
            (String value, Collection<String> occupations, Pageable pageable) {
        return userRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationNotInAndEnabledIsFalse
                        (value, occupations, pageable);
    }

    @Override
    public Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndEnabledIsFalse
            (String value, Role role, Pageable pageable) {
        return userRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndEnabledIsFalse
                        (value, role, pageable);
    }

    @Override
    public Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationEqualsIgnoreCaseAndEnabledIsTrue
            (String value, String occupation, Pageable pageable) {
        return userRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationEqualsIgnoreCaseAndEnabledIsTrue
                        (value, occupation, pageable);
    }

    @Override
    public Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndEnabledIsTrue
            (String value, Role role, Pageable pageable) {
        return userRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndEnabledIsTrue
                        (value, role, pageable);
    }

    @Override
    public Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationEqualsIgnoreCaseAndEnabledIsFalse
            (String value, String occupation, Pageable pageable) {
        return userRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationEqualsIgnoreCaseAndEnabledIsFalse
                        (value, occupation, pageable);
    }

    @Override
    public Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationNotInAndEnabledIsTrue
            (String value, Collection<String> occupations, Pageable pageable) {
        return userRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationNotInAndEnabledIsTrue
                        (value, occupations, pageable);
    }

    @Override
    public Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCaseAndEnabledIsFalse
            (String value, Role role, String occupation, Pageable pageable) {
        return userRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCaseAndEnabledIsFalse
                        (value, role, occupation, pageable);
    }

    @Override
    public Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationNotInAndEnableIsTrue
            (String value, Collection<String> occupations, Role role, Pageable pageable) {
        return userRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationNotInAndEnableIsTrue
                        (value, occupations, role, pageable);
    }

    @Override
    public Page<User> findAllByRoleIsNotAndOccupationEqualsIgnoreCaseAndEnabledIsFalse
            (Pageable pageable, Role admin, String occupation) {
        return userRepository
                .findAllByRoleIsNotAndOccupationEqualsIgnoreCaseAndEnabledIsFalse
                        (pageable, admin, occupation);
    }

    @Override
    public Page<User> findAllByRoleIsNotAndOccupationNotInAndEnabledIsFalse
            (Pageable pageable, Role admin, Collection<String> occupations) {
        return userRepository
                .findAllByRoleIsNotAndOccupationNotInAndEnabledIsFalse
                        (pageable, admin, occupations);
    }

    @Override
    public Page<User> findAllByRoleIsNotAndEnabledIsTrue(Pageable pageable, Role admin) {
        return userRepository
                .findAllByRoleIsNotAndEnabledIsTrue(pageable, admin);
    }

    @Override
    public Page<User> findAllByRoleIsNotAndOccupationNotInAndEnabledIsTrue
            (Pageable pageable, Role admin, Collection<String> occupations) {
        return userRepository
                .findAllByRoleIsNotAndOccupationNotInAndEnabledIsTrue
                        (pageable, admin, occupations);
    }

    @Override
    public Page<User> findAllByRoleIsNotAndEnabledIsFalse(Pageable pageable, Role admin) {
        return userRepository
                .findAllByRoleIsNotAndEnabledIsFalse(pageable, admin);
    }

    @Override
    public Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCase
            (String value, Pageable pageable, Role admin, Role role, String occupation) {
        return userRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCase
                        (value, value, value, pageable, admin, role, occupation);
    }

    @Override
    public Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIs
            (String value, Pageable pageable, Role admin, Role role) {
        return userRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIs
                        (value, value, value, pageable, admin, role);
    }

    @Override
    public Page<User> findAllByRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCase
            (Pageable pageable, Role admin, Role role, String occupation) {
        return userRepository
                .findAllByRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCase
                        (pageable, admin, role, occupation);
    }

    @Override
    public Page<User> findAllByRoleIsNotAndRoleIsAndOccupationNotIn
            (Pageable pageable, Role admin, Role role, Collection<String> occupations) {
        return userRepository
                .findAllByRoleIsNotAndRoleIsAndOccupationNotIn
                        (pageable, admin, role, occupations);
    }

    @Override
    public Page<User> findAllByRoleIsNotAndRoleIs(Pageable pageable, Role admin, Role role) {
        return userRepository
                .findAllByRoleIsNotAndRoleIs(pageable, admin, role);
    }

    @Override
    public Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationEqualsIgnoreCase
            (String value, Pageable pageable, Role admin, String occupation) {
        return userRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationEqualsIgnoreCase
                        (value, value, value, pageable, admin, occupation);
    }

    @Override
    public Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationNotIn
            (String value, Collection<String> occupations, Pageable pageable) {
        return userRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationNotIn
                        (value, occupations, pageable);
    }
}

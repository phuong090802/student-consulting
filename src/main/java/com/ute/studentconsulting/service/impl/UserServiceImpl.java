package com.ute.studentconsulting.service.impl;

import com.ute.studentconsulting.entity.Department;
import com.ute.studentconsulting.entity.Role;
import com.ute.studentconsulting.entity.User;
import com.ute.studentconsulting.exception.NotFoundException;
import com.ute.studentconsulting.repository.UserRepository;
import com.ute.studentconsulting.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User findByResetPasswordTokenAndResetPasswordExpireAfter(String resetPasswordToken, Date current) {
        return userRepository.findByResetPasswordTokenAndResetPasswordExpireAfter(resetPasswordToken, current)
                .orElseThrow(() -> new NotFoundException
                        ("Không tìm thấy người dùng để đặt lại mật khẩu",
                                "Reset password token không hợp lệ", 10056));
    }

    @Override
    @Transactional
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public User findByPhone(String phone) {
        return userRepository.findByPhone(phone)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng",
                        "Không tìm thấy người dùng với số điện thoại: %s".formatted(phone),
                        10006));
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
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng",
                        "Không tìm thấy người dùng với id: %s".formatted(id), 10007));
    }

    @Override
    public Page<User> findAllByRoleIsNot(Pageable pageable, Role admin) {
        return userRepository
                .findAllByRoleIsNot(pageable, admin);
    }

    @Override
    public Page<User> findAllByRoleIsNotAndOccupationNotIn
            (Pageable pageable, Role admin, Collection<String> occupations) {
        return userRepository
                .findAllByRoleIsNotAndOccupationNotIn(pageable, admin, occupations);
    }

    @Override
    public Page<User> findAllByRoleIsNotAndOccupationEqualsIgnoreCase
            (Pageable pageable, Role admin, String occupation) {
        return userRepository
                .findAllByRoleIsNotAndOccupationEqualsIgnoreCase(pageable, admin, occupation);
    }

    @Override
    public Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNot
            (String value, Pageable pageable, Role admin) {
        return userRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNot
                        (value, value, value, pageable, admin);
    }

    @Override
    public Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationNotIn
            (String value, Role admin, Collection<String> occupations, Role role, Pageable pageable) {
        return userRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationNotIn
                        (value, admin, occupations, role, pageable);
    }

    @Override
    public Page<User> findAllByRoleIsNotAndOccupationEqualsIgnoreCaseAndEnabledIs
            (Pageable pageable, Role admin, String occupation, boolean enabled) {
        return userRepository
                .findAllByRoleIsNotAndOccupationEqualsIgnoreCaseAndEnabledIs
                        (pageable, admin, occupation, enabled);
    }

    @Override
    public Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndEnabledIs
            (String value, Role admin, boolean enabled, Pageable pageable) {
        return userRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndEnabledIs
                        (value, admin, enabled, pageable);
    }

    @Override
    public Page<User> findAllByRoleIsNotAndRoleIsAndEnabledIs
            (Pageable pageable, Role admin, Role role, boolean enabled) {
        return userRepository
                .findAllByRoleIsNotAndRoleIsAndEnabledIs(pageable, admin, role, enabled);
    }

    @Override
    public User findByIdAndRoleIsNot(String id, Role role) {
        return userRepository
                .findByIdAndRoleIsNot(id, role)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng",
                        "Không tìm thấy người dùng với id: %s với role không phải là role: %s".formatted(id, role.getName().name()),
                        10008));
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
    public Page<User> findAllByDepartmentIsAndIdIsNotAndEnabledIs
            (Pageable pageable, Department department, String id, boolean enabled) {
        return userRepository
                .findAllByDepartmentIsAndIdIsNotAndEnabledIs
                        (pageable, department, id, enabled);
    }

    @Override
    public User findByIdAndDepartmentIs(String id, Department department) {
        return userRepository.findByIdAndDepartmentIs(id, department)
                .orElseThrow(() ->
                        new NotFoundException("Không tìm thấy người dùng",
                                "Không tìm thấy người dùng với id: %s ở phòng ban: %s".formatted(id, department.getName()), 10009));
    }

    @Override
    public User findByDepartmentAndRole(Department department, Role role) {
        return userRepository.findByDepartmentAndRole(department, role)
                .orElse(null);
    }

    @Override
    public Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndDepartmentIsAndIdIsNot
            (String value, Department department, String id, Pageable pageable) {
        return userRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndDepartmentIsAndIdIsNot
                        (value, department, id, pageable);
    }

    @Override
    public User findByEmailAndEnabledIs(String email, Boolean enabled) {
        return userRepository.findByEmailAndEnabledIs(email, enabled)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng",
                        "Không tìm thấy người dùng với trạng thái hoạt động và email: %s".formatted(email),
                        10055));
    }

    @Override
    public User findByIdAndEnabledIs(String id, boolean enabled) {
        return userRepository.findByIdAndEnabledIs(id, enabled)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng",
                        "Không tìm thấy người dùng với trạng thái hoạt động và id: %s".formatted(id),
                        10010));
    }

    @Override
    public Page<User> findAllByRoleIsAndDepartmentIsNull(Pageable pageable, Role role) {
        return userRepository
                .findAllByRoleIsAndDepartmentIsNull(pageable, role);
    }

    @Override
    public Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndRoleIsAndDepartmentIsNull
            (String value, Role role, Pageable pageable) {
        return userRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndRoleIsAndDepartmentIsNull
                        (value, role, pageable);
    }

    @Override
    public Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndDepartmentIsAndIdIsNotAndEnabledIs
            (String value, Department department, String id, boolean enabled, Pageable pageable) {
        return userRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndDepartmentIsAndIdIsNotAndEnabledIs
                        (value, department, id, enabled, pageable);
    }

    @Override
    public Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndDepartmentIs
            (String value, Department department, Pageable pageable) {
        return userRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndDepartmentIs
                        (value, department, pageable);
    }

    @Override
    public List<User> findAllByIdIn(Collection<String> ids) {
        return userRepository.findAllByIdIn(ids);
    }

    @Override
    public Page<User> findAllByDepartmentIs(Pageable pageable, Department department) {
        return userRepository.findAllByDepartmentIs(pageable, department);
    }

    @Override
    public Page<User> findAllRoleIsNotAndRoleIsAndOccupationNotInAndEnabledIs
            (Role role, Collection<String> occupations, boolean enabled, Pageable pageable) {
        return userRepository
                .findAllRoleIsNotAndRoleIsAndOccupationNotInAndEnabledIs
                        (role, occupations, enabled, pageable);
    }

    @Override
    public Page<User> findAllByRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCaseAndEnabledIs
            (Pageable pageable, Role admin, Role role, String occupation, boolean enabled) {
        return userRepository
                .findAllByRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCaseAndEnabledIs
                        (pageable, admin, role, occupation, enabled);
    }

    @Override
    public Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndEnabledIs
            (String value, Role role, boolean enabled, Pageable pageable) {
        return userRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndEnabledIs
                        (value, role, enabled, pageable);
    }

    @Override
    public Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationEqualsIgnoreCaseAndEnabledIs
            (String value, String occupation, boolean enabled, Pageable pageable) {
        return userRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationEqualsIgnoreCaseAndEnabledIs
                        (value, occupation, enabled, pageable);
    }

    @Override
    public Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationNotInAndEnabledIs
            (String value, Collection<String> occupations, boolean enabled, Pageable pageable) {
        return userRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationNotInAndEnabledIs
                        (value, occupations, enabled, pageable);
    }

    @Override
    public Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCaseAndEnabledIs
            (String value, Role role, String occupation, boolean enabled, Pageable pageable) {
        return userRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCaseAndEnabledIs
                        (value, role, occupation, enabled, pageable);
    }

    @Override
    public Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationNotInAndEnableIs
            (String value, Role admin, Collection<String> occupations, Role role, boolean enabled, Pageable pageable) {
        return userRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationNotInAndEnableIs
                        (value, admin, occupations, role, enabled, pageable);
    }

    @Override
    public Page<User> findAllByRoleIsNotAndOccupationNotInAndEnabledIs
            (Pageable pageable, Role admin, Collection<String> occupations, boolean enabled) {
        return userRepository
                .findAllByRoleIsNotAndOccupationNotInAndEnabledIs
                        (pageable, admin, occupations, enabled);
    }

    @Override
    public Page<User> findAllByRoleIsNotAndEnabledIs(Pageable pageable, Role admin, boolean enabled) {
        return userRepository
                .findAllByRoleIsNotAndEnabledIs(pageable, admin, enabled);
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
            (String value, Role admin, Collection<String> occupations, Pageable pageable) {
        return userRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationNotIn
                        (value, admin, occupations, pageable);
    }
}

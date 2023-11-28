package com.ute.studentconsulting.repository;

import com.ute.studentconsulting.entity.Department;
import com.ute.studentconsulting.entity.Role;
import com.ute.studentconsulting.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByPhone(String phone);

    Boolean existsByPhone(String phone);

    Boolean existsByEmail(String email);

    Page<User> findAllByRoleIsNot(Pageable pageable, Role admin);

    Page<User> findAllByRoleIsNotAndOccupationNotIn(Pageable pageable, Role admin, Collection<String> occupations);

    Page<User> findAllByRoleIsNotAndOccupationEqualsIgnoreCase(Pageable pageable, Role admin, String occupation);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNot
            (String value1, String value2, String value3, Pageable pageable, Role admin);

    @Query("SELECT u FROM User u WHERE (LOWER(u.name) LIKE %:value% OR LOWER(u.email) LIKE %:value% OR u.phone LIKE %:value%) " +
            "AND u.role <> :admin " +
            "AND u.occupation NOT IN :occupations ")
    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationNotIn
            (@Param("value") String value, @Param("admin") Role admin, @Param("occupations") Collection<String> occupations, Pageable pageable);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationEqualsIgnoreCase
            (String value1, String value2, String value3, Pageable pageable, Role admin, String occupation);

    Page<User> findAllByRoleIsNotAndRoleIs(Pageable pageable, Role admin, Role role);

    Page<User> findAllByRoleIsNotAndRoleIsAndOccupationNotIn(Pageable pageable, Role admin, Role role, Collection<String> occupations);

    Page<User> findAllByRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCase(Pageable pageable, Role admin, Role role, String occupation);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIs
            (String value1, String value2, String value3, Pageable pageable, Role admin, Role role);

    @Query("SELECT u FROM User u WHERE (LOWER(u.name) LIKE %:value% OR LOWER(u.email) LIKE %:value% OR u.phone LIKE %:value%) " +
            "AND u.role <> :admin " +
            "AND u.occupation NOT IN :occupations AND u.role = :role ")
    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationNotIn
            (@Param("value") String value, @Param("admin") Role admin, @Param("occupations") Collection<String> occupations, @Param("role") Role role, Pageable pageable);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCase
            (String value1, String value2, String value3, Pageable pageable, Role admin, Role role, String occupation);

    Page<User> findAllByRoleIsNotAndEnabledIs(Pageable pageable, Role admin, Boolean enabled);

    Page<User> findAllByRoleIsNotAndOccupationNotInAndEnabledIs(Pageable pageable, Role admin, Collection<String> occupations, Boolean enabled);


    Page<User> findAllByRoleIsNotAndOccupationEqualsIgnoreCaseAndEnabledIs(Pageable pageable, Role admin, String occupation, Boolean enabled);

    @Query("SELECT u FROM User u " +
            "WHERE (LOWER(u.name) LIKE %:value% OR LOWER(u.email) LIKE %:value% OR u.phone LIKE %:value%) " +
            "AND u.role.id <> :admin " +
            "AND u.enabled = :enabled ")
    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndEnabledIs
            (@Param("value") String value, @Param("admin") Role admin, @Param("enabled") Boolean enabled, Pageable pageable);

    @Query("SELECT u FROM User u WHERE (LOWER(u.name) LIKE %:value% OR LOWER(u.email) LIKE %:value% OR u.phone LIKE %:value%) " +
            "AND u.role <> :admin " +
            "AND u.occupation NOT IN :occupations AND u.role = :role AND u.enabled = :enabled ")
    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationNotInAndEnableIs
            (@Param("value") String value, @Param("admin") Role admin, @Param("occupations") Collection<String> occupations, @Param("role") Role role, @Param("enabled") Boolean enabled, Pageable pageable);

    @Query("SELECT u FROM User u " +
            "WHERE (LOWER(u.name) LIKE %:value% OR LOWER(u.email) LIKE %:value% OR u.phone LIKE %:value%) " +
            "AND u.role.id <> (SELECT r.id FROM Role r WHERE r.name = 'ROLE_ADMIN') " +
            "AND u.role = :role AND LOWER(u.occupation) = LOWER(:occupation) AND u.enabled = :enabled ")
    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCaseAndEnabledIs
            (@Param("value") String value, @Param("role") Role role, @Param("occupation") String occupation, @Param("enabled") Boolean enabled, Pageable pageable);

    @Query("SELECT u FROM User u " +
            "WHERE (LOWER(u.name) LIKE %:value% OR LOWER(u.email) LIKE %:value% OR u.phone LIKE %:value%) " +
            "AND u.role.id <> (SELECT r.id FROM Role r WHERE r.name = 'ROLE_ADMIN') " +
            "AND u.occupation NOT IN :occupations AND u.enabled = :enabled ")
    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationNotInAndEnabledIs
            (@Param("value") String value, @Param("occupations") Collection<String> occupations, @Param("enabled") Boolean enabled, Pageable pageable);

    @Query("SELECT u FROM User u " +
            "WHERE (LOWER(u.name) LIKE %:value% OR LOWER(u.email) LIKE %:value% OR u.phone LIKE %:value%) " +
            "AND u.role.id <> (SELECT r.id FROM Role r WHERE r.name = 'ROLE_ADMIN') " +
            "AND LOWER(u.occupation) = LOWER(:occupation) AND u.enabled = :enabled ")
    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationEqualsIgnoreCaseAndEnabledIs
            (@Param("value") String value, @Param("occupation") String occupation, @Param("enabled") Boolean enabled, Pageable pageable);

    @Query("SELECT u FROM User u " +
            "WHERE (LOWER(u.name) LIKE %:value% OR LOWER(u.email) LIKE %:value% OR u.phone LIKE %:value%) " +
            "AND u.role.id <> (SELECT r.id FROM Role r WHERE r.name = 'ROLE_ADMIN') " +
            "AND u.role = :role AND u.enabled = :enabled ")
    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndEnabledIs
            (@Param("value") String value, @Param("role") Role role, @Param("enabled") Boolean enabled, Pageable pageable);

    Page<User> findAllByRoleIsNotAndRoleIsAndEnabledIs(Pageable pageable, Role admin, Role role, Boolean enabled);

    Page<User> findAllByRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCaseAndEnabledIs(Pageable pageable, Role admin, Role role, String occupation, Boolean enabled);

    @Query("SELECT u FROM User u " +
            "WHERE u.role.id <> (SELECT r.id FROM Role r WHERE r.name = 'ROLE_ADMIN') " +
            "AND u.role = :role AND u.occupation NOT IN :occupations AND u.enabled = :enabled ")
    Page<User> findAllRoleIsNotAndRoleIsAndOccupationNotInAndEnabledIs
            (@Param("role") Role role, @Param("occupations") Collection<String> occupations, @Param("enabled") Boolean enabled, Pageable pageable);

    Optional<User> findByIdAndRoleIsNot(String id, Role admin);

    Page<User> findAllByDepartmentIsAndIdIsNot(Pageable pageable, Department department, String id);

    @Query("SELECT u FROM User u " +
            "WHERE (LOWER(u.name) LIKE %:value% OR LOWER(u.email) LIKE %:value% OR u.phone LIKE %:value%) " +
            "AND u.department = :department AND u.id <> :id")
    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndDepartmentIsAndIdIsNot
            (@Param("value") String value, @Param("department") Department department, @Param("id") String id, Pageable pageable);

    @Query("SELECT u FROM User u " +
            "WHERE (LOWER(u.name) LIKE %:value% OR LOWER(u.email) LIKE %:value% OR u.phone LIKE %:value%) " +
            "AND u.department = :department AND u.id <> :id AND u.enabled = :enabled  ")
    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndDepartmentIsAndIdIsNotAndEnabledIs
            (@Param("value") String value, @Param("department") Department department, @Param("id") String id, @Param("enabled") Boolean enabled, Pageable pageable);

    Page<User> findAllByDepartmentIsAndIdIsNotAndEnabledIs(Pageable pageable, Department department, String id, Boolean enabled);

    Optional<User> findByIdAndDepartmentIs(String id, Department department);

    Optional<User> findByDepartmentAndRole(Department department, Role role);

    @Query("SELECT u FROM User u " +
            "WHERE (LOWER(u.name) LIKE %:value% OR LOWER(u.email) LIKE %:value%) " +
            "AND u.department = :department AND u.id <> :id AND u.enabled = :enabled  ")
    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndDepartmentIsAndIdIsNotAndEnabledIs
            (@Param("value") String value, @Param("department") Department department, @Param("id") String id, @Param("enabled") Boolean enabled, Pageable pageable);

    Optional<User> findByIdAndEnabledIs(String id, Boolean enabled);

    Page<User> findAllByRoleIsAndDepartmentIsNullAndEnabledIs(Pageable pageable, Role role, Boolean enabled);

    @Query("SELECT u FROM User u " +
            "WHERE (LOWER(u.name) LIKE %:value% OR LOWER(u.email) LIKE %:value%) " +
            "AND u.role = :role AND u.enabled = :enabled  ")
    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndRoleIsAndDepartmentIsNullAndEnabledIs
            (@Param("value") String value, @Param("role") Role role, @Param("enabled") Boolean enabled, Pageable pageable);

    Page<User> findAllByDepartmentIsAndEnabledIs(Pageable pageable, Boolean enabled, Department department);

    @Query("SELECT u FROM User u " +
            "WHERE (LOWER(u.name) LIKE %:value% OR LOWER(u.email) LIKE %:value%) " +
            "AND u.department = :department AND u.enabled = :enabled  ")
    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndDepartmentIsAndEnabledIs
            (@Param("value") String value, @Param("department") Department department,
             @Param("enabled") Boolean enabled, Pageable pageable);
}

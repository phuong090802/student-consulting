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
import java.util.List;
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
            "AND u.role <> (SELECT r FROM Role r WHERE r.name = 'ROLE_ADMIN') " +
            "AND u.occupation NOT IN :occupations ")
    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationNotIn
            (@Param("value") String value, @Param("occupations") Collection<String> occupations, Pageable pageable);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationEqualsIgnoreCase
            (String value1, String value2, String value3, Pageable pageable, Role admin, String occupation);

    Page<User> findAllByRoleIsNotAndRoleIs(Pageable pageable, Role admin, Role role);

    Page<User> findAllByRoleIsNotAndRoleIsAndOccupationNotIn(Pageable pageable, Role admin, Role role, Collection<String> occupations);

    Page<User> findAllByRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCase(Pageable pageable, Role admin, Role role, String occupation);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIs
            (String value1, String value2, String value3, Pageable pageable, Role admin, Role role);

    @Query("SELECT u FROM User u WHERE (LOWER(u.name) LIKE %:value% OR LOWER(u.email) LIKE %:value% OR u.phone LIKE %:value%) " +
            "AND u.role <> (SELECT r FROM Role r WHERE r.name = 'ROLE_ADMIN') " +
            "AND u.occupation NOT IN :occupations AND u.role = :role ")
    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationNotIn
            (@Param("value") String value, @Param("occupations") Collection<String> occupations, @Param("role") Role role, Pageable pageable);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCase
            (String value1, String value2, String value3, Pageable pageable, Role admin, Role role, String occupation);

    Page<User> findAllByRoleIsNotAndEnabledIsTrue(Pageable pageable, Role admin);

    Page<User> findAllByRoleIsNotAndEnabledIsFalse(Pageable pageable, Role admin);

    Page<User> findAllByRoleIsNotAndOccupationNotInAndEnabledIsTrue(Pageable pageable, Role admin, Collection<String> occupations);

    Page<User> findAllByRoleIsNotAndOccupationNotInAndEnabledIsFalse(Pageable pageable, Role admin, Collection<String> occupations);

    Page<User> findAllByRoleIsNotAndOccupationEqualsIgnoreCaseAndEnabledIsTrue(Pageable pageable, Role admin, String occupation);

    Page<User> findAllByRoleIsNotAndOccupationEqualsIgnoreCaseAndEnabledIsFalse(Pageable pageable, Role admin, String occupation);

    @Query("SELECT u FROM User u " +
            "WHERE (LOWER(u.name) LIKE %:value% OR LOWER(u.email) LIKE %:value% OR u.phone LIKE %:value%) " +
            "AND u.role.id <> (SELECT r.id FROM Role r WHERE r.name = 'ROLE_ADMIN') " +
            "AND u.enabled = true ")
    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndEnabledIsTrue
            (@Param("value") String value, Pageable pageable);

    @Query("SELECT u FROM User u " +
            "WHERE (LOWER(u.name) LIKE %:value% OR LOWER(u.email) LIKE %:value% OR u.phone LIKE %:value%) " +
            "AND u.role.id <> (SELECT r.id FROM Role r WHERE r.name = 'ROLE_ADMIN') " +
            "AND u.enabled = false ")
    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndEnabledIsFalse
            (@Param("value") String value, Pageable pageable);

    @Query("SELECT u FROM User u WHERE (LOWER(u.name) LIKE %:value% OR LOWER(u.email) LIKE %:value% OR u.phone LIKE %:value%) " +
            "AND u.role <> (SELECT r FROM Role r WHERE r.name = 'ROLE_ADMIN') " +
            "AND u.occupation NOT IN :occupations AND u.role = :role AND u.enabled = true ")
    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationNotInAndEnableIsTrue
            (@Param("value") String value, @Param("occupations") Collection<String> occupations, @Param("role") Role role, Pageable pageable);

    @Query("SELECT u FROM User u WHERE (LOWER(u.name) LIKE %:value% OR LOWER(u.email) LIKE %:value% OR u.phone LIKE %:value%) " +
            "AND u.role <> (SELECT r FROM Role r WHERE r.name = 'ROLE_ADMIN') " +
            "AND u.occupation NOT IN :occupations AND u.role = :role AND u.enabled = false ")
    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationNotInAndEnableIsFalse
            (@Param("value") String value, @Param("occupations") Collection<String> occupations, @Param("role") Role role, Pageable pageable);

    @Query("SELECT u FROM User u " +
            "WHERE (LOWER(u.name) LIKE %:value% OR LOWER(u.email) LIKE %:value% OR u.phone LIKE %:value%) " +
            "AND u.role.id <> (SELECT r.id FROM Role r WHERE r.name = 'ROLE_ADMIN') " +
            "AND u.role = :role AND LOWER(u.occupation) = LOWER(:occupation) AND u.enabled = false ")
    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCaseAndEnabledIsFalse
            (@Param("value") String value, @Param("role") Role role, @Param("occupation") String occupation, Pageable pageable);

    @Query("SELECT u FROM User u " +
            "WHERE (LOWER(u.name) LIKE %:value% OR LOWER(u.email) LIKE %:value% OR u.phone LIKE %:value%) " +
            "AND u.role.id <> (SELECT r.id FROM Role r WHERE r.name = 'ROLE_ADMIN') " +
            "AND u.role = :role AND LOWER(u.occupation) = LOWER(:occupation) AND u.enabled = true ")
    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCaseAndEnabledIsTrue
            (@Param("value") String value, @Param("role") Role role, @Param("occupation") String occupation, Pageable pageable);

    @Query("SELECT u FROM User u " +
            "WHERE (LOWER(u.name) LIKE %:value% OR LOWER(u.email) LIKE %:value% OR u.phone LIKE %:value%) " +
            "AND u.role.id <> (SELECT r.id FROM Role r WHERE r.name = 'ROLE_ADMIN') " +
            "AND u.occupation NOT IN :occupations AND u.enabled = false ")
    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationNotInAndEnabledIsFalse
            (@Param("value") String value, @Param("occupations") Collection<String> occupations, Pageable pageable);

    @Query("SELECT u FROM User u " +
            "WHERE (LOWER(u.name) LIKE %:value% OR LOWER(u.email) LIKE %:value% OR u.phone LIKE %:value%) " +
            "AND u.role.id <> (SELECT r.id FROM Role r WHERE r.name = 'ROLE_ADMIN') " +
            "AND u.occupation NOT IN :occupations AND u.enabled = true ")
    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationNotInAndEnabledIsTrue
            (@Param("value") String value, @Param("occupations") Collection<String> occupations, Pageable pageable);

    @Query("SELECT u FROM User u " +
            "WHERE (LOWER(u.name) LIKE %:value% OR LOWER(u.email) LIKE %:value% OR u.phone LIKE %:value%) " +
            "AND u.role.id <> (SELECT r.id FROM Role r WHERE r.name = 'ROLE_ADMIN') " +
            "AND LOWER(u.occupation) = LOWER(:occupation) AND u.enabled = false ")
    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationEqualsIgnoreCaseAndEnabledIsFalse
            (@Param("value") String value, @Param("occupation") String occupation, Pageable pageable);

    @Query("SELECT u FROM User u " +
            "WHERE (LOWER(u.name) LIKE %:value% OR LOWER(u.email) LIKE %:value% OR u.phone LIKE %:value%) " +
            "AND u.role.id <> (SELECT r.id FROM Role r WHERE r.name = 'ROLE_ADMIN') " +
            "AND LOWER(u.occupation) = LOWER(:occupation) AND u.enabled = true ")
    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndOccupationEqualsIgnoreCaseAndEnabledIsTrue
            (@Param("value") String value, @Param("occupation") String occupation, Pageable pageable);

    @Query("SELECT u FROM User u " +
            "WHERE (LOWER(u.name) LIKE %:value% OR LOWER(u.email) LIKE %:value% OR u.phone LIKE %:value%) " +
            "AND u.role.id <> (SELECT r.id FROM Role r WHERE r.name = 'ROLE_ADMIN') " +
            "AND u.role = :role AND u.enabled = true ")
    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndEnabledIsTrue
            (@Param("value") String value, @Param("role") Role role, Pageable pageable);

    @Query("SELECT u FROM User u " +
            "WHERE (LOWER(u.name) LIKE %:value% OR LOWER(u.email) LIKE %:value% OR u.phone LIKE %:value%) " +
            "AND u.role.id <> (SELECT r.id FROM Role r WHERE r.name = 'ROLE_ADMIN') " +
            "AND u.role = :role AND u.enabled = false ")
    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndRoleIsNotAndRoleIsAndEnabledIsFalse
            (@Param("value") String value, @Param("role") Role role, Pageable pageable);

    Page<User> findAllByRoleIsNotAndRoleIsAndEnabledIsFalse(Pageable pageable, Role admin, Role role);

    Page<User> findAllByRoleIsNotAndRoleIsAndEnabledIsTrue(Pageable pageable, Role admin, Role role);

    Page<User> findAllByRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCaseAndEnabledIsFalse(Pageable pageable, Role admin, Role role, String occupation);

    Page<User> findAllByRoleIsNotAndRoleIsAndOccupationEqualsIgnoreCaseAndEnabledIsTrue(Pageable pageable, Role admin, Role role, String occupation);

    @Query("SELECT u FROM User u " +
            "WHERE u.role.id <> (SELECT r.id FROM Role r WHERE r.name = 'ROLE_ADMIN') " +
            "AND u.role = :role AND u.occupation NOT IN :occupations AND u.enabled = false ")
    Page<User> findAllRoleIsNotAndRoleIsAndOccupationNotInAndEnabledIsFalse
            (@Param("role") Role role, @Param("occupations") Collection<String> occupations, Pageable pageable);

    @Query("SELECT u FROM User u " +
            "WHERE u.role.id <> (SELECT r.id FROM Role r WHERE r.name = 'ROLE_ADMIN') " +
            "AND u.role = :role AND u.occupation NOT IN :occupations AND u.enabled = true ")
    Page<User> findAllRoleIsNotAndRoleIsAndOccupationNotInAndEnabledIsTrue
            (@Param("role") Role role, @Param("occupations") Collection<String> occupations, Pageable pageable);

    Optional<User> findByIdAndRoleIsNot(String id, Role admin);

    List<User> findAllByDepartmentIsNullAndRoleIsNotAndRoleIsAndEnabledIsTrue(Role admin, Role departmentHead);

    @Query("SELECT DISTINCT u.department.id FROM User u WHERE u.department IS NOT NULL")
    List<String> findDistinctDepartmentIds();

    Page<User> findAllByDepartmentIsAndIdIsNot(Pageable pageable, Department department, String id);

    @Query("SELECT u FROM User u " +
            "WHERE (LOWER(u.name) LIKE %:value% OR LOWER(u.email) LIKE %:value% OR u.phone LIKE %:value%) " +
            "AND u.department = :department AND u.id <> :id")
    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndDepartmentIsAndIdIsNot
            (@Param("value") String value, @Param("department") Department department, @Param("id") String id, Pageable pageable);

    @Query("SELECT u FROM User u " +
            "WHERE (LOWER(u.name) LIKE %:value% OR LOWER(u.email) LIKE %:value% OR u.phone LIKE %:value%) " +
            "AND u.department = :department AND u.id <> :id AND u.enabled = true  ")
    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndDepartmentIsAndIdIsNotAndEnabledIsTrue
            (@Param("value") String value, @Param("department") Department department, @Param("id") String id, Pageable pageable);

    @Query("SELECT u FROM User u " +
            "WHERE (LOWER(u.name) LIKE %:value% OR LOWER(u.email) LIKE %:value% OR u.phone LIKE %:value%) " +
            "AND u.department = :department AND u.id <> :id AND u.enabled = false  ")
    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingAndDepartmentIsAndIdIsNotAndEnabledIsFalse
            (@Param("value") String value, @Param("department") Department department, @Param("id") String id, Pageable pageable);

    Page<User> findAllByDepartmentIsAndIdIsNotAndEnabledIsTrue(Pageable pageable, Department department, String id);
    Page<User> findAllByDepartmentIsAndIdIsNotAndEnabledIsFalse(Pageable pageable, Department department, String id);
    Optional<User> findByIdAndDepartmentIs(String id, Department department);
}

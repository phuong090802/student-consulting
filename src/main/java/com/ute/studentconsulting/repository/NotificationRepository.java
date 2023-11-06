package com.ute.studentconsulting.repository;

import com.ute.studentconsulting.entity.Department;
import com.ute.studentconsulting.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, String> {
    List<Notification> findAllByStatusIsTrueAndDepartmentIsOrderByDateDesc(Department department);
}

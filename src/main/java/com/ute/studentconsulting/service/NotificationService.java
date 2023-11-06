package com.ute.studentconsulting.service;

import com.ute.studentconsulting.entity.Department;
import com.ute.studentconsulting.entity.Notification;

import java.util.List;

public interface NotificationService {
    void save(Notification notification);
    List<Notification> findAllByStatusIsTrueAndDepartmentIsOrderByDateDesc(Department department);

}

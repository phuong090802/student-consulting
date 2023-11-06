package com.ute.studentconsulting.service.impl;

import com.ute.studentconsulting.entity.Department;
import com.ute.studentconsulting.entity.Notification;
import com.ute.studentconsulting.repository.NotificationRepository;
import com.ute.studentconsulting.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;

    @Override
    public void save(Notification notification) {
        notificationRepository.save(notification);
    }

    @Override
    public List<Notification> findAllByStatusIsTrueAndDepartmentIsOrderByDateDesc(Department department) {
        return notificationRepository.findAllByStatusIsTrueAndDepartmentIsOrderByDateDesc(department);
    }
}

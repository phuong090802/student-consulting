package com.ute.studentconsulting.mail.service;

import com.ute.studentconsulting.model.SimpleMailModel;

public interface MailService {
    void sendSimpleMail(SimpleMailModel mail);
}

package com.ute.studentconsulting.firebase.service;

import org.springframework.web.multipart.MultipartFile;

public interface FireBaseService {
    String uploadFile(MultipartFile file, String folderName);

    String downloadFile(String id);

    void deleteFile(String id);
}

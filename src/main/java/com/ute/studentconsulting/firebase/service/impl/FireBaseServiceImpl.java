package com.ute.studentconsulting.firebase.service.impl;

import com.google.cloud.storage.BlobId;
import com.google.firebase.cloud.StorageClient;
import com.ute.studentconsulting.exception.InternalServerErrorException;
import com.ute.studentconsulting.firebase.service.FireBaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FireBaseServiceImpl implements FireBaseService {
    @Value("${firebase.bucket}")
    private String firebaseBucket;

    @Override
    public String downloadFile(String id) {
        var blobId = BlobId.fromGsUtilUri(id);
        var bucket = StorageClient.getInstance().bucket(firebaseBucket);
        return bucket.getStorage().get(blobId).getMediaLink();
    }

    @Override
    public String uploadFile(MultipartFile file, String folderName) {
        var filename = UUID.randomUUID().toString();
        var bucket = StorageClient.getInstance().bucket(firebaseBucket);
        var objectName = folderName + filename;
        try (var content = new ByteArrayInputStream(file.getBytes())) {
            var blob = bucket.create(objectName, content, file.getContentType());
            return blob.getBlobId().toGsUtilUri();
        } catch (IOException e) {
            throw new InternalServerErrorException("Lỗi server", e.getMessage(), 10012);
        }
    }

    @Override
    public void deleteFile(String id) {
        var blobId = BlobId.fromGsUtilUri(id);
        var bucket = StorageClient.getInstance().bucket(firebaseBucket);
        bucket.getStorage().delete(blobId);
    }
}

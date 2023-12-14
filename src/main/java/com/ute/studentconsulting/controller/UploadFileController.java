package com.ute.studentconsulting.controller;

import com.ute.studentconsulting.firebase.service.FireBaseService;
import com.ute.studentconsulting.model.FileModel;
import com.ute.studentconsulting.payload.response.ApiSuccessResponse;
import com.ute.studentconsulting.payload.response.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
@Slf4j
public class UploadFileController {
    private final FireBaseService fireBaseService;

    @PostMapping("/news")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Không tìm thấy file",
                            "Vui lòng kiểm tra lại file", 10081));
        }
        var folderName = "news/";
        var blobId = fireBaseService.uploadFile(file, folderName);
        var url = fireBaseService.downloadFile(blobId);
        return ResponseEntity.ok(new ApiSuccessResponse<>(new FileModel(blobId, url)));
    }
}

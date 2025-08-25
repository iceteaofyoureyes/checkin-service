package com.wiinvent.checkinservice.service.impl;

import com.wiinvent.checkinservice.exception.FileValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {

    @Value("${app.upload.max-file-size}")
    private long maxFileSize;

    public String storeAvatarFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new FileValidationException("Invalid Avatar File");
        }

        if (file.getSize() > maxFileSize) {
            throw new FileValidationException("Avatar File size exceeds limit (" + maxFileSize/1024/1024 + "MB)");
        }

        // TODO: Implement upload to S3, GCP, Azure Blob, etc.
        // Mock return fake avatar URL for now

        return "https://i.pravatar.cc/300";

    }


    private void validateFile(MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            if (file.getSize() > maxFileSize) {
                throw new FileValidationException("File size exceeds the maximum allowed: " + maxFileSize + " bytes");
            }
        }
    }
}

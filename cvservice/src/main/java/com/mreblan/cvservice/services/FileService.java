package com.mreblan.cvservice.services;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    void uploadFile(MultipartFile file, String dirPath) throws IOException;
    String extractText(MultipartFile file) throws IOException;
}

package com.mreblan.cvservice.services;

import com.mreblan.cvservice.models.CvModel;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    void   uploadFile(MultipartFile file, String dirPath) throws IOException;
    String extractText(MultipartFile file) throws IOException;
    byte[] generateFile(CvModel cv) throws IOException;
}

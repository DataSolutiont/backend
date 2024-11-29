package com.mreblan.cvservice.services.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mreblan.cvservice.services.FileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class PDFService implements FileService {

    private final PDFTextStripper pdfStripper;

    @Override
    public void uploadFile(MultipartFile file, String dirPath) throws IOException {
            File uploadDir = new File(dirPath);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            File pdfFile = new File(uploadDir, file.getOriginalFilename());

            try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
                fos.write(file.getBytes());
            }

            log.info("FILE WRITTEN: {}", pdfFile.getAbsolutePath());
    } 

    @Override
    public String extractText(MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("uploaded", ".pdf");
        Files.copy(file.getInputStream(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        PDDocument document = Loader.loadPDF(tempFile);
        
        String text = pdfStripper.getText(document);
        document.close();

        return text;
    }
}

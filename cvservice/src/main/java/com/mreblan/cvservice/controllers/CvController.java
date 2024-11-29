package com.mreblan.cvservice.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mreblan.cvservice.services.FileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cv")
public class CvController {
    
    private static final String UPLOAD_DIR = "/Users/danilboarov/Documents/upload_test";
    private final FileService pdfService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (
            file.isEmpty() ||
            !file.getOriginalFilename().endsWith(".pdf")
        ) {
            return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("Загрузите PDF файл");
        }

        try {
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            File pdfFile = new File(uploadDir, file.getOriginalFilename());

            try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
                fos.write(file.getBytes());
            }

            return ResponseEntity.ok("Файл загружен");
        } catch (IOException e) {
            return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Ошибка загрузки файла");
        }
    }

    @PostMapping("/extract")
    public ResponseEntity<String> extractTextFromFile(@RequestParam("file") MultipartFile file) {
        try {
            String text = pdfService.extractText(file);
            log.info("TEXT FROM FILE: {}", text);

            return ResponseEntity.ok("Text extracted");
        } catch (IOException e) {
            log.error(e.getMessage());

            return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Something went wrong with extracting");
        }
    }
}

package com.mreblan.cvservice.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mreblan.cvservice.models.CvModel;
import com.mreblan.cvservice.services.CvService;
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
    private final CvService cvService;

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
        String text = null;
        try {
            text = pdfService.extractText(file);
            log.info("TEXT FROM FILE: {}", text);

            return ResponseEntity.ok("Text extracted");
        } catch (IOException e) {
            log.error(e.getMessage());

            return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Something went wrong with extracting");
        }
    }

    @PostMapping("/uploadCv")
    public ResponseEntity<String> uploadCv(@RequestParam("file") MultipartFile file) {
        String text = null;

        try {
            text = pdfService.extractText(file);
            log.info("TEXT FROM FILE: {}", text);
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        if (text != null) {
            cvService.saveCv(text);

            return ResponseEntity.ok("CV Saved");
        }

        return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong");
    }

    @GetMapping("/allCv")
    public ResponseEntity<String> getAllCv() {
        cvService.getAllCv();

        return ResponseEntity.ok("All CVs getted");
    }

    @GetMapping("/search")
    public List<CvModel> searchCvs(@RequestParam String keyword) {
        return cvService.getCvByKeyword(keyword);
    }

} 

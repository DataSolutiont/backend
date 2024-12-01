package com.mreblan.cvservice.controllers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.font.encoding.Encoding;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mreblan.cvservice.models.CvModel;
import com.mreblan.cvservice.models.FindByKeywordsRequest;
import com.mreblan.cvservice.models.CvResponse;
import com.mreblan.cvservice.models.Response;

import com.mreblan.cvservice.services.CvService;
import com.mreblan.cvservice.services.FileService;
import com.mreblan.cvservice.services.ZipService;
import com.mreblan.cvservice.services.impl.TxtService;

import com.mreblan.cvservice.exceptions.CvsNotFoundException;
import com.mreblan.cvservice.exceptions.CvAlreadyExistsException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cvs")
public class CvController {
    
    private final ZipService zipService;
    private final FileService pdfService;
    private final TxtService txtService;
    private final CvService cvService;

    // @PostMapping("/upload")
    // public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
    //     if (
    //         file.isEmpty() ||
    //         !file.getOriginalFilename().endsWith(".pdf")
    //     ) {
    //         return ResponseEntity
    //                     .status(HttpStatus.BAD_REQUEST)
    //                     .body("Загрузите PDF файл");
    //     }
    //
    //     try {
    //         File uploadDir = new File(UPLOAD_DIR);
    //         if (!uploadDir.exists()) {
    //             uploadDir.mkdir();
    //         }
    //
    //         File pdfFile = new File(uploadDir, file.getOriginalFilename());
    //
    //         try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
    //             fos.write(file.getBytes());
    //         }
    //
    //         return ResponseEntity.ok("Файл загружен");
    //     } catch (IOException e) {
    //         return ResponseEntity
    //                     .status(HttpStatus.INTERNAL_SERVER_ERROR)
    //                     .body("Ошибка загрузки файла");
    //     }
    // }

    // @PostMapping("/extract")
    // public ResponseEntity<String> extractTextFromFile(@RequestParam("file") MultipartFile file) {
    //     String text = null;
    //     try {
    //         text = pdfService.extractText(file);
    //         log.info("TEXT FROM FILE: {}", text);
    //
    //         return ResponseEntity.ok("Text extracted");
    //     } catch (IOException e) {
    //         log.error(e.getMessage());
    //
    //         return ResponseEntity
    //                     .status(HttpStatus.INTERNAL_SERVER_ERROR)
    //                     .body("Something went wrong with extracting");
    //     }
    // }

    @PostMapping("/saveCv")
    public ResponseEntity<Response> uploadCv(@RequestParam("file") MultipartFile file) {
        String text = null;

        if (
            file.isEmpty() || 
            !file.getOriginalFilename().endsWith(".pdf")
        ) {
            return createResponse(HttpStatus.BAD_REQUEST, new Response(false, "Файл не загружен или не является файлом формата pdf"));
        }

        try {
            text = pdfService.extractText(file);
            log.info("TEXT FROM FILE: {}", text);
        } catch (IOException e) {
            log.error(e.getMessage());

            return createResponse(HttpStatus.INTERNAL_SERVER_ERROR, new Response(false, "Что-то пошло не так"));
        }

        if (text != null) {
            try {
                cvService.saveCv(text);
            } catch (CvAlreadyExistsException e) {
                log.error(e.getMessage());

                return createResponse(HttpStatus.BAD_REQUEST, new Response(false, "Данное резюме уже есть в базе"));
            }

            return createResponse(HttpStatus.OK, new Response(true, "Резюме получено"));
        }

        return createResponse(HttpStatus.INTERNAL_SERVER_ERROR, new Response(false, "Что-то пошло не так"));
    }
    
    @GetMapping("/search")
    public ResponseEntity<byte[]> searchByKeywords(@RequestBody FindByKeywordsRequest request) {
        List<CvModel> cvs = cvService.getCvByKeyword(request.getKeywords());
        ByteArrayOutputStream bOutputStream = null;
        ZipOutputStream zOutputStream = null;

        try {
            bOutputStream = new ByteArrayOutputStream();
            zOutputStream = zipService.generateZipOutputStream(bOutputStream);
        
            int counter = 1;
            for (CvModel cv : cvs) {
                byte[] file = txtService.generateFile(cv); 

                zipService.appendZip(zOutputStream, file, "resume%d".formatted(counter));
                counter++;
            }

            zOutputStream.close();
        } catch (IOException e) {
            log.error(e.getMessage());

            return createCvResponse(HttpStatus.INTERNAL_SERVER_ERROR, null);
        } 

        return createCvResponse(HttpStatus.OK, bOutputStream.toByteArray());
    }

    @Deprecated
    @GetMapping()
    public ResponseEntity<String> getAllCv() {
        cvService.getAllCv();

        return ResponseEntity.ok("All CVs getted");
    }

    private ResponseEntity<byte[]> createCvResponse(HttpStatus status, byte[] data) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=resumes.zip");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/zip");

        return ResponseEntity
                    .status(status)
                    .headers(headers)
                    .body(data);
    } 

    private ResponseEntity<Response> createResponse(HttpStatus status, Response response) {
        return ResponseEntity
                    .status(status)
                    .body(response);
    }
} 

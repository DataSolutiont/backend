package com.mreblan.cvservice.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipOutputStream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mreblan.cvservice.models.CvModel;
import com.mreblan.cvservice.models.FindByKeywordsRequest;
import com.mreblan.cvservice.models.Response;

import com.mreblan.cvservice.services.CvService;
import com.mreblan.cvservice.services.FileService;
import com.mreblan.cvservice.services.ZipService;
import com.mreblan.cvservice.services.impl.TxtService;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import com.mreblan.cvservice.exceptions.CvAlreadyExistsException;
import com.mreblan.cvservice.exceptions.JsonMappingFailedException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@OpenAPIDefinition(info = @Info(title = "CV API", version = "v1"))
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cvs")
public class CvController {
    
    private final ZipService zipService;
    private final FileService pdfService;
    private final TxtService txtService;
    private final CvService cvService;


    @Operation(summary = "Сохранение резюме",
        description = "Сохраняет текст из резюме в БД"
    )
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200",
            description = "Резюме успешно добавлено",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = "{ \"success\": true, \"description\": \"Резюме получено\"}"
                    )
                )
            ),
            @ApiResponse(responseCode = "400",
            description = "Отсутствует файл или у него не тот формат",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = "{ \"success\": false, \"description\": \"Файл не загружен или не является файлом формата pdf\"}"
                    )
                )
            ),
            @ApiResponse(responseCode = "500",
            description = "При сохранении БД что-то пошло не так",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        example = "{ \"success\": false, \"description\": \"Что-то пошло не так\"}"
                    )
                )
            )
        }
    )
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
            } catch (JsonMappingFailedException e) {
                log.error(e.getMessage());

                return createResponse(HttpStatus.INTERNAL_SERVER_ERROR, new Response(false, "Что-то пошло не так"));
            }
            return createResponse(HttpStatus.OK, new Response(true, "Резюме получено"));
        }

        return createResponse(HttpStatus.INTERNAL_SERVER_ERROR, new Response(false, "Что-то пошло не так"));
    }
    
    @Operation(summary = "Ищет резюме по ключевым словам",
        description = "Ищет резюме, в которых присутствуют ключевые слова, указанные в запросе. Возвращает zip-архив"
    )
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200",
            description = "Резюме найдены",
            content = @Content(
                    mediaType = "application/zip",
                    schema = @Schema(
                        type = "string", format = "binary"
                    )
                )
            ),
            @ApiResponse(responseCode = "500",
            description = "При формировании архива что-то пошло не так"
            )
        }
    )
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

    @GetMapping()
    public ResponseEntity<String> getAll() {
        cvService.getAllCv();

        return ResponseEntity.ok("Ok");
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

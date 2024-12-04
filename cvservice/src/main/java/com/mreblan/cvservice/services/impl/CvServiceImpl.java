package com.mreblan.cvservice.services.impl;

import java.util.List;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mreblan.cvservice.models.CvModel;
import com.mreblan.cvservice.models.FindByKeywordsRequest;
import com.mreblan.cvservice.models.yandexgpt.response.YandexGptResponse;
import com.mreblan.cvservice.repositories.CustomCvRepository;
import com.mreblan.cvservice.repositories.CvRepository;
import com.mreblan.cvservice.services.AiService;
import com.mreblan.cvservice.services.CvService;

import com.mreblan.cvservice.exceptions.CvsNotFoundException;
import com.mreblan.cvservice.exceptions.CvAlreadyExistsException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
// @RequiredArgsConstructor
@Service
public class CvServiceImpl implements CvService {

    private final CustomCvRepository customCvRepository;
    private final CvRepository cvRepository;
    private final AiService yandexGptService;

    @Autowired
    public CvServiceImpl(CvRepository cvRepository, AiService aiService, CustomCvRepository customCvRepository) {
        this.cvRepository = cvRepository;
        this.yandexGptService = aiService;
        this.customCvRepository = customCvRepository;
    }
    
    @Override
    public void saveCv(String cvText) {
        if (isCvExists(cvText)) {
            throw new CvAlreadyExistsException("Cv already in DB");
        }

        CvModel fromAi = yandexGptService.sendMessageSync(cvText);

        cvRepository.save(fromAi);
    }

    @Override
    public void saveCvAsync(String cvText) {
        if (isCvExists(cvText)) {
            Mono.error(new CvAlreadyExistsException("Cv already in DB"));
        }

        yandexGptService.sendMessageAsync(cvText)
            .subscribe(response -> {
                log.info("YANDEXGPT RESPONSE: {}", response.toString())
                String json = response.getResult().getAlternatives().get(0).getMessage().getText();
                CvModel cv = yandexGptService.jsonToCvModel(json, cvText);

                cvRepository.save(cv);
            }, error -> {
                    log.error("ERROR WITH RESPONSE HANDLING: {}", error.getMessage());
                });
    }

    @Override
    public void getAllCv() {
        Iterable<CvModel> cvs = cvRepository.findAll();

        StreamSupport.stream(cvs.spliterator(), false)
                .forEach(cv -> System.out.println(cv.toString()));
    }

    @Override
    public List<CvModel> getCvByKeywordsStrict(FindByKeywordsRequest request) {
        List<CvModel> result = customCvRepository.searchByKeywordsStrict(request);

        // result.forEach(cv -> System.out.println(cv.toString()));

        if (result == null || result.isEmpty()) {
            throw new CvsNotFoundException("Cvs with such keywords not found");
        }

        return result;
    }

    @Override
    public List<CvModel> getCvByKeywordsWeak(FindByKeywordsRequest request) {
        List<CvModel> result = customCvRepository.searchByKeywordsWeak(request);

        // result.forEach(cv -> System.out.println(cv.toString()));

        if (result == null) {
            throw new CvsNotFoundException("Cvs with such keywords not found");
        }

        return result;
    }

    private boolean isCvExists(String cvText) {
        return (cvRepository.findByCvText(cvText) != null);
    }
}

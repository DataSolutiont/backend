package com.mreblan.cvservice.services.impl;

import java.util.List;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mreblan.cvservice.models.CvModel;

import com.mreblan.cvservice.repositories.CvRepository;
import com.mreblan.cvservice.services.AiService;
import com.mreblan.cvservice.services.CvService;

import com.mreblan.cvservice.exceptions.CvsNotFoundException;
import com.mreblan.cvservice.exceptions.CvAlreadyExistsException;

import lombok.RequiredArgsConstructor;

// @RequiredArgsConstructor
@Service
public class CvServiceImpl implements CvService {

    private final CvRepository cvRepository;
    private final AiService yandexGptService;

    @Autowired
    public CvServiceImpl(CvRepository cvRepository, AiService aiService) {
        this.cvRepository = cvRepository;
        this.yandexGptService = aiService;
    }
    
    @Override
    public void saveCv(String cvText) {
        CvModel existingCv = cvRepository.findByCvText(cvText);

        if (existingCv != null) {
            throw new CvAlreadyExistsException("This CV already in DB");
        }

        CvModel fromAi = yandexGptService.sendMessage(cvText);

        cvRepository.save(fromAi);
    }

    @Override
    public void getAllCv() {
        Iterable<CvModel> cvs = cvRepository.findAll();

        StreamSupport.stream(cvs.spliterator(), false)
                .forEach(cv -> System.out.println(cv.toString()));
    }

    @Override
    public List<CvModel> getCvByKeyword(List<String> keywordsList) {
        String keywords = String.join(" ", keywordsList);
        List<CvModel> result = cvRepository.searchByKeyword(keywords);

        // result.forEach(cv -> System.out.println(cv.toString()));

        if (result == null) {
            throw new CvsNotFoundException("Cvs with such keywords not found");
        }

        return result;
    }
}

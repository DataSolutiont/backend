package com.mreblan.cvservice.services.impl;

import java.util.List;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;

import com.mreblan.cvservice.models.CvModel;
import com.mreblan.cvservice.repositories.CvRepository;
import com.mreblan.cvservice.services.CvService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CvServiceImpl implements CvService {

    private final CvRepository cvRepository;
    
    @Override
    public void saveCv(String cvText) {
        CvModel cv = new CvModel(cvText);

        cvRepository.save(cv);
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

        result.forEach(cv -> System.out.println(cv.toString()));

        return result;
    }
}

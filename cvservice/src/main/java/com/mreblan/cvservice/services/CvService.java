package com.mreblan.cvservice.services;

import java.util.List;

import com.mreblan.cvservice.models.CvModel;
import com.mreblan.cvservice.models.FindByKeywordsRequest;

import reactor.core.publisher.Mono;

public interface CvService {
    void saveCv(String cvText);
    void saveCvAsync(String cvText);
    // Mono<Void> saveCvAsync(String cvText);
    void getAllCv();
    List<CvModel> getCvByKeywordsStrict(FindByKeywordsRequest request);
    List<CvModel> getCvByKeywordsWeak(FindByKeywordsRequest request);
}

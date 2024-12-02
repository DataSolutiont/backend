package com.mreblan.cvservice.services;

import java.util.List;

import com.mreblan.cvservice.models.CvModel;
import com.mreblan.cvservice.models.FindByKeywordsRequest;

public interface CvService {
    void saveCv(String cvText);
    void getAllCv();
    List<CvModel> getCvByKeywordsStrict(FindByKeywordsRequest request);
    List<CvModel> getCvByKeywordsWeak(FindByKeywordsRequest request);
}

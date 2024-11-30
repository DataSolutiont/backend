package com.mreblan.cvservice.services;

import java.util.List;

import com.mreblan.cvservice.models.CvModel;

public interface CvService {
    void saveCv(String cvText);
    void getAllCv();
    List<CvModel> getCvByKeyword(List<String> keyword);
}

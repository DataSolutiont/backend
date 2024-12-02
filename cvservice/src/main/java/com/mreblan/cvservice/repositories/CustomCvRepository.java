package com.mreblan.cvservice.repositories;

import java.util.List;

import com.mreblan.cvservice.models.FindByKeywordsRequest;
import com.mreblan.cvservice.models.CvModel;

public interface CustomCvRepository {

    List<CvModel> searchByKeywordsStrict(FindByKeywordsRequest request);
    List<CvModel> searchByKeywordsWeak(FindByKeywordsRequest request);
}

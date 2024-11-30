package com.mreblan.cvservice.repositories;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.mreblan.cvservice.models.CvModel;

public interface CvRepository extends ElasticsearchRepository<CvModel, String> {
    @Query("{\"match\": {\"cvText\": \"%?0%\"}}")
    List<CvModel> searchByKeyword(String keyword);
}

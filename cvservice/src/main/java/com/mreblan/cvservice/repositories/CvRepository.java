package com.mreblan.cvservice.repositories;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.mreblan.cvservice.models.CvModel;

@Repository
public interface CvRepository extends ElasticsearchRepository<CvModel, String> {
    @Query("{\"match\": {\"cvText\": {\"query\": \"%?0%\", \"operator\": \"or\"}}}")
    // @Query("{\"match\": {\"fullText\": \"%?0%\"}}")
    List<CvModel> searchByKeyword(String keyword);
    CvModel findByCvText(String cvText);
}

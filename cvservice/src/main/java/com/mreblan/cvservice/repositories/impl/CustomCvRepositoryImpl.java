package com.mreblan.cvservice.repositories.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
// import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Repository;

import com.mreblan.cvservice.models.CvModel;
import com.mreblan.cvservice.models.FindByKeywordsRequest;
import com.mreblan.cvservice.repositories.CustomCvRepository;

import lombok.extern.slf4j.Slf4j;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

@Slf4j
@Repository
public class CustomCvRepositoryImpl implements CustomCvRepository {
    @Autowired
    private ElasticsearchOperations operations;

    @Override
    public List<CvModel> searchByKeywordsStrict(FindByKeywordsRequest request) {
        List<CvModel> result = new ArrayList<>();

        Criteria criteria = new Criteria();

        if (request.getKwSkills() != null && !request.getKwSkills().isEmpty()) {
            criteria.and("skills").is(request.getKwSkills());
        }

        if (request.getKwYearsOfExp() > 0 && Integer.valueOf(request.getKwYearsOfExp()).equals(null)) {
            criteria.and("expYears").greaterThan(request.getKwYearsOfExp());
        }
        
        if (request.getKwCompanies() != null && !request.getKwCompanies().isEmpty()) {
            criteria.and("companies").is(request.getKwCompanies());
        }

        if (request.getKwWorkFormat() != null && !request.getKwWorkFormat().isEmpty()) {
            criteria.and("workFormat").is(request.getKwWorkFormat());
        }

        Query query = new CriteriaQuery(criteria);

        try {
            var response = operations.search(query, CvModel.class);

            for (int i = 0; i < response.getTotalHits(); i++) {
                CvModel cv = response.getSearchHit(i).getContent();
                result.add(cv);
            }

            return result;
        } catch (Exception e) {
            log.error(e.getMessage());

            return null;
        }
    }

    @Override
    public List<CvModel> searchByKeywordsWeak(FindByKeywordsRequest request) {
        List<CvModel> result = new ArrayList<>();

        Criteria criteria = new Criteria();

        if (request.getKwSkills() != null && !request.getKwSkills().isEmpty()) {
            criteria.or("skills").is(request.getKwSkills());
        }

        if (request.getKwYearsOfExp() > 0 && Integer.valueOf(request.getKwYearsOfExp()).equals(null)) {
            criteria.or("expYears").greaterThan(request.getKwYearsOfExp());
        }
        
        if (request.getKwCompanies() != null && !request.getKwCompanies().isEmpty()) {
            criteria.or("companies").is(request.getKwCompanies());
        }

        if (request.getKwWorkFormat() != null && !request.getKwWorkFormat().isEmpty()) {
            criteria.or("workFormat").is(request.getKwWorkFormat());
        }

        Query query = new CriteriaQuery(criteria);

        try {
            var response = operations.search(query, CvModel.class);

            for (int i = 0; i < response.getTotalHits(); i++) {
                CvModel cv = response.getSearchHit(i).getContent();
                result.add(cv);
            }

            return result;
        } catch (Exception e) {
            log.error(e.getMessage());

            return null;
        }
    }
}

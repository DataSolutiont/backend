package com.mreblan.cvservice.repositories.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Repository;

import com.mreblan.cvservice.models.CvModel;
import com.mreblan.cvservice.models.FindByKeywordsRequest;
import com.mreblan.cvservice.repositories.CustomCvRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class CustomCvRepositoryImpl implements CustomCvRepository {
    @Autowired
    private ElasticsearchOperations operations;

    @Override
    public List<CvModel> searchByKeywordsStrict(FindByKeywordsRequest request) {
        List<CvModel> result = new ArrayList<>();

        // Criteria criteria = new Criteria();

        StringBuilder strQuery = new StringBuilder("{\"bool\": {\"must\": ["); 

        if (request.getKwSkills() != null && !request.getKwSkills().isEmpty()) {
            // criteria.and("skills").is(request.getKwSkills());

            for (String skill : request.getKwSkills()) {
                strQuery.append("{\"match\": {\"skills\": \"" + skill + "\"}},");
            }
        }

        if (request.getKwYearsOfExp() > 0) {
            // criteria.and("expYears").greaterThan(request.getKwYearsOfExp());
            strQuery.append("{\"range\": {\"expYears\": {\"gte\": " + request.getKwYearsOfExp() + "}}},");
        }
        
        if (request.getKwCompanies() != null && !request.getKwCompanies().isEmpty()) {
            // criteria.and("companies").is(request.getKwCompanies());
            for (String company : request.getKwCompanies()) {
                strQuery.append("{\"match\": {\"companies\": \"" + company + "\"}},");
            }
        }

        if (request.getKwWorkFormat() != null && !request.getKwWorkFormat().isEmpty()) {
            // criteria.and("workFormat").is(request.getKwWorkFormat());
            strQuery.append("{\"match\": {\"workFormat\": \"" + request.getKwWorkFormat() + "\"}},");
        }

        if (strQuery.charAt(strQuery.length() - 1) == ',') {
            strQuery.setLength(strQuery.length() - 1);
        }

        strQuery.append("]}}");

        // Query query = new CriteriaQuery(criteria);
        StringQuery stringQuery = new StringQuery(strQuery.toString());

        log.info("QUERY: {}", strQuery.toString());

        try {
            var response = operations.search(stringQuery, CvModel.class);

            for (int i = 0; i < response.getTotalHits(); i++) {
                CvModel cv = response.getSearchHit(i).getContent();
                result.add(cv);
            }

            for (CvModel el : result) {
                log.info("CVS FOUND: {}", el.toString());
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

        // Criteria criteria = new Criteria();
        StringBuilder strQuery = new StringBuilder("{\"bool\": {\"should\": ["); 

        if (request.getKwSkills() != null && !request.getKwSkills().isEmpty()) {
            // criteria.or("skills").is(request.getKwSkills());
            // String skillsStr = "[" + String.join(",", Arrays.stream(request.getKwSkills())
            //                                             .map(skill -> "\"" + skill + "\"")
            //                                             .toArray(String[]::new)
            // ) + "]";
            
            strQuery.append("{\"terms\": {\"skills\": [");

            for (int i = 0; i < request.getKwSkills().size(); i++) {
                String skill = request.getKwSkills().get(i);
                strQuery.append("\"").append(skill).append("\"");
                if (i < request.getKwSkills().size() - 1) {
                    strQuery.append(",");
                }
            }
            strQuery.append("]}},");
        }

        if (request.getKwYearsOfExp() > 0) {
            // criteria.or("expYears").greaterThan(request.getKwYearsOfExp());
            strQuery.append("{\"range\": {\"expYears\": {\"gte\": " + request.getKwYearsOfExp() + "}}},");
        }
        
        if (request.getKwCompanies() != null && !request.getKwCompanies().isEmpty()) {
            // criteria.or("companies").is(request.getKwCompanies());
            strQuery.append("{\"terms\": {\"companies\": [");

            for (int i = 0; i < request.getKwCompanies().size(); i++) {
                String company = request.getKwCompanies().get(i);
                strQuery.append("\"").append(company).append("\"");
                if (i < request.getKwCompanies().size() - 1) {
                    strQuery.append(",");
                }
            }
            strQuery.append("]}},");
        }

        if (request.getKwWorkFormat() != null && !request.getKwWorkFormat().isEmpty()) {
            // criteria.or("workFormat").is(request.getKwWorkFormat());
            strQuery.append("{\"match\": {\"workFormat\": \"" + request.getKwWorkFormat() + "\"}},");
        }

        if (strQuery.charAt(strQuery.length() - 1) == ',') {
            strQuery.setLength(strQuery.length() - 1);
        }

        // Query query = new CriteriaQuery(criteria);
        StringQuery query = new StringQuery(strQuery.toString());

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

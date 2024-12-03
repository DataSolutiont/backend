package com.mreblan.cvservice.repositories.impl;

import java.util.ArrayList;
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


        StringBuilder strQuery = new StringBuilder("{\"bool\": {\"must\": ["); 

        if (request.getKwSkills() != null && !request.getKwSkills().isEmpty()) {

            for (String skill : request.getKwSkills()) {
                strQuery.append("{\"match\": {\"skills\": \"" + skill + "\"}},");
            }
        }

        if (request.getKwYearsOfExp() > 0) {
            strQuery.append("{\"range\": {\"expYears\": {\"gte\": " + request.getKwYearsOfExp() + "}}},");
        }
        
        if (request.getKwCompanies() != null && !request.getKwCompanies().isEmpty()) {
            for (String company : request.getKwCompanies()) {
                strQuery.append("{\"match\": {\"companies\": \"" + company + "\"}},");
            }
        }

        if (request.getKwWorkFormat() != null && !request.getKwWorkFormat().isEmpty()) {
            strQuery.append("{\"match\": {\"workFormat\": \"" + request.getKwWorkFormat() + "\"}},");
        }

        if (strQuery.charAt(strQuery.length() - 1) == ',') {
            strQuery.setLength(strQuery.length() - 1);
        }

        strQuery.append("]}}");

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

        StringBuilder strQuery = new StringBuilder("{\"bool\": {\"should\": ["); 

        if (request.getKwSkills() != null && !request.getKwSkills().isEmpty()) {
            
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
            strQuery.append("{\"range\": {\"expYears\": {\"gte\": " + request.getKwYearsOfExp() + "}}},");
        }
        
        if (request.getKwCompanies() != null && !request.getKwCompanies().isEmpty()) {
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
            strQuery.append("{\"match\": {\"workFormat\": \"" + request.getKwWorkFormat() + "\"}},");
        }

        if (strQuery.charAt(strQuery.length() - 1) == ',') {
            strQuery.setLength(strQuery.length() - 1);
        }

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

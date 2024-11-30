package com.mreblan.cvservice.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import lombok.Data;

@Data
@Document(indexName = "cv")
public class CvModel {
    @Id
    private String id;
    private String cvText;

    public CvModel(String cvText) {
        this.cvText = cvText;
    }
}

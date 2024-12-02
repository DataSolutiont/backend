package com.mreblan.cvservice.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

import lombok.Data;
import lombok.Setter;

@Setter
@Data
@Document(indexName = "cv")
public class CvModel {
    @Id
    private String   id;
    private String   cvText;
    private List<String> skills;
    private int      expYears;
    private List<String> companies;
    private String workFormat;

    public CvModel(String cvText) {
        this.cvText = cvText;
    }
}

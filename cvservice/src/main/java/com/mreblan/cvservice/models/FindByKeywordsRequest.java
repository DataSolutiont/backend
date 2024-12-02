package com.mreblan.cvservice.models;

import java.util.List;

import lombok.Data;

@Data
public class FindByKeywordsRequest {
    private List<String> kwSkills;
    private int          kwYearsOfExp;
    private List<String> kwCompanies;
    private String       kwWorkFormat;
}

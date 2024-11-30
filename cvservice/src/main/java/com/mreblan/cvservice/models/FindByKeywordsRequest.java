package com.mreblan.cvservice.models;

import java.util.List;

import lombok.Data;

@Data
public class FindByKeywordsRequest {
    private List<String> keywords;
}

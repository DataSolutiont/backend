package com.mreblan.cvservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CvResponse {
    private boolean success;
    private String  description;
    private byte[]  data;
}

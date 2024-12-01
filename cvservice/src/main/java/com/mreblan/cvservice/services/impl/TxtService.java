package com.mreblan.cvservice.services.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.stereotype.Service;

import com.mreblan.cvservice.models.CvModel;

@Service
public class TxtService {
    
    public byte[] generateFile(CvModel cv) throws IOException {
        ByteArrayOutputStream oStream = new ByteArrayOutputStream();
        String cvText = cv.getCvText();
        try {
            oStream.write(cvText.getBytes());
        } catch (IOException e) {
            throw new IOException("Error with creating .txt", e);
        }

        return oStream.toByteArray();
    }

}

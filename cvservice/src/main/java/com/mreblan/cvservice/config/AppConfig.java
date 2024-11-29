package com.mreblan.cvservice.config;

import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public PDFTextStripper pdfTextStripper() {
        return new PDFTextStripper();
    }
}

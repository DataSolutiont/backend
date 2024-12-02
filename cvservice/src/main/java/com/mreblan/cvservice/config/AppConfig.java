package com.mreblan.cvservice.config;

import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {
    @Bean
    public PDFTextStripper pdfTextStripper() {
        return new PDFTextStripper();
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                    .baseUrl("https://llm.api.cloud.yandex.net/foundationModels/v1/completion")
                    .defaultHeader("Content-Type", "application/json")
                    .build();
    } 

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
            .baseUrl("https://llm.api.cloud.yandex.net/foundationModels/v1/completion")
            .defaultHeaders(
                httpHeader -> {
                    httpHeader.set("Content-Type", "application/json");
                })
            .build();
    }
}

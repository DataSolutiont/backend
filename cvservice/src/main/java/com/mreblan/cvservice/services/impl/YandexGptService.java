package com.mreblan.cvservice.services.impl;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mreblan.cvservice.config.YandexGptProperties;
import com.mreblan.cvservice.exceptions.AiRequestFailedException;
import com.mreblan.cvservice.exceptions.JsonMappingFailedException;
import com.mreblan.cvservice.factories.YandexGptRequestFactory;
import com.mreblan.cvservice.models.CvModel;
import com.mreblan.cvservice.models.yandexgpt.YandexGptMessage;
import com.mreblan.cvservice.models.yandexgpt.request.YandexGptRequest;
import com.mreblan.cvservice.models.yandexgpt.response.YandexGptResponse;
import com.mreblan.cvservice.services.AiService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class YandexGptService implements AiService {
    private static YandexGptProperties properties;
    private final YandexGptRequestFactory requestFactory;
    private final RestClient restClient;
    private final WebClient webclient;
    private final ObjectMapper objectMapper;

    @Override
    public CvModel sendMessageSync(String cvText) {
        YandexGptRequest request = requestFactory.createRequest(cvText);

        // ResponseEntity<YandexGptResponse> responseEntity = restClient.post().header("Authorization", String.format("Api-key %s", properties.API_KEY))
        //                                           .header("x-folder-id", properties.FOLDER_ID)
        //                                           .contentType(MediaType.APPLICATION_JSON)
        //                                           .body(request)
        //                                           .retrieve()
        //                                           .toEntity(YandexGptResponse.class);
        //
        // YandexGptResponse response = responseEntity.getBody();

        YandexGptResponse response = webclient.post().header("Authorization", "Api-key %s".formatted(properties.API_KEY))
                                        .header("x-folder-id", properties.FOLDER_ID)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(request)
                                        .retrieve()
                                        .bodyToMono(YandexGptResponse.class)
                                        .block();

        log.info(response.toString());

        YandexGptResponse.Result.Alternative.Message message = response.getResult().getAlternatives().get(0).getMessage();

        return jsonToCvModel(message.getText(), cvText);
    }

    @Override
    public Mono<YandexGptResponse> sendMessageAsync(String cvText) {
        YandexGptRequest request = requestFactory.createRequest(cvText);

        Mono<YandexGptResponse> monoResponse = webclient.post().header("Authorization", "Api-key %s".formatted(properties.API_KEY))
                                                    .header("x-folder-id", properties.FOLDER_ID)
                                                    .contentType(MediaType.APPLICATION_JSON)
                                                    .bodyValue(request)
                                                    .retrieve()
                                                    .bodyToMono(YandexGptResponse.class)
                                                    .doOnError(error -> {
            log.error("AN ERROR ACQUIRED: {}", error);
            throw new AiRequestFailedException("Failed to reach the AI");
        });

        return monoResponse;
    }

    @Override
    public CvModel jsonToCvModel(String json, String cvText) {
        json = json.replace("`", "");
        CvModel model = new CvModel(cvText);
        try {
            Map<String, Object> fields = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
            model.setSkills( (List<String>) fields.get("skills"));
            model.setExpYears( (int) fields.get("expYears"));
            model.setCompanies( (List<String>) fields.get("companies"));
            model.setWorkFormat( (String) fields.get("workFormat"));
        } catch (Exception e) { 
            throw new JsonMappingFailedException("Failed to map json");
        }

        return model;
    }
}

package com.mreblan.cvservice.services;

import com.mreblan.cvservice.models.CvModel;
import com.mreblan.cvservice.models.yandexgpt.response.YandexGptResponse;

import reactor.core.publisher.Mono;

public interface AiService {

    CvModel sendMessageSync(String content);
    Mono<YandexGptResponse> sendMessageAsync(String content);
    // CvModel processAsyncResponse(Mono<?> response);
    CvModel jsonToCvModel(String json, String content);
}

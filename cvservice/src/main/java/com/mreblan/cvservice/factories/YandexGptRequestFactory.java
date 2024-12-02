package com.mreblan.cvservice.factories;

import com.mreblan.cvservice.models.yandexgpt.request.YandexGptRequest;

public interface YandexGptRequestFactory {

    YandexGptRequest createRequest(String content);
}

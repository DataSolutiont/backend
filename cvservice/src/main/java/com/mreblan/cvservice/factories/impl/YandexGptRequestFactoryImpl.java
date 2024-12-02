package com.mreblan.cvservice.factories.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.mreblan.cvservice.config.YandexGptProperties;
import com.mreblan.cvservice.factories.YandexGptRequestFactory;
import com.mreblan.cvservice.models.yandexgpt.YandexGptMessage;
import com.mreblan.cvservice.models.yandexgpt.request.YandexGptCompletionOptions;
import com.mreblan.cvservice.models.yandexgpt.request.YandexGptRequest;

import lombok.RequiredArgsConstructor;
import lombok.AllArgsConstructor;

// @AllArgsConstructor
@RequiredArgsConstructor
@Component
public class YandexGptRequestFactoryImpl implements YandexGptRequestFactory {
    private static YandexGptProperties properties;

    public YandexGptRequest createRequest(String cvText) {
        String modelUri = String.format("gpt://%s/yandexgpt-lite", properties.FOLDER_ID);

        YandexGptCompletionOptions opts = new YandexGptCompletionOptions(false, 0.2f, "1000");
        List<YandexGptMessage> msgs = new ArrayList<>();
        StringBuilder rules = new StringBuilder();

        rules.append("Проанализируй текст резюме и выдели следующие поля оттуда:\n");
        rules.append("навыки кандидата, сколько лет опыта работы, компании, в которых он работал, предпочитаемый формат работы.\n");
        rules.append("Предоставь ответ в JSON формате, не используя никакие разметки и специальные символы.\n");
        rules.append("JSON должен быть следующего вида:\n");
        rules.append("{\n \"skills: [\"skills\"], \"expYears\": int, \"companies\": [\"companies\"], \"workFormat\": \"string\" }");

        msgs.add(new YandexGptMessage("system", rules.toString()));
        msgs.add(new YandexGptMessage("user", cvText));

        return YandexGptRequest.builder()
                        .modelUri(modelUri)
                        .completionOptions(opts)
                        .messages(msgs)
                        .build();
    }
}

package com.mreblan.cvservice.models.yandexgpt.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class YandexGptCompletionOptions {
    private boolean stream;
    private float   temperature;
    private String  maxTokens;

    // public YandexGptCompletionOptions(boolean stream, float temp, String maxTokens) {
    //     this.stream = stream;
    //     this.temperature = temp;
    //     this.maxTokens = maxTokens;
    // }
}

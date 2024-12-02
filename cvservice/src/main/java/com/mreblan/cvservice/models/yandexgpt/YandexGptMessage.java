package com.mreblan.cvservice.models.yandexgpt;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class YandexGptMessage {
    private String role;
    private String text;
}

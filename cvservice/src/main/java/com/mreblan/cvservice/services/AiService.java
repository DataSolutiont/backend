package com.mreblan.cvservice.services;

import com.mreblan.cvservice.models.CvModel;
import com.mreblan.cvservice.models.yandexgpt.response.YandexGptResponse;

public interface AiService {

    CvModel sendMessage(String content);
}

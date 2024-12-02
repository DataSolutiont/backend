package com.mreblan.cvservice.models.yandexgpt.response;

import java.util.List;

import lombok.Data;

@Data
public class YandexGptResponse {
    private Result result;
    
    @Data
    public static class Result {
        private List<Alternative> alternatives;

        @Data
        public static class Alternative {
          private Message message;

            @Data
            public static class Message {
                String role;
                String text;
            }
        }
    }
}

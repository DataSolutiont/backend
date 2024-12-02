package com.mreblan.cvservice.models.yandexgpt.request;

import java.util.List;

import com.mreblan.cvservice.models.yandexgpt.YandexGptMessage;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class YandexGptRequest {
    private String modelUri;
    private YandexGptCompletionOptions completionOptions;
    private List<YandexGptMessage> messages;

    // @Autowired
    // public void setModelUri(String modelUri) {
    //     this.modelUri = modelUri;
    // }
    //
    // @Autowired
    // public void setCompletionOptions(YandexGptCompletionOptions opts) {
    //     this.completionOptions = opts;
    // }
    //
    // @Autowired
    // public void setMessages(List<YandexGptMessage> msgs) {
    //     this.messages = msgs;
    // }
}

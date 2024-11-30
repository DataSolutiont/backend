package com.mreblan.cvservice.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.mreblan.cvservice.repositories")
public class ElasticsearchConfig extends ElasticsearchConfiguration {
    
    // @Bean
    // public RestClient client() {
    //     return RestClient.builder(new HttpHost("cv-elsearch", 9200, "http")).build();
    // }

    // @Bean
    // public ElasticsearchOperations elasticsearchTemplate() {
    //     return new ElasticsearchRestTemplate(client());
    // }

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                    .connectedTo("cv-elsearch:9200")
                    .build();
    }
}

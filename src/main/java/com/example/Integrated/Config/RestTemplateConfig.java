package com.example.Integrated.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // JSON 메시지 컨버터를 맨 앞에 넣음 (우선순위 높이기)
        restTemplate.getMessageConverters().add(0, new MappingJackson2HttpMessageConverter());

        return restTemplate;
    }
}

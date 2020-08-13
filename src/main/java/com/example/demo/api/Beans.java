package com.example.demo.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Duration;

@Configuration
@Slf4j
public class Beans {

    @Value("${rest.template.max.connections:500}")
    private Integer maxTotal;
    @Value("${rest.template.default.max.per.host:50}")
    private Integer defaultMaxPerRoute;

    private final int HTTP_CONNECT_TIMEOUT = 150000;
    private final int HTTP_READ_TIMEOUT = 150000;


    @Bean
    public RestTemplate restTemplate() {
        return restTemplateBuilder().build();
    }

    @Bean
    @DependsOn(value = {"customRestTemplateCustomizer"})
    public RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder(customRestTemplateCustomizer())
                .setConnectTimeout(Duration.ofMillis(HTTP_CONNECT_TIMEOUT))
                .setReadTimeout(Duration.ofMillis(HTTP_READ_TIMEOUT));
    }

    @Bean
    public CustomRestTemplateCustomizer customRestTemplateCustomizer() {
        return new CustomRestTemplateCustomizer();
    }


    public static class CustomRestTemplateCustomizer implements RestTemplateCustomizer {
        @Override
        public void customize(RestTemplate restTemplate) {
            restTemplate
                    .getInterceptors().add(new CustomClientHttpRequestInterceptor());
        }
    }

    public static class CustomClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

        @Override
        public ClientHttpResponse intercept(
                HttpRequest request, byte[] body,
                ClientHttpRequestExecution execution) throws IOException {

            logRequestDetails(request);
            return execution.execute(request, body);
        }

        private void logRequestDetails(HttpRequest request) {
            log.info("Headers: {}", request.getHeaders());
            log.info("Request Method: {}", request.getMethod());
            log.info("Request URI: {}", request.getURI());
        }
    }


}

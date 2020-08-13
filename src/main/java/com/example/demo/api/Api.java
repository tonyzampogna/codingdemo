package com.example.demo.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class Api {

    private static final String API_URL = "https://api.github.com/users/{username}/repos";

    RestTemplate restTemplate;

    @Autowired
    public Api(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/user/{username}")
    public List<Map<String,Object>> getUser(@PathVariable("username") String username) {
        log.info("getting user: {}", username);
        return callAPI(username).getBody();
    }

    private HttpEntity<List<Map<String,Object>>> callAPI(String username) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(API_URL, HttpMethod.GET, entity, new ParameterizedTypeReference<List<Map<String,Object>>>() {}, username);
    }

}

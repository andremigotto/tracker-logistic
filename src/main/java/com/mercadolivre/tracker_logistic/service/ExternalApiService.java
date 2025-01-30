package com.mercadolivre.tracker_logistic.service;

import com.mercadolivre.tracker_logistic.config.ApiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

@Service
public class ExternalApiService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ApiConfig apiConfig;

    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public boolean checkIfHoliday(LocalDate estimatedDeliveryDate) {
        try {
            String url = apiConfig.getHolidayApiUrl(estimatedDeliveryDate.getYear());
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                    }
            );

            return Optional.ofNullable(response.getBody())
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(holiday -> holiday.get("date"))
                    .filter(Objects::nonNull)
                    .anyMatch(date -> estimatedDeliveryDate.toString().equals(date));
        } catch (Exception e) {
            return false;
        }
    }

    //Ativando Cache na respota da api dogFunFact caso o resultado da chamada seja 'No fun fact avaible'
    @Cacheable(value = "dogFunFact", key = "'latest'", unless = "#result == null || #result.equals('No fun fact available')")
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public String getDogFunFact() {
        try {
            String url = apiConfig.getFunFactApiUrl();
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                    }
            );

            return Optional.ofNullable(response.getBody())
                    .map(body -> (List<?>) body.get("data"))
                    .filter(dataList -> !dataList.isEmpty())
                    .map(dataList -> (Map<?, ?>) dataList.getFirst())
                    .map(firstItem -> (Map<?, ?>) firstItem.get("attributes"))
                    .map(attributes -> attributes.get("body"))
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .orElse("No fun fact available");
        } catch (Exception e) {
            return "No fun fact available";
        }
    }

}

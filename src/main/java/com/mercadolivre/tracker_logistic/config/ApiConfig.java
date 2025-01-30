package com.mercadolivre.tracker_logistic.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiConfig {

    @Value("${api.dateNager.url}")
    private String dateNagerUrl;

    @Value("${api.dogApi.url}")
    private String dogApiUrl;

    public String getHolidayApiUrl(int year) {
        return dateNagerUrl.replace("{year}", String.valueOf(year));
    }

    public String getFunFactApiUrl() {
        return dogApiUrl;
    }
}

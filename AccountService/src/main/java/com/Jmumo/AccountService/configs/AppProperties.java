package com.Jmumo.AccountService.configs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class AppProperties {
    @Value("${app.Base_Url}")
    private String baseUrl;

    public String getBaseUrl() {
        return baseUrl;
    }
}

package com.twitter.gateway.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

import static com.gmail.merikbest2015.constants.FeignConstants.USER_SERVICE;
import static com.gmail.merikbest2015.constants.PathConstants.API_V1_AUTH;

@Configuration
public class ClientConfig {
//    @LoadBalanced
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

}

package com.twitter.gateway;

import com.gmail.merikbest2015.configuration.SharedConfiguration;
import com.gmail.merikbest2015.security.JwtProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@EnableFeignClients
@EnableDiscoveryClient
@Import({JwtProvider.class, SharedConfiguration.class})
@SpringBootApplication(
		scanBasePackages = {"com.twitter.gateway", "com.gmail.merikbest2015"},
		exclude = {DataSourceAutoConfiguration.class})
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

}

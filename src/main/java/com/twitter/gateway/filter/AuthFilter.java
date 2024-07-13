package com.twitter.gateway.filter;


import com.gmail.merikbest2015.dto.response.user.UserPrincipalResponse;
import com.gmail.merikbest2015.security.JwtAuthenticationException;
import com.gmail.merikbest2015.security.JwtProvider;
import com.twitter.gateway.config.ClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static com.gmail.merikbest2015.constants.ErrorMessage.JWT_TOKEN_EXPIRED;
import static com.gmail.merikbest2015.constants.FeignConstants.USER_SERVICE;
import static com.gmail.merikbest2015.constants.PathConstants.*;

@Component
public class AuthFilter extends
        AbstractGatewayFilterFactory<AuthFilter.Config> {
    final Logger logger =
            LoggerFactory.getLogger(AuthFilter.class);
    private final JwtProvider jwtProvider;
    private final WebClient.Builder webClientBuilder;
    @Value("${authUser.url}")
    private String authUserUrl;
    @Autowired
    public AuthFilter(JwtProvider jwtProvider, WebClient.Builder clientBuilder) {
        super(Config.class);
        this.jwtProvider = jwtProvider;
        this.webClientBuilder = clientBuilder;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String token = jwtProvider.resolveToken(exchange.getRequest());
            boolean isValid = jwtProvider.validateToken(token);

            if (!token.isEmpty() && isValid) {
                String email = jwtProvider.parseToken(token);

                UserPrincipalResponse user = webClientBuilder
                        .baseUrl(authUserUrl)
                        .build()
                        .get()
                        .uri(USER_EMAIL, email)
                        .retrieve()
                        .bodyToMono(UserPrincipalResponse.class)
                        .block();

                assert user != null;
                if (Boolean.FALSE.equals(user.getActive())) {
                    throw new JwtAuthenticationException("Email not activated");
                }
                exchange.getRequest()
                        .mutate()
                        .header(AUTH_USER_ID_HEADER, String.valueOf(user.getId()))
                        .build();
                return chain.filter(exchange);
            } else {
                throw new JwtAuthenticationException(JWT_TOKEN_EXPIRED);
            }
        };
    }

    public static class Config {

    }
}

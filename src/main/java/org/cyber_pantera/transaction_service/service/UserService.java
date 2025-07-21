package org.cyber_pantera.transaction_service.service;

import lombok.RequiredArgsConstructor;
import org.cyber_pantera.transaction_service.dto.UserResponse;
import org.cyber_pantera.transaction_service.exception.UserException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class UserService {

    @Value("${service.auth.url}")
    private String authServiceUrl;

    private final WebClient.Builder webClientBuilder;

    public UserResponse validateUser(long userId) {
        return webClientBuilder.build()
                .get().uri(authServiceUrl + "/validate/{userId}", userId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> response.bodyToMono(String.class).map(UserException::new))
                .bodyToMono(UserResponse.class)
                .block();
    }
}

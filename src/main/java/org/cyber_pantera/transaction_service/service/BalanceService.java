package org.cyber_pantera.transaction_service.service;

import lombok.RequiredArgsConstructor;
import org.cyber_pantera.transaction_service.dto.BalanceChangeRequest;
import org.cyber_pantera.transaction_service.dto.BalanceResponse;
import org.cyber_pantera.transaction_service.dto.ChangeType;
import org.cyber_pantera.transaction_service.exception.BalanceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class BalanceService {

    @Value("${service.balance.url}")
    private String balanceServiceUrl;

    private final WebClient.Builder webClientBuilder;

    public CompletableFuture<BalanceResponse> getUserBalance(long userId) {
        return webClientBuilder.build()
                .get().uri(balanceServiceUrl + "/{userId}", userId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> response.bodyToMono(String.class).map(BalanceException::new))
                .bodyToMono(BalanceResponse.class)
                .toFuture();
    }

    public CompletableFuture<Void> changeBalance(BalanceChangeRequest request) {
        return webClientBuilder.build()
                .post().uri(balanceServiceUrl + "/change")
                .body(Mono.just(request), BalanceChangeRequest.class)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> response.bodyToMono(String.class).map(BalanceException::new))
                .bodyToMono(Void.class)
                .toFuture();
    }

    public CompletableFuture<Void> increase(long userId, BigDecimal amount) {
        var request = BalanceChangeRequest.builder()
                .userId(userId)
                .amount(amount)
                .type(ChangeType.INCREASE)
                .build();
        return changeBalance(request);
    }

    public CompletableFuture<Void> decrease(long userId, BigDecimal amount) {
        var request = BalanceChangeRequest.builder()
                .userId(userId)
                .amount(amount)
                .type(ChangeType.DECREASE)
                .build();
        return changeBalance(request);
    }
}

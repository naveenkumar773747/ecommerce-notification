package com.ecommerce.notification.handler;

import com.ecommerce.notification.service.NotificationService;
import com.ecommerce.shared.events.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationHandler {

    @Autowired
    private NotificationService notificationService;

    public Mono<ServerResponse> notifyOrder(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(NotificationEvent.class)
                .doOnNext(event -> log.info("Received order update via WebClient : {},", event.getOrderId()))
                .flatMap(event -> notificationService.sendEmail(event)
                        .doOnNext(order ->
                                log.info("Order placed (Web Client) with id : {} for userId : {}",
                                        event.getOrderId(),
                                        event.getUserId()))
                        .flatMap(saved -> ServerResponse.ok().bodyValue(event))
                );
    }
}

package com.ecommerce.notification.consumer;

import com.ecommerce.notification.service.NotificationService;
import com.ecommerce.shared.events.NotificationEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;

@Service
@Slf4j
public class OrderNotificationListener {

    private final KafkaReceiver<String, String> kafkaReceiver;
    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;


    public OrderNotificationListener(KafkaReceiver<String, String> kafkaReceiver,
                                     ObjectMapper objectMapper,
                                     NotificationService notificationService) {
        this.kafkaReceiver = kafkaReceiver;
        this.objectMapper = objectMapper;
        this.notificationService = notificationService;
    }

    @PostConstruct
    public void listen() {
        kafkaReceiver
                .receive()
                .flatMap(record -> {
                    String message = record.value();

                    try {
                        NotificationEvent event = objectMapper.readValue(message, NotificationEvent.class);
                        log.info("Consumed event : {}", event);

                        Mono<Void> processingMono;

                        switch (event.getStatus()) {
                            case PLACED -> processingMono =
                                    notificationService.sendOrderPlacedNotification(
                                            event
                                    ).then();

                            case CONFIRMED -> processingMono =
                                    notificationService.sendPaymentConfirmedNotification(
                                            event
                                    ).then();

                            case COMPLETED -> processingMono =
                                    notificationService.sendDeliveryCompletedNotification(
                                            event
                                    ).then();

                            case CANCELLED -> processingMono =
                                    notificationService.sendOrderCancelledNotification(
                                            event
                                    ).then();

                            default -> {
                                log.info("Skipping unsupported order status: {}", event.getStatus());
                                record.receiverOffset().acknowledge();
                                return Mono.empty(); // no-op
                            }
                        }

                        return processingMono
                                .doOnSuccess(unused -> record.receiverOffset().acknowledge())
                                .doOnError(error -> log.error("Processing failed: {}", error.getMessage(), error));

                    } catch (Exception e) {
                        log.error("Failed to parse Kafka message : {}", e.getMessage(), e);
                        return Mono.empty();
                    }
                })
                .subscribe();
    }
}

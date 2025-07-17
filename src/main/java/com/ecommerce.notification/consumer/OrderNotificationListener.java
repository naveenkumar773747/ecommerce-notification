package com.ecommerce.notification.consumer;

import com.ecommerce.notification.service.NotificationService;
import com.ecommerce.notification.service.OrderService;
import com.ecommerce.shared.events.OrderPlacedEvent;
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
    private final OrderService orderService;


    public OrderNotificationListener(KafkaReceiver<String, String> kafkaReceiver,
                                     ObjectMapper objectMapper,
                                     NotificationService notificationService,
                                     OrderService orderService) {
        this.kafkaReceiver = kafkaReceiver;
        this.objectMapper = objectMapper;
        this.notificationService = notificationService;
        this.orderService = orderService;
    }

    @PostConstruct
    public void listen() {
        kafkaReceiver
                .receive()
                .flatMap(record -> {
                    try {
                        String message = record.value();
                        OrderPlacedEvent event = objectMapper.readValue(message, OrderPlacedEvent.class);
                        log.info("Consumed event : {}", event);
                        return notificationService.sendOrderConfirmation(
                                        event.getBillingInfo().getBillingEmail(),
                                        "Order Confirmation",
                                        event
                                )
                                .then(orderService.updateOrderStatusEnum(event.getOrderId()))
                                .doOnSuccess(unused -> record.receiverOffset().acknowledge());
                    } catch (Exception e) {
                        log.error("Failed to parse Kafka message : {}", e.getMessage());
                        return Mono.empty();
                    }
                })
                .subscribe();
    }
}

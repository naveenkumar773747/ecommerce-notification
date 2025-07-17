//package com.ecommerce.notification.config;
//
//import com.ecommerce.notification.service.NotificationService;
//import com.ecommerce.shared.events.OrderPlacedEvent;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.annotation.PostConstruct;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import reactor.core.publisher.Flux;
//import reactor.kafka.receiver.KafkaReceiver;
//import reactor.kafka.receiver.ReceiverOptions;
//
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Map;
//
//@Slf4j
//@Configuration
//public class KafkaConsumerConfigListener {
//
//    private final NotificationService notificationService;
//
//
//
//    @Value("${spring.kafka.bootstrap-servers}")
//    private String bootstrapServers;
//
//    @Value("${spring.kafka.consumer.group-id}")
//    private String groupId;
//
//    public KafkaConsumerConfigListener(NotificationService notificationService) {
//        this.notificationService = notificationService;
//    }
//
//    @PostConstruct
//    public void kafkaConsumer() {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//
//        ReceiverOptions<String, String> receiverOptions =
//                ReceiverOptions.<String, String>create(props)
//                        .subscription(Collections.singleton("order_topic"));
//
//        Flux<reactor.kafka.receiver.ReceiverRecord<String, String>> kafkaFlux =
//                KafkaReceiver.create(receiverOptions).receive();
//
//        kafkaFlux
//                .doOnNext(record -> {
//                    try {
//                        OrderPlacedEvent event = new ObjectMapper().readValue(record.value(), OrderPlacedEvent.class);
//                        log.info("Received event: {}", event);
//                        notificationService.sendOrderConfirmation("naveenkumar773747@gmail.com", "Order Confirmation", event);
//                        record.receiverOffset().acknowledge();
//                    } catch (Exception e) {
//                        log.error("Failed to parse event", e);
//                    }
//                })
/// /                .flatMap(obj -> )
//                .subscribe();
//    }
//}

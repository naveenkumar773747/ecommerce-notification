package com.ecommerce.notification.service;

import com.ecommerce.notification.repository.OrderRepository;
import com.ecommerce.shared.enums.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Mono<Void> updateOrderStatus(String orderId) {

        return orderRepository.updateStatusById(orderId, OrderStatus.CONFIRMED)
                .doOnSuccess(any -> log.info("Order Status updated to Confirmed for id : {}", orderId))
                .doOnError(err -> log.error("Error occurred while updating order status for id : {}", orderId));
    }
}


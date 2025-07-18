package com.ecommerce.notification.service;

import com.ecommerce.notification.repository.OrderRepository;
import com.ecommerce.shared.enums.OrderStatusEnum;
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

    public Mono<Void> updateOrderStatusEnum(String orderId, OrderStatusEnum orderStatus) {
        return orderRepository.updateStatusById(orderId, orderStatus)
                .doOnSuccess(any -> log.info("Order Status updated to {} for id : {}", orderStatus, orderId))
                .doOnError(err -> log.error("Error occurred while updating order status to : {} for id : {}", orderStatus, orderId));
    }
}


package com.ecommerce.notification.repository;

import com.ecommerce.shared.enums.OrderStatusEnum;
import com.ecommerce.shared.model.Order;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class OrderRepository {

    private final ReactiveMongoTemplate mongoTemplate;

    public OrderRepository(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Mono<Void> updateStatusById(String orderId, OrderStatusEnum status) {
        Query query = new Query(Criteria.where("_id").is(orderId));
        Update update = new Update().set("status", status);
        return mongoTemplate.updateFirst(query, update, Order.class)
                .then();
    }
}


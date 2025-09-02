package com.ecommerce.notification.router;


import com.ecommerce.notification.handler.NotificationHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class NotificationRouter {

    @Bean
    public RouterFunction<ServerResponse> notificationRoute(NotificationHandler handler) {
        return RouterFunctions.route()
                .POST("/api/notification", handler::notifyOrder)
                .build();
    }
}
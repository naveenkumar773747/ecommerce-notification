package com.ecommerce.notification.service;


import com.ecommerce.shared.events.NotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Slf4j
public class NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    public Mono<Void> sendEmail(NotificationEvent event) {
        log.info("Sending order update mail to {} : {}", event.getBillingName(), event);

        return Mono.fromRunnable(() -> {
                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setTo(event.getBillingEmail());
                    message.setSubject(generateEmailSubject(event));
                    message.setText(generateEmailBody(event));
                    mailSender.send(message);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(unused -> log.info("Email sent successfully for {} event", event.getStatus()))
                .doOnError(error -> log.error("Failed to send email for {} event with error", event.getStatus(), error))
                .then();
    }

    private String generateEmailBody(NotificationEvent event) {

        return switch (event.getStatus()) {
            case PLACED -> String.format("""
                            Dear %s,
                            
                            Your order (%s) has been successfully placed.
                            Total Amount: ₹%.2f
                            
                            
                            Thank you for shopping with us!
                            """,
                    event.getBillingName(),
                    event.getOrderId(),
                    event.getTotalAmount());

            case CONFIRMED -> String.format("""
                            Hi %s,
                            
                            Your payment of ₹%.2f is success and order (%s) has been shipped!
                            
                            
                            - Ecom Team
                            """,
                    event.getBillingName(),
                    event.getTotalAmount(),
                    event.getOrderId());

            case COMPLETED -> String.format("""
                            Hello %s,
                            
                            Good news! Your order (%s) has been delivered.
                            We hope you enjoy it.
                            
                            
                            Please leave us a review!
                            
                            - Support Team
                            """,
                    event.getBillingName(),
                    event.getOrderId());

            case CANCELLED -> String.format("""
                            Dear %s,
                            
                            Unfortunately, your order (%s) has been cancelled.
                            If this was not intentional, please contact support.
                            
                            - Customer Care
                            """,
                    event.getBillingName(),
                    event.getOrderId());

            default -> "Order status not recognized.";
        };
    }

    private String generateEmailSubject(NotificationEvent event) {

        return switch (event.getStatus()) {
            case PLACED -> "E-COMMERCE : Order Placed";

            case CONFIRMED -> "E-COMMERCE : Payment Confirmed";

            case COMPLETED -> "E-COMMERCE : Order Delivered";

            case CANCELLED -> "E-COMMERCE : Order Cancelled";

            default -> "Order status not recognized";
        };
    }

}
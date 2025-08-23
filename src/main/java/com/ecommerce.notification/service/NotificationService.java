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

    public Mono<Void> sendOrderPlacedNotification(NotificationEvent event) {
        return sendEmail(event.getBillingEmail(), "E-COMMERCE : Order Placed", event);
    }

    public Mono<Void> sendPaymentConfirmedNotification(NotificationEvent event) {
        return sendEmail(event.getBillingEmail(), "E-COMMERCE : Payment Confirmed", event);
    }

    public Mono<Void> sendDeliveryCompletedNotification(NotificationEvent event) {
        return sendEmail(event.getBillingEmail(), "E-COMMERCE : Order Delivered", event);
    }

    public Mono<Void> sendOrderCancelledNotification(NotificationEvent event) {
        return sendEmail(event.getBillingEmail(), "E-COMMERCE : Order Cancelled", event);
    }

    private Mono<Void> sendEmail(String to, String subject, NotificationEvent event) {
        log.info("Sending order update mail to {} : {}", event.getBillingName(), event);

        return Mono.fromRunnable(() -> {
                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setTo(to);
                    message.setSubject(subject);
                    message.setText(generateEmailBody(event));
                    mailSender.send(message);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(unused -> log.info("Email sent successfully for {}", subject))
                .doOnError(error -> log.error("Failed to send email for {}", subject, error))
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

}
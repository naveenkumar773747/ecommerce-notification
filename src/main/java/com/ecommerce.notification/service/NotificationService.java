package com.ecommerce.notification.service;

import com.ecommerce.shared.events.OrderEvent;
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

    public Mono<Void> sendOrderPlacedNotification(String to, OrderEvent event) {
        return sendEmail(to, "E-COMMERCE : Order Placed", event);
    }

    public Mono<Void> sendPaymentConfirmedNotification(String to, OrderEvent event) {
        return sendEmail(to, "E-COMMERCE : Payment Confirmed", event);
    }

    public Mono<Void> sendDeliveryCompletedNotification(String to, OrderEvent event) {
        return sendEmail(to, "E-COMMERCE : Order Delivered", event);
    }

    private Mono<Void> sendEmail(String to, String subject, OrderEvent event) {
        log.info("Sending order update mail to {} : {}", event.getBillingInfo().getBillingName(), event);

        return Mono.fromRunnable(() -> {
                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setTo(to);
                    message.setSubject(subject);
                    message.setText(event.toString());
                    mailSender.send(message);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(unused -> log.info("Email sent successfully for {}", subject))
                .doOnError(error -> log.error("Failed to send email for {}", subject, error))
                .then();
    }

}
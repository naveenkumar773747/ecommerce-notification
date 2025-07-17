package com.ecommerce.notification.service;

import com.ecommerce.shared.events.OrderPlacedEvent;
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

    public Mono<Void> sendOrderConfirmation(String to, String subject, OrderPlacedEvent text) {
        log.info("Sending mail to Naveen : {}", text);
        return Mono.fromRunnable(() -> {
                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setTo(to);
                    message.setSubject(subject);
                    message.setText(text.toString());
                    mailSender.send(message);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(unused -> log.info("Email sent successfully"))
                .doOnError(error -> log.error("Failed to send email", error))
                .then();
    }
}
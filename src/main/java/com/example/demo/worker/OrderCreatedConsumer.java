package com.example.demo.workers;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.demo.dto.OrderCreatedEvent;

@Component
public class OrderCreatedConsumer {

    @KafkaListener(topics = "order.created", groupId = "order-workers")
    public void handle(OrderCreatedEvent event) {
        System.out.println("📩 Processing order.created event: " + event);
        // Example: insert into order_notifications table
    }
}

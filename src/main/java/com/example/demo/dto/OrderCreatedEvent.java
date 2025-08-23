package com.example.demo.dto;

public record OrderCreatedEvent(Long orderId, String email, Integer totalPriceDollars) {}


package com.example.demo.controller;

import com.example.demo.OrderService;
import com.example.demo.CreateOrderRequest;
import com.example.demo.OrderResponse;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/orders")

public class OrderController {
  private final OrderService orderService;

  public OrderController(OrderService orderService){
    this.orderService = orderService;
  }

  @PostMapping
  public ResponseEntity<OrderResponse> create(
      @Valid @RequestBody CreateOrderRequest req,
      @RequestHeader(value = "Idempotency-Key", required = false) String key) {
    OrderResponse resp = orderService.createOrder(req, key); // business logic lives here
    return ResponseEntity.status(HttpStatus.CREATED).body(resp);
  }
}

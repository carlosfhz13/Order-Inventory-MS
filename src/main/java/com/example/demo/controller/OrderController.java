package com.example.demo.controller;

import com.example.demo.OrderService;
import com.example.demo.CreateOrderRequest;
import com.example.demo.OrderResponse;
import com.example.demo.dto.OrderDto;
import com.example.demo.dto.ChangeStatusDto;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/orders")

public class OrderController {
  private final OrderService orderService;

  public OrderController(OrderService orderService){
    this.orderService = orderService;
  }
//Orders requested will return id, email, Status, Price, Items
  @GetMapping
  public List<OrderDto> getAllOrders() {
    //Return all orders
    List<OrderDto> allOrders = orderService.getAllOrders();
    return allOrders;
  }
  
  @GetMapping("/{id}")
  public OrderDto getOrderById(@PathVariable Long id) {
    return orderService.getOrderById(id);
  }
  
  @GetMapping("/by-email")
  public List<OrderDto> getOrdersByEmail(@RequestParam String email) {
    return orderService.getOrdersByEmail(email);
  }

  @PostMapping
  public ResponseEntity<OrderResponse> create(
  @Valid @RequestBody CreateOrderRequest req,
  @RequestHeader(value = "Idempotency-Key", required = false) String key) {
    OrderResponse resp = orderService.createOrder(req, key); // business logic lives here
    return ResponseEntity.status(HttpStatus.CREATED).body(resp);
  }

  @PutMapping("/change-status/{id}")
    public ResponseEntity<OrderDto> put(
        @PathVariable Long id,
        @Valid @RequestBody ChangeStatusDto req) {
      OrderDto resp = orderService.changeStatus(id, req);
      return ResponseEntity.ok().body(resp);
    }
}

package com.example.demo.controller;

import com.example.demo.OrderItemRepository;
import com.example.demo.CreateOrderRequest;
import com.example.demo.OrderItemDto;
import com.example.demo.OrderItem;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/order-item")

public class OrderItemController {
  private final OrderItemRepository orderItemRepository;

  public OrderItemController(OrderItemRepository orderItemRepository){
    this.orderItemRepository = orderItemRepository;
  }
//Orders requested will return id, email, Status, Price, Items
  @GetMapping
  public List<OrderItemDto> getAllOrderItems() {
    List<OrderItemDto> allOrderItems = orderItemRepository.findAll().stream().map(OrderItemDto::new).toList();
    return allOrderItems;
  }
}

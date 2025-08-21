package com.example.demo;

import com.example.demo.OrderItemRequest;

import java.util.List;
import java.util.ArrayList;

public class OrderResponse {
    private final Long id;
    private final String status;
    private final Integer totalPriceDollars;
    private final List<OrderItemRequest> items;

    public OrderResponse(Long id, String status, Integer totalPriceDollars, List<OrderItemRequest> items) {
        this.id = id;
        this.status = status;
        this.totalPriceDollars = totalPriceDollars;
        this.items = items;
    }

    public Long getId() { return id; }
    public String getStatus() { return status; }
    public Integer getTotalPriceDollars() { return totalPriceDollars; }
    public List<OrderItemRequest> getItems() { return items; }
}

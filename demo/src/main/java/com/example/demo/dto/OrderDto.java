package com.example.demo.dto;

import com.example.demo.OrderItemDto;
import com.example.demo.OrderItem;
import com.example.demo.Orders;

import java.util.List;
import java.util.ArrayList;

public class OrderDto {
    private final Long id;
    private final String email;
    private final String status;
    private final Integer totalPriceDollars;
    private final List<OrderItemDto> items;

    public OrderDto(Long id, String email, String status, Integer totalPriceDollars, List<OrderItemDto> items) {
        this.id = id;
        this.email = email;
        this.status = status;
        this.totalPriceDollars = totalPriceDollars;
        this.items = items;
    }

    public OrderDto(Orders order){
        this.id = order.getId();
        this.email = order.getCustomer().getEmail();
        this.status = order.getStatus();
        List<OrderItem> items = order.getOrderItem();
        Integer price = 0;
        List<OrderItemDto> items2 = new ArrayList<OrderItemDto>();
        for (OrderItem item : items) {
            Integer sum = item.getQuantity()*(item.getProduct().getPriceCents());
            price = price + sum;
            items2.add(new OrderItemDto(item.getId(), item.getProduct().getSku(), item.getQuantity()));
        }
        price = (int) Math.round(price/100.0);
        this.totalPriceDollars = price;
        this.items = items2;
    } 

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getStatus() { return status; }
    public Integer getTotalPriceDollars() { return totalPriceDollars; }
    public List<OrderItemDto> getItems() { return items; }
}

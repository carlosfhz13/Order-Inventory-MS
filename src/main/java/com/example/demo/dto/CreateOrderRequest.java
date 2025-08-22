package com.example.demo;

import com.example.demo.OrderItemRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;
import java.util.ArrayList;

public class CreateOrderRequest {
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;
    
    @NotEmpty(message = "Order must contain at least one item")
    @Valid // <— validate each item in the list
    private List<OrderItemRequest> items;

    public CreateOrderRequest(String email, List<OrderItemRequest> items) {
        this.email = email;
        this.items = items;
    }

    public CreateOrderRequest(){}

    public void setEmail(String email) { this.email = email; }
    public void setItems(List<OrderItemRequest> items) { this.items = items; }

    public String getEmail() { return email; }
    public List<OrderItemRequest> getItems() { return items; }
}

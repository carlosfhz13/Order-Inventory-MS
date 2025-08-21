package com.example.demo;

import com.example.demo.OrderItem;

import jakarta.validation.constraints.*;

public class OrderItemDto {
    private Long id;

    @NotBlank(message = "SKU is required")
    @Pattern(regexp = "^[A-Za-z0-9-_.]+$", message = "SKU must be alphanumeric/dash/underscore/dot")
    private String sku;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be > 0")
    private Integer quantity;

    public OrderItemDto(Long id, String sku, Integer quantity){
        this.id = id;
        this.sku = sku;
        this.quantity = quantity;
    }
    public OrderItemDto(OrderItem item){
        this.id = item.getId();
        this.sku = item.getProduct().getSku();
        this.quantity = item.getQuantity();
    }

    public String getSku(){
        return sku;
    }
    public Integer getQuantity(){
        return quantity;
    }
    public Long getId(){
        return id;
    }

    public void setSku(String sku){
        this.sku = sku;
    }
    public void setQuantity(Integer quantity){
        this.quantity = quantity;
    }
    public void setId(Long id){
        this.id = id;
    }
}
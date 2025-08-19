package com.example.demo;

import jakarta.validation.constraints.*;

public class OrderItemRequest {
    @NotBlank(message = "SKU is required")
    @Pattern(regexp = "^[A-Za-z0-9-_.]+$", message = "SKU must be alphanumeric/dash/underscore/dot")
    private String sku;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be > 0")
    private Integer quantity;

    public OrderItemRequest(String sku, Integer quantity){
        this.sku = sku;
        this.quantity = quantity;
    }

    public String getSku(){
        return sku;
    }
    public Integer getQuantity(){
        return quantity;
    }

    public void setSku(String sku){
        this.sku = sku;
    }
    public void setQuantity(Integer quantity){
        this.quantity = quantity;
    }
}
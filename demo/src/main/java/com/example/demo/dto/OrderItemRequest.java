package com.example.demo;

import jakarta.validation.constraints.*;

public class OrderItemRequest {
    @NotBlank(message = "SKU is required")
    @Pattern(regexp = "^[A-Za-z0-9-_.]+$", message = "SKU must be alphanumeric/dash/underscore/dot")
    private String sku;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be > 0")
    private Integer quantity;
}
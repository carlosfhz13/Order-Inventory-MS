package com.example.demo;


import jakarta.validation.constraints.*;

public class CreateProductDto {
    
    @NotBlank(message = "Sku is required")
    private String sku;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    private Integer priceCents;
    
    private Integer stock;
    
    public CreateProductDto(String sku, String name, Integer priceCents, Integer stock) {
        this.sku = sku;
        this.name = name;
        this.priceCents = priceCents;
        this.stock = stock;
    }

    public String getSku() { return sku; }
    public String getName() { return name; }
    public Integer getPriceCents() { return priceCents; }
    public Integer getStock() { return stock; }
}

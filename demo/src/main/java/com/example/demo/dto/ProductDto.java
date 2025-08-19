package com.example.demo.dto;

import com.example.demo.Product;

public class ProductDto {
    private final Long id;
    private final String sku;
    private final String name;
    private final Integer priceCents;
    private final Integer stock;

    public ProductDto(Product product) {
        this.id = product.getId();
        this.sku = product.getSku();
        this.name = product.getName();
        this.priceCents = product.getPriceCents();
        this.stock = product.getStock();
    }

    public Long getId() { return id; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public Integer getPriceCents() { return priceCents; }
    public Integer getStock() { return stock; }
}

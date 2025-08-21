package com.example.demo;


import jakarta.validation.constraints.*;

public class ProductUpdateRequestDto {

    private Integer priceCents;
        
    public ProductUpdateRequestDto(Integer priceCents) {
        this.priceCents = priceCents;
    }

    public Integer getPriceCents() { return priceCents; }
}

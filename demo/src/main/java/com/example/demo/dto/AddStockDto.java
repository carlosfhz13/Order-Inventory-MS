package com.example.demo.dto;


import jakarta.validation.constraints.*;

public class AddStockDto {

    private Integer stock;
        
    public AddStockDto(Integer stock) {
        this.stock = stock;
    }

    public Integer getStock() { return stock; }
}

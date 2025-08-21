package com.example.demo.dto;

import com.example.demo.Customer;

public class CusNameChangeDto {
    private final String name;

    public CusNameChangeDto(String name) {
        this.name = name;
    }

    public String getName() { return name; }
}

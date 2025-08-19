package com.example.demo.dto;

import com.example.demo.Customer;

public class CustomerDto {
    private final Long id;
    private final String email;
    private final String name;

    public CustomerDto(Customer customer) {
        this.id = customer.getId();
        this.email = customer.getEmail();
        this.name = customer.getName();
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getName() { return name; }
}

package com.example.demo.dto;


public class ChangeStatusDto {
    private final String status;

    public ChangeStatusDto(String status) {
        this.status = status;
    }

    public String getStatus() { return status; }
}

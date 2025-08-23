package com.example.demo.controller;

import com.example.demo.OrderService;
import com.example.demo.CreateOrderRequest;
import com.example.demo.OrderResponse;
import com.example.demo.dto.OrderDto;
import com.example.demo.dto.ChangeStatusDto;
import com.example.demo.OrderItemDto;
import com.example.demo.OrderItemRequest;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    KafkaTemplate<String, Object> kafkaTemplate; // swallows any send() calls

    @Test
    void createOrder_returns201AndOrderResponse() throws Exception {
        // Arrange: fake response
        OrderResponse fakeResponse = new OrderResponse(
                1L, "CREATED", 20, List.of(new OrderItemRequest("SKU-050", 2))
        );
        Mockito.when(orderService.createOrder(any(CreateOrderRequest.class),any()))
                .thenReturn(fakeResponse);

        // Act & Assert
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "email": "alice@example.com",
                              "items": [
                                {"sku": "hadvjvdk", "quantity": 2}
                              ]
                            }
                            """))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.totalPriceDollars").value(20));
    }

    @Test
    void getAllOrders_returns200AndList() throws Exception {
        // Arrange: fake response list
        OrderDto dto = new OrderDto(
                1L, "alice@example.com", "CREATED", 20, List.of(new OrderItemDto(1L, "SKU1", 10))
        );
        Mockito.when(orderService.getAllOrders()).thenReturn(List.of(dto));

        // Act & Assert
        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].email").value("alice@example.com"))
                .andExpect(jsonPath("$[0].status").value("CREATED"));
    }
}

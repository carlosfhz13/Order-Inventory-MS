package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.ArrayList;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // Optional: add custom queries if needed later, e.g.
    List<OrderItem> findAllByOrder(Orders order);
    List<OrderItem> findByProduct(Product product);
}

package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // Optional: add custom queries if needed later, e.g.
    // List<OrderItem> findByOrder(Orders order);
    // List<OrderItem> findByProduct(Product product);
}

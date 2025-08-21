package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {
	List<Orders> findByCustomerEmail(String email);
	List<Orders> findByCustomer(Customer customer);
}

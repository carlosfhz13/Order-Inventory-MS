package com.example.demo;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "orders")
public class Orders {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
    @JoinColumn(name = "customer_id", nullable = false) // foreign key column in "Customer" table
    private Customer customer;

	@Column(name = "status", nullable = false)
	@NotBlank
	private String status;  // [CREATED, CONFIRMED, CANCELLED]

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private OffsetDateTime createdAt;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true) // points to the field in Order
    private List<OrderItem> orderItem = new ArrayList<>();

	public Orders() {}

	public Orders(Customer customer, String status) {
		this.customer = customer;
		this.status = status;
		this.orderItem = orderItem;
	}

	public Long getId() { return id; }
	public Customer getCustomer() { return customer; }
	public String getStatus() { return status; }
	public OffsetDateTime getCreatedAt() { return createdAt; }
	public List<OrderItem> getOrderItem() { return orderItem; }

	public void setCustomer(Customer customer) { this.customer = customer; }
	public void setStatus(String status) { this.status = status; }
	public void setOrderItem(List<OrderItem> orderItem) { this.orderItem = orderItem; }
	
} 
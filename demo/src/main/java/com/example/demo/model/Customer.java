package com.example.demo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "customers")
public class Customer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "email", nullable = false, unique = true)
	@NotBlank
	private String email;

	@Column(name = "name", nullable = false)
	@NotBlank
	private String name;

	@OneToMany(mappedBy = "customer") // points to the field in Order
    private List<Orders> orders = new ArrayList<>(); // ADD CONSTRAINTS? AND TO OTHER LINKS

	public Customer() {}

	public Customer(String email, String name, List<Orders> orders) {
		this.email = email;
		this.name = name;
		this.orders = orders;
	}

	public Long getId() { return id; }
	public String getEmail() { return email; }
	public String getName() { return name; }
	public List<Orders> getOrders() { return orders; }

	public void setEmail(String email) { this.email = email; }
	public void setName(String name) { this.name = name; }
	public void setOrders(List<Orders> orders) { this.orders = orders; }

} 
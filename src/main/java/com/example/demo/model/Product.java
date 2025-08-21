package com.example.demo;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "products")
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "sku", nullable = false, unique = true)
	@NotBlank
	private String sku;

	@Column(name = "name", nullable = false)
	@NotBlank
	private String name;

	@Column(name = "price_cents", nullable = false)
	@Min(0)
	private Integer priceCents;
	
	@Column(name = "stock", nullable = false)
	@Min(0)
	private Integer stock;
	
	@Version
	private Long version;

	@OneToMany(mappedBy = "product") // points to the field in Order
    private List<OrderItem> orderItem = new ArrayList<>();

	public Product() {}

	public Product(String sku, String name, Integer priceCents, Integer stock) {
		this.sku = sku;
		this.name = name;
		this.priceCents = priceCents;
		this.stock = stock;
	}

	public Product(CreateProductDto productRequest){
		this.sku = productRequest.getSku();
		this.name = productRequest.getName();
		this.priceCents = productRequest.getPriceCents();
		this.stock = productRequest.getStock();
	}

	public Long getId() { return id; }
	public String getSku() { return sku; }
	public String getName() { return name; }
	public Integer getPriceCents() { return priceCents; }
	public Integer getStock() { return stock; }
	public Long getVersion() { return version; }
	public List<OrderItem> getOrderItem() { return orderItem; }

	public void setSku(String sku) { this.sku = sku; }
	public void setName(String name) { this.name = name; }
	public void setPriceCents(Integer priceCents) { this.priceCents = priceCents; }
	public void setStock(Integer stock) { this.stock = stock; }
	public void setOrderItem(List<OrderItem> orderItem) { this.orderItem = orderItem; }

} 
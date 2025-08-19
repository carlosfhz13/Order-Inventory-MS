package com.example.demo;

import jakarta.persistence.*;

@Entity
@Table(
  name = "order_items",
  uniqueConstraints = @UniqueConstraint(columnNames = {"order_id","product_id"})
)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Orders order;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    public OrderItem() {}

    public OrderItem(Orders order, Product product, Integer quantity) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
    }

    // getters
    public Long getId() { return id; }
    public Orders getOrder() { return order; }
    public Product getProduct() { return product; }
    public Integer getQuantity() { return quantity; }

    // setters
    public void setOrder(Orders order) {
        this.order = order;   // no parent-side maintenance
    }

    public void setProduct(Product product) {
        this.product = product; // no parent-side maintenance
    }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}

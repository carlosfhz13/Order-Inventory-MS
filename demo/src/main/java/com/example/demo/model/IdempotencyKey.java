package com.example.demo;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "idempotency_key")
public class IdempotencyKey {

  @Id
  @Column(name = "key", nullable = false, updatable = false)
  private String key;

  @Column(name = "request_hash", nullable = false)
  private String requestHash;

  @Column(name = "order_id")
  private Long orderId;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  // ===== Constructors (ctors) =====

  /** Required by JPA. Keep it protected or public. */
  protected IdempotencyKey() {
  }

  /** Convenience: claim a key for a request body (no order yet). */
  public IdempotencyKey(String key, String requestHash) {
    this.key = key;
    this.requestHash = requestHash;
    // createdAt will be set in @PrePersist if left null
  }

  /** Full constructor (useful in tests). */
  public IdempotencyKey(String key, String requestHash, Long orderId, Instant createdAt) {
    this.key = key;
    this.requestHash = requestHash;
    this.orderId = orderId;
    this.createdAt = createdAt;
  }

  // Ensure createdAt is populated if null
  @PrePersist
  void prePersist() {
    if (this.createdAt == null) {
      this.createdAt = Instant.now();
    }
  }

  // ===== Getters =====

  public String getKey() {
    return key;
  }

  public String getRequestHash() {
    return requestHash;
  }

  public Long getOrderId() {
    return orderId;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  // ===== Setters =====
  // Note: typically you won't change key/createdAt after creation,
  // but they're here since you asked for setters.

  public void setKey(String key) {
    this.key = key;
  }

  public void setRequestHash(String requestHash) {
    this.requestHash = requestHash;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }
}

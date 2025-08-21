package com.example.demo;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class NotEnoughStockException extends RuntimeException {
  private final Long productId;
  private final int requestedQty;
  private final int availableQty;

  public NotEnoughStockException(Long productId, int requestedQty, int availableQty) {
    super("Not enough stock for product %d: requested %d, available %d"
          .formatted(productId, requestedQty, availableQty));
    this.productId = productId;
    this.requestedQty = requestedQty;
    this.availableQty = availableQty;
  }

  public Long getProductId() { return productId; }
  public int getRequestedQty() { return requestedQty; }
  public int getAvailableQty() { return availableQty; }
}

package com.example.demo;

import com.example.demo.ProductRepository;
import com.example.demo.OrdersRepository;
import com.example.demo.OrderItemRepository;
import com.example.demo.CustomerRepository;
import com.example.demo.OrderResponse;
import com.example.demo.OrderItemRequest;
import com.example.demo.Customer;
import com.example.demo.Product;
import com.example.demo.Orders;
import com.example.demo.OrderItem;
import com.example.demo.dto.OrderDto;
import com.example.demo.dto.ChangeStatusDto;

import java.time.Instant;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Optional;




import java.util.List;
import java.util.ArrayList;

@Service
public class OrderService {
  private final ObjectMapper objectMapper = new ObjectMapper();

  private final ProductRepository productRepo;
  private final OrdersRepository orderRepo;
  private final OrderItemRepository itemRepo;
  private final CustomerRepository customerRepo;
  private final IdempotencyKeyRepository idemRepo;

  private String hash(Object obj) {
    try {
      // Convert object → canonical JSON
      String json = objectMapper.writeValueAsString(obj);

      // SHA-256 digest
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hashBytes = digest.digest(json.getBytes(StandardCharsets.UTF_8));

      // Convert to hex string
      StringBuilder sb = new StringBuilder();
      for (byte b : hashBytes) {
        sb.append(String.format("%02x", b));
      }
      return sb.toString();

    } catch (Exception e) {
      throw new RuntimeException("Failed to hash object", e);
    }
  }

  public OrderService(ProductRepository productRepo,
                      OrdersRepository orderRepo,
                      OrderItemRepository itemRepo,
                      CustomerRepository customerRepo,
                      IdempotencyKeyRepository idemRepo) {
    this.productRepo = productRepo;
    this.orderRepo = orderRepo;
    this.itemRepo = itemRepo;
    this.customerRepo = customerRepo;
    this.idemRepo = idemRepo;
  }

  //Orders requested will return id, email, Status, Price, Items
  public List<OrderDto> getAllOrders(){
    //1. Get all Orders from repo
    List<Orders> allOrders = orderRepo.findAll();
    List<OrderDto> returnOrderList = listOrdersToListOrderDto(allOrders);
    return returnOrderList;
  }

  public OrderDto getOrderById(Long id){
    Orders order = orderRepo.findById(id).orElse(null);
    if (order==null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found for id " + id);
    }
    //Find the email
    Customer orderCustomer = order.getCustomer();
    String custyEmail = orderCustomer.getEmail();
    //Find the Status
    String status = order.getStatus();
    //Find the Items
    List<OrderItem> items = itemRepo.findAllByOrder(order);
    //Find the price
    Integer price = 0;
    List<OrderItemDto> items2 = new ArrayList<OrderItemDto>();
    for (OrderItem item : items) {
      Integer sum = item.getQuantity()*(item.getProduct().getPriceCents());
      price = price + sum;
      items2.add(new OrderItemDto(item.getId(), item.getProduct().getSku(), item.getQuantity()));
    }
    price = (int) Math.round(price/100.0);
    //Encapsulate all in OrderDto
    //Append to List
    return new OrderDto(order.getId(), custyEmail, status, price, items2);
  }

  public List<OrderDto> getOrdersByEmail(String email){
    List<Orders> allOrders = orderRepo.findByCustomerEmail(email);
    if (allOrders.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No orders found for email " + email);
    }
    List<OrderDto> returnOrderList = listOrdersToListOrderDto(allOrders);
    return returnOrderList;
  }

  public List<OrderDto> listOrdersToListOrderDto(List<Orders> allOrders){
    List<OrderDto> returnOrderList = new ArrayList<OrderDto>();
    List<OrderItemDto> items2 = new ArrayList<OrderItemDto>();
    //2. For each Order:
    for (Orders theOrder : allOrders) {
      //Find the email
      Customer orderCustomer = theOrder.getCustomer();
      String custyEmail = orderCustomer.getEmail();
      //Find the Status
      String status = theOrder.getStatus();
      //Find the Items
      List<OrderItem> items = itemRepo.findAllByOrder(theOrder);
      //Find the price
      Integer price = 0;
      items2 = new ArrayList<OrderItemDto>();
      for (OrderItem item : items) {
        Integer sum = item.getQuantity()*(item.getProduct().getPriceCents());
        price = price + sum;
        items2.add(new OrderItemDto(item.getId(), item.getProduct().getSku(), item.getQuantity()));
      }
      price = (int) Math.round(price/100.0);
      //Encapsulate all in OrderDto
      //Append to List
      returnOrderList.add(new OrderDto(theOrder.getId(), custyEmail, status, price, items2));
    }
    //3. Return List
    return returnOrderList;
  }

  @Transactional // <- one atomic unit of work
  public OrderResponse createOrder(CreateOrderRequest req,@Nullable String idemKey) {
    // 0) Idempotency check (if you’re adding it)
    String bodyHash = hash(req); // e.g., SHA-256 of canonicalized JSON

    if (idemKey != null) {
      var existing = idemRepo.findById(idemKey).orElse(null);

      if (existing != null) {
        // Same key seen before
        if (!existing.getRequestHash().equals(bodyHash)) {
          throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
            "Idempotency-Key reuse with different request");
        }
        if (existing.getOrderId() != null) {
          // Completed previously → return same order
          var order = orderRepo.findById(existing.getOrderId()).orElseThrow(); // should exist
          List<OrderItemRequest> items = req.getItems();
          Integer priceTotal = 0;
          for (OrderItemRequest item : items){
            String itemSku = item.getSku();
            Integer itemQuantity = item.getQuantity();
            Product p1 = productRepo.findBySku(itemSku).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product SKU not found" + itemSku));
            priceTotal = priceTotal+(p1.getPriceCents()*itemQuantity);
          }
          OrderResponse orderReturn = new OrderResponse(order.getId(), "CREATED", (int) Math.round(priceTotal/100.0), items);
          return orderReturn;
        }
        // Key exists but no order yet → another request is in-flight
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Request in progress");
      }

      // First time we see this key → “claim” it
      idemRepo.save(new IdempotencyKey(idemKey, bodyHash, null, Instant.now()));
    }

    // 1) Find by email
    String email = req.getEmail();
    Customer c1 = customerRepo.findByEmail(email).orElse(null);
    if (c1==null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found for email " + email);
    }
    
    // 2) Load & validate all products, ensure qty > 0, enough stock
    List<OrderItemRequest> items = req.getItems();
    Integer priceTotal = 0;
    for (OrderItemRequest item : items){
      String itemSku = item.getSku();
      Integer itemQuantity = item.getQuantity();
      Product p1 = productRepo.findBySku(itemSku).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product SKU not found" + itemSku));
      if (p1.getStock()<itemQuantity) {
        throw new NotEnoughStockException(p1.getId(), itemQuantity, p1.getStock());
      }
    // 3) Decrement stock for each product (optimistic locking via @Version)
      p1.setStock(p1.getStock()-itemQuantity);
      priceTotal = priceTotal+(p1.getPriceCents()*itemQuantity);
    }
    
    // 4) Save order + order_items
    Orders o1 = new Orders(c1, "CREATED", null);
    orderRepo.save(o1);
    List<OrderItem> itemsForOrder = new ArrayList<OrderItem>();
    for (OrderItemRequest item : items){
      String itemSku = item.getSku();
      Product p2 = productRepo.findBySku(itemSku).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product SKU not found" + itemSku));
      itemsForOrder.add(new OrderItem(o1, p2, item.getQuantity()));
    }
    itemRepo.saveAll(itemsForOrder);
    // 5) Persist idemKey → orderId (optional)
    if (idemKey != null) {
      IdempotencyKey claimed = idemRepo.getReferenceById(idemKey);
      claimed.setOrderId(o1.getId());           // link the key to the created order
      idemRepo.save(claimed);
    }
    // 6) Map to OrderResponse and return
    OrderResponse orderReturn = new OrderResponse(o1.getId(), "CREATED", (int) Math.round(priceTotal/100.0), items);
    return orderReturn;
  }

  @Transactional
  public OrderDto changeStatus(Long id, ChangeStatusDto req){ 
    //1. Find order with Id(Validate existence)
    Optional<Orders> o = orderRepo.findById(id);
    if (o.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found for id " + id);
    }
    Orders o2 = o.get();
    //2. Validate status value
    String status = req.getStatus();
    if (!status.equals("CREATED") && !status.equals("CONFIRMED") && !status.equals("CANCELLED")) {
      throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
        "Invalid status: must be CREATED, CONFIRMED, or CANCELLED");
    }
    //2. Use setter to modify status
    String prevStatus = o2.getStatus();
    o2.setStatus(status);

    //3. Change stock based on status change
    if ((prevStatus.equals("CREATED") || prevStatus.equals("CONFIRMED")) && status.equals("CANCELLED")) {
      //Re-add stock
      List<OrderItem> items = itemRepo.findAllByOrder(o2);
      for (OrderItem item : items){
        Product p2 = item.getProduct();
        p2.setStock(p2.getStock() + item.getQuantity());
      }
    }
    else if ((prevStatus.equals("CANCELLED")) && ( status.equals("CONFIRMED") || status.equals("CREATED"))) {
      //Remove stock or throw error if not enough stock
      List<OrderItem> items = itemRepo.findAllByOrder(o2);
      for (OrderItem item : items){
        Product p2 = item.getProduct();
        if (p2.getStock() - item.getQuantity()<0) {
          throw new NotEnoughStockException(p2.getId(), item.getQuantity(), p2.getStock());
        }
        p2.setStock(p2.getStock() - item.getQuantity());
      }
    }

    //4. Convert order to dto and return
    return new OrderDto(o2);

  }
}

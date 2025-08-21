package com.example.demo;

import com.example.demo.ProductRepository;
import com.example.demo.Product;
import com.example.demo.OrdersRepository;
import com.example.demo.OrderItemRepository;
import com.example.demo.CustomerRepository;
import com.example.demo.CreateProductDto;
import com.example.demo.dto.ProductDto;
import com.example.demo.dto.CustomerDto;
import com.example.demo.dto.AddStockDto;

import java.util.Optional;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class ProductService {

  private final ProductRepository productRepo;
  private final OrdersRepository orderRepo;
  private final OrderItemRepository itemRepo;
  private final CustomerRepository customerRepo;
  private final IdempotencyKeyRepository idemRepo;

  public ProductService(ProductRepository productRepo,
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

  @Transactional // <- one atomic unit of work
  public ProductDto createAProduct(CreateProductDto req) {
    //Use Repository to save Product to table, check if SKU already exists
    String sku = req.getSku();
    Optional<Product> p = productRepo.findBySku(sku);
    if (p.isPresent()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Product with SKU "+sku+" already exists");
    }
    Product p2 = new Product(req);
    productRepo.save(p2);
    return new ProductDto(p2);
  }  

  @Transactional
  public ProductDto put(Long id, ProductUpdateRequestDto req){
    //1. Look for product in table with Id
    Optional<Product> p = productRepo.findById(id);
    if (p.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found for id " + id);
    }
    Product p2 = p.get();
    //3. Update with setters
    p2.setPriceCents(req.getPriceCents());
    //4. Return ProductDto
    return new ProductDto(p2);
  }

  @Transactional
  public ProductDto putStock(Long id, AddStockDto req){
    //1. Look for product in table with Id
    Optional<Product> p = productRepo.findById(id);
    if (p.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found for id " + id);
    }
    Product p2 = p.get();
    //3. Update with setters
    Integer newStock = p2.getStock()+req.getStock();
    p2.setStock(newStock);
    //4. Return ProductDto
    return new ProductDto(p2);
  }

  @Transactional
  public void delete(Long id){
    //1. Find product by Id(Validate existence)
    Product product = productRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    //Check Product is not in an order
    List<OrderItem> orderItems = itemRepo.findByProduct(product);
    if (!orderItems.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Product being sold in an order");
    }
    //2. Delete product
    productRepo.delete(product);
  }

  @Transactional
  public void deleteBySku(String sku){
    //1. Find product by SKU(Validate existence)
    Optional<Product> p = productRepo.findBySku(sku);
    if (p.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Product with SKU "+sku+" could not be found");
    }
    Product p2 = p.get();
    //Check Product is not in an order
    List<OrderItem> orderItems = itemRepo.findByProduct(p2);
    if (!orderItems.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Product being sold in an order");
    }
    //2. Delete product
    productRepo.delete(p2);
  }
}

package com.example.demo;

import com.example.demo.ProductRepository;
import com.example.demo.OrdersRepository;
import com.example.demo.OrderItemRepository;
import com.example.demo.CustomerRepository;
import com.example.demo.CreateCustomer;
import com.example.demo.Customer;
import com.example.demo.dto.CustomerDto;

import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class CustomerService {

  private final ProductRepository productRepo;
  private final OrdersRepository orderRepo;
  private final OrderItemRepository itemRepo;
  private final CustomerRepository customerRepo;
  private final IdempotencyKeyRepository idemRepo;

  public CustomerService(ProductRepository productRepo,
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
  public CustomerDto createACustomer(CreateCustomer req) {
    //Use Repository to save customer to table, check if it already exists
    String email = req.getEmail();
    Optional<Customer> c1 = customerRepo.findByEmail(email);
    if (c1.isPresent()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Customer email already exists");
    }
    Customer c2 = new Customer(email, req.getName().trim());
    customerRepo.save(c2);
    return new CustomerDto(c2);
  }  
}

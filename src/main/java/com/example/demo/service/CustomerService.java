package com.example.demo;

import com.example.demo.ProductRepository;
import com.example.demo.OrdersRepository;
import com.example.demo.OrderItemRepository;
import com.example.demo.CustomerRepository;
import com.example.demo.CreateCustomer;
import com.example.demo.Customer;
import com.example.demo.dto.CustomerDto;
import com.example.demo.dto.CusNameChangeDto;

import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;


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

  @Transactional // <- one atomic unit of work
  public CustomerDto changeName(Long id,CusNameChangeDto req) {
    //1. Find customer(Throw NOTFOUND if not present)
    Optional<Customer> maybeCustomer = customerRepo.findById(id);
    if (maybeCustomer.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found for id " + id);
    }
    //2. Use setter to change name
    Customer customer = maybeCustomer.get(); 
    customer.setName(req.getName());
    //3. Convert to Dto and return
    return new CustomerDto(customer);
  }

  @Transactional
  public void delete(Long id){
    //1. Find Customer by Id(Validate existence)
    Customer customer = customerRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
    //Check customer does not have orders
    List<Orders> ordersForCustomer = orderRepo.findByCustomer(customer);
    if (!ordersForCustomer.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Customer has ongoing order");
    }
    //2. Delete Customer
    customerRepo.delete(customer);
  }

  @Transactional
  public void deleteByEmail(String email){
    //1. Find product by SKU(Validate existence)
    Optional<Customer> c = customerRepo.findByEmail(email);
    if (c.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Customer with email "+email+" could not be found");
    }
    Customer c2 = c.get();
    //Check customer does not have orders
    List<Orders> ordersForCustomer = orderRepo.findByCustomer(c2);
    if (!ordersForCustomer.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Customer has ongoing order");
    }
    //2. Delete product
    customerRepo.delete(c2);
  }
}

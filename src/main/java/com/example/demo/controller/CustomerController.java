package com.example.demo.controller;

import com.example.demo.Customer;
import com.example.demo.CustomerRepository;
import com.example.demo.dto.CustomerDto;
import com.example.demo.CreateCustomer;
import com.example.demo.CustomerService;
import com.example.demo.dto.CusNameChangeDto;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerRepository customerRepository;
    private final CustomerService customerService;

    public CustomerController(CustomerRepository customerRepository, CustomerService customerService) {
        this.customerRepository = customerRepository;
        this.customerService = customerService;
    }

    @GetMapping
    public List<CustomerDto> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(CustomerDto::new)
                .toList();
    }

    @GetMapping("/{id}")
    public CustomerDto getCustomerById(@PathVariable Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
        return new CustomerDto(customer);
    }

    @PostMapping
    public ResponseEntity<CustomerDto> create(
    @Valid @RequestBody CreateCustomer req) {
        CustomerDto resp = customerService.createACustomer(req); // business logic lives here
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @PutMapping("/change-name/{id}")
    public ResponseEntity<CustomerDto> put(
        @PathVariable Long id,
        @Valid @RequestBody CusNameChangeDto req) {
      CustomerDto resp = customerService.changeName(id, req);
      return ResponseEntity.ok().body(resp);
    }

    //Delete by Id
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
      customerService.delete(id);
      return ResponseEntity.noContent().build(); // 204
    }

    //Delete by SKU
    @DeleteMapping("/delete-by-email")
    public ResponseEntity<Void> deleteProduct(@RequestParam String email) {
      customerService.deleteByEmail(email);
      return ResponseEntity.noContent().build(); // 204
    }
}

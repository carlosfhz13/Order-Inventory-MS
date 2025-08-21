package com.example.demo.controller;

import com.example.demo.Product;
import com.example.demo.ProductService;
import com.example.demo.ProductRepository;
import com.example.demo.CreateProductDto;
import com.example.demo.ProductUpdateRequestDto;
import com.example.demo.dto.ProductDto;
import com.example.demo.dto.AddStockDto;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final ProductRepository productRepository;

    public ProductController(ProductService productService, ProductRepository productRepository) {
        this.productService = productService;
        this.productRepository = productRepository;
    }

    @GetMapping
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductDto::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        ProductDto resp = new ProductDto(product);
        return ResponseEntity.ok().eTag("\"" + product.getVersion() + "\"").body(resp);
    }

    @PostMapping
    public ResponseEntity<ProductDto> create(
    @Valid @RequestBody CreateProductDto req) {
        ProductDto resp = productService.createAProduct(req); // business logic lives here
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @PutMapping("/change-price/{id}")
    public ResponseEntity<ProductDto> put(
        @PathVariable Long id,
        @Valid @RequestBody ProductUpdateRequestDto req) {
      ProductDto resp = productService.put(id, req);
      return ResponseEntity.ok().body(resp);
    }

    @PutMapping("/add-stock/{id}")
    public ResponseEntity<ProductDto> put(
        @PathVariable Long id,
        @Valid @RequestBody AddStockDto req) {
      ProductDto resp = productService.putStock(id, req);
      return ResponseEntity.ok().body(resp);
    }
    
    //Delete by Id
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
      productService.delete(id);
      return ResponseEntity.noContent().build(); // 204
    }

    //Delete by SKU
    @DeleteMapping("/delete-by-sku")
    public ResponseEntity<Void> deleteProduct(@RequestParam String sku) {
      productService.deleteBySku(sku);
      return ResponseEntity.noContent().build(); // 204
    }
}

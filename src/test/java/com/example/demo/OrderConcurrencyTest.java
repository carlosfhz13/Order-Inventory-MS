package com.example.demo;

import com.example.demo.dto.OrderDto;
import com.example.demo.dto.ChangeStatusDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

class OrderConcurrencyTest extends AbstractIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrdersRepository orderRepository;

    @Autowired
    private OrderItemRepository itemRepository;

    @Autowired
    private IdempotencyKeyRepository idempotencyKeyRepository;
    

    private Long productId;

    @BeforeEach
    void setup() {
        // reset DB state each test
        itemRepository.deleteAll();
        orderRepository.deleteAll();
        customerRepository.deleteAll();
        productRepository.deleteAll();
        idempotencyKeyRepository.deleteAll();

        Product p = new Product();
        p.setSku("SKU1");
        p.setName("Test Product");
        p.setPriceCents(1000);
        p.setStock(5); // only 5 in stock
        productId = productRepository.save(p).getId();

        customerRepository.save(new Customer("bob@example.com", "Bob"));
    }

    @Test
    void shouldNotOversellWhenMultipleOrdersCompete() throws InterruptedException {
        int threads = 10;

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    CreateOrderRequest req = new CreateOrderRequest();
                    req.setEmail("bob@example.com");
                    req.setItems(List.of(new OrderItemRequest("SKU1", 1)));
                    orderService.createOrder(req,null);
                } catch (Exception ignored) {
                    // some orders will fail when stock runs out
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // reload product
        Product product = productRepository.findById(productId).orElseThrow();

        assertThat(product.getStock()).isGreaterThanOrEqualTo(0);

        // total sold + remaining == 5
        long totalOrdered = orderService.getAllOrders().stream()
                .flatMap(o -> o.getItems().stream())
                .mapToInt(OrderItemDto::getQuantity)
                .sum();

        assertThat(totalOrdered + product.getStock()).isEqualTo(5);
    }

    @Test
    void cancellingOrderRestoresStock() {
        // place order
        CreateOrderRequest req = new CreateOrderRequest();
        req.setEmail("bob@example.com");
        req.setItems(List.of(new OrderItemRequest("SKU1", 2)));
        OrderResponse order = orderService.createOrder(req,null);

        // cancel it
        orderService.changeStatus(order.getId(),new ChangeStatusDto("CANCELLED"));

        Product product = productRepository.findById(productId).orElseThrow();
        assertThat(product.getStock()).isEqualTo(5); // restored
    }
}

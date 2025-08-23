package com.example.demo;

import com.example.demo.dto.OrderDto;
import com.example.demo.dto.OrderCreatedEvent;
import com.example.demo.workers.OrderCreatedConsumer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;

/*@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"order.created"})
class OrderEventIntegrationTest {
    // place order -> assert event consumed
}*/

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"order.created"})
@TestPropertySource(properties = {
  "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
  "spring.kafka.listener.auto-startup=true",

  "spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
  "spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer",
  "spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer",
  "spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.ErrorHandlingDeserializer",
  "spring.kafka.consumer.properties.spring.deserializer.value.delegate.class=org.springframework.kafka.support.serializer.JsonDeserializer",
  "spring.kafka.consumer.properties.spring.json.trusted.packages=com.example.demo.dto",
  "spring.kafka.consumer.properties.spring.json.value.default.type=com.example.demo.dto.OrderCreatedEvent",
  "spring.kafka.consumer.group-id=order-workers-test",
  "spring.kafka.consumer.group-id=order-workers-it",
  "spring.kafka.consumer.auto-offset-reset=earliest"
})
class OrderEventIntegrationTest extends AbstractIntegrationTest {
    // place order -> assert event consumed

    @Autowired
    private KafkaTemplate<String, OrderCreatedEvent> template;

    @SpyBean
    private OrderCreatedConsumer consumer; // your @KafkaListener bean
    

    private Long productId;

    @Test
    void publishesAndConsumerIsInvoked() {
        var event = new OrderCreatedEvent(123L, "alice@example.com", 20);
        template.send("order.created", String.valueOf(event.orderId()), event);

        // wait up to 5s for the async listener to be invoked
        Mockito.verify(consumer, Mockito.timeout(5000))
               .handle(Mockito.argThat(e -> e.orderId().equals(123L)));
    }
}

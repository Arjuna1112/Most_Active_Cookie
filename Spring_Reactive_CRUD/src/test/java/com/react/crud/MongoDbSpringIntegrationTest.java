package com.react.crud;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import com.react.crud.entity.Product;
import com.react.crud.repository.ProductRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataMongoTest
@ExtendWith(SpringExtension.class)
public class MongoDbSpringIntegrationTest {

    @Autowired
    private ProductRepository repository;

    @Test
    public void validateDbOperation() {
        repository.save(new Product("102", "mobile", 1, 10000)).block();
        Flux<Product> productFlux = repository.findAll();
        StepVerifier
                .create(productFlux)
                .assertNext(product -> {
                    System.out.println(product);
                    assertEquals("mobile", product.getName());
                    assertEquals(Double.valueOf(10000) , product.getPrice());
                    assertNotNull(product.getId());
                })
                .expectComplete()
                .verify();
        Mono<Product> productMono = repository.findById("102");
        StepVerifier
                .create(productMono)
                .assertNext(product -> {
                    System.out.println(product);
                    assertEquals("mobile", product.getName());
                    assertEquals(Double.valueOf(10000) , product.getPrice());
                    assertNotNull(product.getId());
                })
                .expectComplete()
                .verify();
    }
}


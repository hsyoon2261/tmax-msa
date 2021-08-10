package com.example.orderservice.jpa;
import org.springframework.data.repository.CrudRepository;

public interface OrdersRepository extends CrudRepository<OrderEntity, Long> {
    OrderEntity findByOrderId(String productId);
    Iterable<OrderEntity> findByUserId(String userId);
}


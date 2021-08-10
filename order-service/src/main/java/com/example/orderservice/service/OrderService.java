package com.example.orderservice.service;


import com.example.orderservice.jpa.OrderEntity;

public interface OrderService {
    Iterable<OrderEntity> getAllOrders();
}

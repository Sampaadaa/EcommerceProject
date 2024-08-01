package com.example.Ecommerce.services;

import com.example.Ecommerce.dto.OrderDto;
import com.example.Ecommerce.dto.OrderResponse;

import java.util.List;

public class OrderServiceImpl implements OrderService {


    @Override
    public OrderDto placeOrder(String emailId, Long cartId, String paymentMethod) {
        return null;
    }

    @Override
    public OrderDto getOrder(String emailId, Long orderId) {
        return null;
    }

    @Override
    public List<OrderDto> getOrdersByUser(String emailId) {
        return List.of();
    }

    @Override
    public OrderResponse getAllOrders(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        return null;
    }

    @Override
    public OrderDto updateOrder(String emailId, Long orderId, String orderStatus) {
        return null;
    }
}

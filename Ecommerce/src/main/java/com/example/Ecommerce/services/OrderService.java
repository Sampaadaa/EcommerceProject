package com.example.Ecommerce.services;

import com.example.Ecommerce.dto.OrderDto;
import com.example.Ecommerce.dto.OrderResponse;

import java.util.List;

public interface OrderService {

    OrderDto placeOrder(String emailId, Long cartId, String paymentMethod, Long productId, int quantity);

    OrderDto getOrder(String emailId, Long orderId);

    List<OrderDto> getOrdersByUser(String emailId);

    OrderResponse getAllOrders(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    OrderDto updateOrder(String emailId, Long orderId, String orderStatus);
}

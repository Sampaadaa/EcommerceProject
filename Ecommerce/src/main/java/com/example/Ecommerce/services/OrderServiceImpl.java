package com.example.Ecommerce.services;
import com.example.Ecommerce.Repository.*;
import com.example.Ecommerce.dto.OrderDto;
import com.example.Ecommerce.dto.OrderResponse;
import com.example.Ecommerce.exceptions.APIException;
import com.example.Ecommerce.exceptions.ResourceNotFoundException;
import com.example.Ecommerce.models.*;
import com.example.Ecommerce.services.OrderService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
@Transactional

public class OrderServiceImpl implements OrderService{

    @Autowired
    public UserRepository userRepo;

//    @Autowired
//    public CartItemRepository cartRepo;

    @Autowired
    public OrderRepository orderRepo;

//    @Autowired
//    private PaymentRepository paymentRepo;

    @Autowired
    public OrderItemRepository orderItemRepo;

//    @Autowired
//    public CartItemRepository cartItemRepo;

    @Autowired
    public UserService userService;

//    @Autowired
//    public CartService cartService;

    @Autowired
    public ModelMapper modelMapper;


    @Override
    public OrderDto placeOrder(String emailId, Long cartId, String paymentMethod) {



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
package com.example.Ecommerce.controller;


import com.example.Ecommerce.config.AppConstant;
import com.example.Ecommerce.dto.OrderDto;
import com.example.Ecommerce.dto.OrderResponse;
import com.example.Ecommerce.models.Order;
import com.example.Ecommerce.services.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "E-Commerce Application Project")


public class OrderController {

    @Autowired
    public OrderService orderService;

    // Endpoint to place an order from the cart
    @PostMapping("/public/users/{emailId}/carts/{cartId}/payments/{paymentMethod}/order")
    public ResponseEntity<OrderDto> orderFromCart(@PathVariable String emailId,
                                                  @PathVariable Long cartId,
                                                  @PathVariable String paymentMethod)
    {

        // Call the service method to place the order from the cart
        OrderDto order = orderService.placeOrder(emailId, cartId, paymentMethod, null, 0);

        // Return the response with the created order
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    // Endpoint to place a direct "Buy Now" order
    @PostMapping("/public/users/{emailId}/products/{productId}/payments/{paymentMethod}/order")
    public ResponseEntity<OrderDto> orderDirectly(
            @PathVariable String emailId,
            @PathVariable Long productId,
            @PathVariable String paymentMethod,
            @RequestParam int quantity) {

        // Call the service method to place the direct order
        OrderDto order = orderService.placeOrder(emailId, null, paymentMethod, productId, quantity);

        // Return the response with the created order
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @GetMapping("/admin/orders")
    public ResponseEntity<OrderResponse> getAllOrders(
            @RequestParam(name = "pageNumber", defaultValue = AppConstant.pageNumber) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstant.pageSize) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstant.SORT_PRODUCTS_BY) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstant.SORT_DIR) String sortOrder) {

        // Call the service to get all orders with pagination and sorting
        OrderResponse orderResponse = orderService.getAllOrders(pageNumber, pageSize, sortBy, sortOrder);

        // Return the order response with an HTTP status of FOUND (302)
        return new ResponseEntity<OrderResponse>(orderResponse, HttpStatus.FOUND);
    }


    @GetMapping("public/users/{emailId}/orders")
    public ResponseEntity<List<OrderDto>> getOrdersByUser(@PathVariable String emailId) {
        List<OrderDto> orders = orderService.getOrdersByUser(emailId);

        return new ResponseEntity<List<OrderDto>>(orders, HttpStatus.FOUND);
    }

    @GetMapping("public/users/{emailId}/orders/{orderId}")
    public ResponseEntity<OrderDto> getOrderByUser(@PathVariable String emailId, @PathVariable Long orderId) {
        // Call the service to get the specific order by the user's email ID and order ID
        OrderDto order = orderService.getOrder(emailId, orderId);

        // Return the order with an HTTP status of FOUND (302)
        return new ResponseEntity<OrderDto>(order, HttpStatus.FOUND);
    }


    @PutMapping("admin/users/{emailId}/orders/{orderId}/orderStatus/{orderStatus}")
    public ResponseEntity<OrderDto> updateOrderByUser(@PathVariable String emailId, @PathVariable Long orderId, @PathVariable String orderStatus) {
        // Call the service to update the order status for the specified user's order
        OrderDto order = orderService.updateOrder(emailId, orderId, orderStatus);

        // Return the updated order with an HTTP status of OK (200)
        return new ResponseEntity<OrderDto>(order, HttpStatus.OK);
    }



}

package com.example.Ecommerce.services;
import com.example.Ecommerce.Repository.*;
import com.example.Ecommerce.dto.OrderDto;
import com.example.Ecommerce.dto.OrderItemDto;
import com.example.Ecommerce.dto.OrderResponse;
import com.example.Ecommerce.exceptions.APIException;
import com.example.Ecommerce.exceptions.ResourceNotFoundException;
import com.example.Ecommerce.models.*;
import com.example.Ecommerce.services.OrderService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional

public class OrderServiceImpl implements OrderService {

    @Autowired
    public UserRepository userRepo;

    @Autowired
    public CartRepository cartRepository;

    @Autowired
    public OrderRepository orderRepository;

    @Autowired
    public ProductRepository productRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    public OrderItemRepository orderItemRepository;

    @Autowired
    public CartItemRepository cartItemRepository;

    @Autowired
    public UserService userService;

//    @Autowired
//    public CartService cartService;

    @Autowired
    public ModelMapper modelMapper;


    @Override
    public OrderDto placeOrder(String emailId, Long cartId, String paymentMethod, Long productId, int quantity) {

        //create a new orders and set initial values
        Order order = new Order();
        order.setEmail(emailId);
        order.setOrderDate(LocalDate.now());
        order.setOrderStatus("Order Accepted!");

        List<OrderItem> orderItems = new ArrayList<>();
        double totalAmount = 0;

        //if cart id is provided process from cart
        if (cartId != null) {
            // Process order from cart
            Cart cart = cartRepository.findCartByEmailAndCartId(emailId, cartId);
            if (cart == null || cart.getCartItems().isEmpty()) {
                throw new APIException("Cart is empty or not found");
            }

            totalAmount = cart.getTotalPrice();

            List<CartItem> cartItems = cart.getCartItems();
            for (CartItem cartItem : cartItems) {
                OrderItem orderItem = createOrderItem(cartItem.getProduct(), cartItem.getQuantity(), cartItem.getProductPrice(), order);
                orderItems.add(orderItem);
            }

//             Clear cart after order is placed
//            cart.getCartItems().forEach(item -> {
//                cartService.deleteProductFromCart(cartId, item.getProduct().getProductId());
//                item.getProduct().setQuantity(item.getProduct().getQuantity() - item.getQuantity());
//            });

        } else if (productId != null && quantity > 0) {
            // Process direct "Buy Now" order
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
            if (product.getQuantity() < quantity) {
                throw new APIException("Insufficient stock for the product");
            }

            double productPrice = product.getPrice();
            totalAmount = productPrice * quantity;

            OrderItem orderItem = createOrderItem(product, quantity, productPrice, order);
            orderItems.add(orderItem);

            // Reduce product quantity
            product.setQuantity(product.getQuantity() - quantity);

        } else {
            throw new APIException("Invalid order request");
        }

        order.setTotalAmount(totalAmount);

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod(paymentMethod);
        payment = paymentRepository.save(payment);
        order.setPayment(payment);

        Order savedOrder = orderRepository.save(order);
        orderItems = orderItemRepository.saveAll(orderItems);

        OrderDto   orderDTO = modelMapper.map(savedOrder, OrderDto.class);
        orderItems.forEach(item -> orderDTO.getOrderItems().add(modelMapper.map(item, OrderItemDto.class)));

        return orderDTO;
    }

    private OrderItem createOrderItem(Product product, int quantity, double productPrice, Order order) {
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setQuantity(quantity);
        orderItem.setOrderedProductPrice(productPrice);
        orderItem.setOrder(order);
        return orderItem;
    }

    @Override
    public List<OrderDto> getOrdersByUser(String emailId) {
        // Fetch all orders associated with the given email ID from the order repository
        List<Order> orders = orderRepository.findAllByEmail(emailId);

        // Convert the list of Order entities into a list of OrderDTOs using ModelMapper
        List<OrderDto> orderDtos = orders.stream()
                .map(order -> modelMapper.map(order, OrderDto.class))
                .collect(Collectors.toList());

        // If no orders are found for the given email, throw an exception
        if (orderDtos.isEmpty()) {
            throw new APIException("No orders placed yet by the user with email: " + emailId);
        }

        // Return the list of OrderDTOs
        return orderDtos;
    }



    @Override
    public OrderDto getOrder(String emailId, Long orderId) {
        // Retrieve the order based on the provided email and order ID
        Order order = orderRepository.findOrderByEmailAndOrderId(emailId, orderId);

        // If no order is found, throw an exception indicating the resource was not found
        if (order == null) {
            throw new ResourceNotFoundException("Order", "orderId", orderId);
        }

        // Map the Order entity to an OrderDTO and return it
        return modelMapper.map(order, OrderDto.class);
    }





    @Override
    public OrderResponse getAllOrders(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        // Determine the sort order (ascending or descending) based on the provided sortOrder parameter
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        // Create a Pageable object that includes page number, page size, and sorting details
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        // Retrieve a page of orders from the repository based on the provided paging details
        Page<Order> pageOrders = orderRepository.findAll(pageDetails);

        // Get the list of orders from the retrieved page content
        List<Order> orders = pageOrders.getContent();

        // Convert the list of Order entities to a list of OrderDTOs
        List<OrderDto> orderDTOs = orders.stream().map(order -> modelMapper.map(order, OrderDto.class))
                .collect(Collectors.toList());

        // If there are no orders found, throw an APIException
        if (orderDTOs.isEmpty()) {
            throw new APIException("No orders placed yet by the users");
        }

        // Create a response object to hold the order data and paging details
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setContent(orderDTOs);
        orderResponse.setPageNumber(pageOrders.getNumber());
        orderResponse.setPageSize(pageOrders.getSize());
        orderResponse.setTotalElements(pageOrders.getTotalElements());
        orderResponse.setTotalPages(pageOrders.getTotalPages());
        orderResponse.setLastPage(pageOrders.isLast());

        return orderResponse;
    }


    @Override
    public OrderDto updateOrder(String emailId, Long orderId, String orderStatus) {
        // Retrieve the order based on the provided email and order ID
        Order order = orderRepository.findOrderByEmailAndOrderId(emailId, orderId);

        // If the order is not found, throw a ResourceNotFoundException
        if (order == null) {
            throw new ResourceNotFoundException("Order", "orderId", orderId);
        }

        // Update the order status with the provided status
        order.setOrderStatus(orderStatus);

        // Convert the updated Order entity to an OrderDTO and return it
        return modelMapper.map(order, OrderDto.class);
    }

}
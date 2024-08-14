package com.example.Ecommerce.controller;


import com.example.Ecommerce.dto.CartDto;
import com.example.Ecommerce.services.CartService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "E-Commerce Application")
public class CartController {

    // Injecting CartService to handle the business logic related to cart operations
    @Autowired
    private CartService cartService;

    // Endpoint to add a product to a cart with a specified quantity
    @PostMapping("/public/carts/{cartId}/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDto> addProductToCart(@PathVariable Long cartId,
                                                    @PathVariable Long productId,
                                                    @PathVariable Integer quantity) {
        // Call the service method to add a product to the cart and return the updated cart details
        CartDto cartDTO = cartService.addProductToCart(cartId, productId, quantity);

        // Return the updated cart details with HTTP status code 201 (CREATED)
        return new ResponseEntity<CartDto>(cartDTO, HttpStatus.CREATED);
    }

    // Endpoint to retrieve all carts (restricted to admin users)
    @GetMapping("/admin/carts")
    public ResponseEntity<List<CartDto>> getCarts() {
        // Call the service method to retrieve all carts
        List<CartDto> cartDTOs = cartService.getAllCarts();

        // Return the list of all carts with HTTP status code 302 (FOUND)
        return new ResponseEntity<List<CartDto>>(cartDTOs, HttpStatus.FOUND);
    }

    // Endpoint to retrieve a specific cart by emailId and cartId
    @GetMapping("/public/users/{emailId}/carts/{cartId}")
    public ResponseEntity<CartDto> getCartById(@PathVariable String emailId,
                                               @PathVariable Long cartId) {

        // Call the service method to retrieve a specific cart by emailId and cartId
        CartDto cartDTO = cartService.getCart(emailId, cartId);

        // Return the cart details with HTTP status code 302 (FOUND)
        return new ResponseEntity<CartDto>(cartDTO, HttpStatus.FOUND);
    }

    // Endpoint to update the quantity of a product in a specific cart
    @PutMapping("/public/carts/{cartId}/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDto> updateCartProduct(@PathVariable Long cartId,
                                                     @PathVariable Long productId,
                                                     @PathVariable Integer quantity) {
        // Call the service method to update the quantity of a product in the cart
        CartDto cartDTO = cartService.updateProductQuantityInCart(cartId, productId, quantity);

        // Return the updated cart details with HTTP status code 200 (OK)
        return new ResponseEntity<CartDto>(cartDTO, HttpStatus.OK);
    }

    // Endpoint to delete a product from a specific cart
    @DeleteMapping("/public/carts/{cartId}/product/{productId}")
    public ResponseEntity<String> deleteProductFromCart(@PathVariable Long cartId,
                                                        @PathVariable Long productId) {
        // Call the service method to delete a product from the cart
        String status = cartService.deleteProductFromCart(cartId, productId);

        // Return the status message with HTTP status code 200 (OK)
        return new ResponseEntity<String>(status, HttpStatus.OK);
    }
}


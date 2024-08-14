package com.example.Ecommerce.services;

import com.example.Ecommerce.dto.CartDto;

import java.util.List;

public interface CartService {


    // Adds a product to a cart the method takes the cart ID, product ID, and quantity as inputs.
    CartDto addProductToCart(Long cartId, Long productId, Integer quantity);

    // Retrieves all carts in the system.
    List<CartDto> getAllCarts();

    // Retrieves a specific cart by its ID and the user's email ID.
    CartDto getCart(String emailId, Long cartId);

    // Updates the quantity of a specific product in a cart
    CartDto updateProductQuantityInCart(Long cartId, Long productId, Integer quantity);

    // Updates product information in multiple carts (e.g., after a product update in the catalog)
    void updateProductInCarts(Long cartId, Long productId);

    // Deletes a specific product from a cart by cart ID and product ID
    String deleteProductFromCart(Long cartId, Long productId);

}

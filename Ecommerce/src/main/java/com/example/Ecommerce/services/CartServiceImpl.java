package com.example.Ecommerce.services;

import com.example.Ecommerce.Repository.CartItemRepository;
import com.example.Ecommerce.Repository.CartRepository;
import com.example.Ecommerce.Repository.ProductRepository;
import com.example.Ecommerce.dto.CartDto;
import com.example.Ecommerce.dto.ProductDto;
import com.example.Ecommerce.exceptions.APIException;
import com.example.Ecommerce.exceptions.ResourceNotFoundException;
import com.example.Ecommerce.models.Cart;
import com.example.Ecommerce.models.CartItem;
import com.example.Ecommerce.models.Product;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Transactional
@Service


public class CartServiceImpl implements CartService{

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public CartDto addProductToCart(Long cartId, Long productId, Integer quantity) {

        // Retrieve the cart by ID or throw an exception if not found
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        // Retrieve the product by ID or throw an exception if not found
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        // Check if the product is already in the cart
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);

        // If the product is already in the cart, throw an exception
        if (cartItem != null) {
            throw new APIException("Product " + product.getProductName() + " already exists in the cart");
        }

        // If the product is out of stock, throw an exception
        if (product.getQuantity() == 0) {
            throw new APIException(product.getProductName() + " is not available");
        }

        // If the requested quantity exceeds available stock, throw an exception
        if (product.getQuantity() < quantity) {
            throw new APIException("Please, make an order of the " + product.getProductName()
                    + " less than or equal to the quantity " + product.getQuantity() + ".");
        }

        // Create a new CartItem
        CartItem newCartItem = new CartItem();
        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setProductPrice(product.getPrice());

        // Save the new CartItem
        cartItemRepository.save(newCartItem);

        // Update the product's stock quantity
        product.setQuantity(product.getQuantity() - quantity);

        // Update the cart's total price
        cart.setTotalPrice(cart.getTotalPrice() + (product.getPrice() * quantity));

        // Map the cart to a CartDTO
        CartDto cartDTO = modelMapper.map(cart, CartDto.class);

        // Map the products in the cart to ProductDTOs
        List<ProductDto> productDtos = cart.getCartItems().stream()
                .map(p -> modelMapper.map(p.getProduct(), ProductDto.class)).collect(Collectors.toList());

        cartDTO.setProducts(productDtos);

        return cartDTO;
    }



    @Override
    public List<CartDto> getAllCarts() {
        // Retrieve all Cart entities from the database using the Cart repository
        List<Cart> carts = cartRepository.findAll();

        // Check if the list of carts is empty; if so, throw an exception
        if (carts.isEmpty()) {
            throw new APIException("No cart exists");
        }

        // Convert the list of Cart entities to a list of CartDTOs using Java Streams
        List<CartDto> cartDtos = carts.stream().map(cart -> {
            // Map the Cart entity to a CartDTO object using ModelMapper
            CartDto cartDto = modelMapper.map(cart, CartDto.class);

            // Convert each CartItem in the Cart to a ProductDTO object
            List<ProductDto> products = cart.getCartItems().stream()
                    // Map each Product in the CartItem to a ProductDTO using ModelMapper
                    .map(p -> modelMapper.map(p.getProduct(), ProductDto.class))
                    // Collect the mapped ProductDTOs into a list
                    .collect(Collectors.toList());

            // Set the list of ProductDTOs into the CartDTO
            cartDto.setProducts(products);

            // Return the mapped CartDTO for this Cart
            return cartDto;

            // Collect the list of CartDTOs into a list and return it
        }).collect(Collectors.toList());

        // Return the final list of CartDTOs
        return cartDtos;
    }


    @Override
    public CartDto getCart(String emailId, Long cartId) {
        // Retrieve the Cart entity by emailId and cartId from the database using the Cart repository
        Cart cart = cartRepository.findCartByEmailAndCartId(emailId, cartId);

        // Check if the cart is null (i.e., not found); if so, throw a ResourceNotFoundException
        if (cart == null) {
            throw new ResourceNotFoundException("Cart", "cartId", cartId);
        }

        // Map the Cart entity to a CartDTO object using ModelMapper
        CartDto cartDTO = modelMapper.map(cart, CartDto.class);

        // Convert each CartItem in the Cart to a ProductDTO object
        List<ProductDto> products = cart.getCartItems().stream()
                // Map each Product in the CartItem to a ProductDTO using ModelMapper
                .map(p -> modelMapper.map(p.getProduct(), ProductDto.class))
                // Collect the mapped ProductDTOs into a list
                .collect(Collectors.toList());

        // Set the list of ProductDTOs into the CartDTO
        cartDTO.setProducts(products);

        // Return the final CartDTO object
        return cartDTO;
    }

    @Override
    public CartDto updateProductQuantityInCart(Long cartId, Long productId, Integer quantity) {
        // Retrieve the Cart entity by cartId from the database
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        // Retrieve the Product entity by productId from the database
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        // Check if the product is out of stock
        if (product.getQuantity() == 0) {
            throw new APIException(product.getProductName() + " is not available");
        }

        // Check if the requested quantity exceeds the available product quantity
        if (product.getQuantity() < quantity) {
            throw new APIException("Please, make an order of the " + product.getProductName()
                    + " less than or equal to the quantity " + product.getQuantity() + ".");
        }

        // Find the CartItem by productId and cartId from the CartItem repository
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);

        // If the CartItem is not found, throw an APIException indicating the product is not in the cart
        if (cartItem == null) {
            throw new APIException("Product " + product.getProductName() + " not available in the cart!!!");
        }

        // Calculate the current total price of the cart without the price of the CartItem being updated
        double cartPrice = cart.getTotalPrice() - (cartItem.getProduct().getPrice() * cartItem.getQuantity());

        // Update the product quantity (increase available stock by the old quantity, then subtract the new quantity)
        product.setQuantity(product.getQuantity() + cartItem.getQuantity() - quantity);

        // Update the CartItem with the new quantity
        cartItem.setQuantity(quantity);

        // Recalculate the total price of the cart with the updated CartItem quantity and price
        cart.setTotalPrice(cartPrice + (product.getPrice() * quantity));

        // Save the updated CartItem back to the database
        cartItem = cartItemRepository.save(cartItem);

        // Map the Cart entity to CartDTO for returning the response
        CartDto cartDTO = modelMapper.map(cart, CartDto.class);

        // Convert each CartItem's Product to ProductDTO and add it to the CartDTO's products list
        List<ProductDto> productDTOs = cart.getCartItems().stream()
                .map(p -> modelMapper.map(p.getProduct(), ProductDto.class)).collect(Collectors.toList());

        cartDTO.setProducts(productDTOs);

        // Return the updated CartDTO
        return cartDTO;
    }


    @Override
    public void updateProductInCarts(Long cartId, Long productId) {
        // Retrieve the Cart entity by cartId from the database
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        // Retrieve the Product entity by productId from the database
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        // Find the CartItem by productId and cartId from the CartItem repository
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);

        // If the CartItem is not found, throw an APIException indicating the product is not in the cart
        if (cartItem == null) {
            throw new APIException("Product " + product.getProductName() + " not available in the cart!!!");
        }

        // Calculate the current total price of the cart without the price of the CartItem being updated
        double cartPrice = cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity());

        // Update the price of the CartItem to the new product's regular price
        cartItem.setProductPrice(product.getPrice());

        // Recalculate the total price of the cart with the updated CartItem price
        cart.setTotalPrice(cartPrice + (cartItem.getProductPrice() * cartItem.getQuantity()));

        // Save the updated CartItem back to the database
        cartItemRepository.save(cartItem);
    }


    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        // Retrieve the Cart entity by cartId from the database
        Cart cart = cartRepository.findById(cartId)
                // If the cart is not found, throw a ResourceNotFoundException
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        // Find the CartItem by productId and cartId from the CartItem repository
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);

        // If the CartItem is not found, throw a ResourceNotFoundException indicating the product is not in the cart
        if (cartItem == null) {
            throw new ResourceNotFoundException("Product", "productId", productId);
        }

        // Decrease the total price of the cart by subtracting the price of the CartItem being removed
        cart.setTotalPrice(cart.getTotalPrice() - (cartItem.getProduct().getPrice() * cartItem.getQuantity()));

        // Get the Product entity from the CartItem
        Product product = cartItem.getProduct();

        // Increase the product quantity in the inventory by the quantity that was in the cart
        product.setQuantity(product.getQuantity() + cartItem.getQuantity());

        // Delete the CartItem from the database using the cartId and productId
        cartItemRepository.deleteCartItemByProductIdAndCartId(cartId, productId);

        // Return a message indicating the product has been removed from the cart
        return "Product " + cartItem.getProduct().getProductName() + " removed from the cart !!!";
    }

}

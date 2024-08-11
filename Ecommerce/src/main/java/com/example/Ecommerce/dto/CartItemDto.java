package com.example.Ecommerce.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {

    private Long cartItemId;
    private CartDto cart;
    private ProductDto product;
    private Integer quantity;

    private double productPrice;


}

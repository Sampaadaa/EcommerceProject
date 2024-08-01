package com.example.Ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class OrderItemDto {

    private Long orderItemId;
    private ProductDto product;
    private Integer quantity;
    private double orderedProductPrice;
}


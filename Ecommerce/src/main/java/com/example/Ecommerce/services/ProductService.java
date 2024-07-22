package com.example.Ecommerce.services;

import com.example.Ecommerce.dto.CategoryDto;
import com.example.Ecommerce.dto.CategoryResponse;
import com.example.Ecommerce.dto.ProductDto;
import com.example.Ecommerce.dto.ProductResponse;
import com.example.Ecommerce.models.Category;
import com.example.Ecommerce.models.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {
    ProductDto addProduct( Long categoryId, Product product);

    ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ProductDto updateProduct(Long productId, Product product);

    ProductDto updateProductImage(Long productId, MultipartFile image) throws IOException;


    String deleteProduct(Long productId);


    ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);



    ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy,
                                     String sortOrder);








}

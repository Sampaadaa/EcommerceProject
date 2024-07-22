package com.example.Ecommerce.Repository;

import com.example.Ecommerce.models.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ProductRepository extends JpaRepository<Product, Long>  {

    //search for Product entities whose productName contains a specified keyword

    Page<Product> findByProductNameLike(String keyword, Pageable pageDetails);


    List<Product> findByProductNameContaining(String keyword, Pageable pageable);

    Long countByProductNameContaining(String keyword);
}


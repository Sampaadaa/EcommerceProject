//Interact with database

package com.example.Ecommerce.Repository;

import com.example.Ecommerce.models.Category;
import com.example.Ecommerce.models.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.net.ContentHandler;
import java.util.List;

public interface CategoryRepository extends JpaRepository <Category, Long> {

   

    Category findByCategoryName(String categoryName);

}

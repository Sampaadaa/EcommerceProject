//define business method
//methods that service layer must implement

package com.example.Ecommerce.services;

import com.example.Ecommerce.dto.CategoryDto;
import com.example.Ecommerce.dto.CategoryResponse;
import com.example.Ecommerce.models.Category;

public interface CategoryService {

    CategoryDto createCategory(Category category);
    CategoryResponse getCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);


    CategoryDto updateCategory(Category category, long categoryId);

    String deleteCategory(Long categoryId);

}

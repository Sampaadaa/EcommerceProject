package com.example.Ecommerce.controller;


import com.example.Ecommerce.config.AppConstant;
import com.example.Ecommerce.dto.CategoryDto;
import com.example.Ecommerce.dto.CategoryResponse;
import com.example.Ecommerce.dto.ProductDto;
import com.example.Ecommerce.dto.ProductResponse;
import com.example.Ecommerce.models.Category;
import com.example.Ecommerce.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")

public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping("/admin/category")
    public ResponseEntity<CategoryDto> createCategory(@RequestBody Category category){
        CategoryDto newProduct = categoryService.createCategory(category);
        return new ResponseEntity<>(newProduct, HttpStatus.CREATED);
    }

    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getCategories(
        @RequestParam(name = "pageNumber", defaultValue = AppConstant.pageNumber ) Integer pageNumber,
        @RequestParam(name = "pageSize", defaultValue = AppConstant.pageSize) Integer pageSize,
        @RequestParam(name = "sortBy", defaultValue = AppConstant.SORT_CATEGORIES_BY )String sortBy,
        @RequestParam(name = "sortOrder", defaultValue = AppConstant.SORT_DIR) String sortOrder){


            CategoryResponse categoryResponse = categoryService.getCategories(pageNumber, pageSize, sortBy, sortOrder);


            return new ResponseEntity<CategoryResponse>(categoryResponse, HttpStatus.FOUND);
        }



    @PutMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDto> updateCategory(@RequestBody Category category,
                                                      @PathVariable Long categoryId) {
        CategoryDto categoryDTO = categoryService.updateCategory(category, categoryId);

        return new ResponseEntity<CategoryDto>(categoryDTO, HttpStatus.OK);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId) {
        String status = categoryService.deleteCategory(categoryId);

        return new ResponseEntity<String>(status, HttpStatus.OK);
    }

}


//contains business logic

package com.example.Ecommerce.services;

import com.example.Ecommerce.Repository.CategoryRepository;
import com.example.Ecommerce.dto.CategoryDto;
import com.example.Ecommerce.dto.CategoryResponse;
import com.example.Ecommerce.exceptions.APIException;
import com.example.Ecommerce.exceptions.ResourceNotFoundException;
import com.example.Ecommerce.models.Category;
import com.example.Ecommerce.models.Product;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional

public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ProductService productService;




    @Override
    public CategoryDto createCategory(Category category) {
        // check if a category with the same name already exists in the database
        Category savedCategory = categoryRepository.findByCategoryName(category.getCategoryName());

        //Checks if a category with the same name was found
        if (savedCategory != null) {
            throw new APIException("Category with the name '" + category.getCategoryName() + "' already exists !!!");
        }
        //saves the new Category object to the database
        savedCategory = categoryRepository.save(category);

        // map the saved Category entity to a CategoryDTO object and return CategoryDto object
        return modelMapper.map(savedCategory, CategoryDto.class);
    }


    @Override
    public CategoryResponse getCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        // Determine sorting direction based on sortOrder
        Sort sort = sortOrder.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending():Sort.by(sortBy).descending();

        //create Pageable object for pagination
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sort);

        Page<Category> pageCategories = categoryRepository.findAll(pageDetails);

        //Retrieve  list of category from Repository  using pagination

        List<Category> categories = pageCategories.getContent();

        //check if category is empty or not
        if (categories.isEmpty()){
            throw new APIException("No any page is created");

        }
        // Map category entities to categoryDto using ModelMapper
        List<CategoryDto> categoryDTOs = categories.stream()
                .map(category -> modelMapper.map(category, CategoryDto.class)).collect(Collectors.toList());

        //Create a categoryResponse object
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOs);
        categoryResponse.setPageNumber(pageCategories.getNumber());
        categoryResponse.setPageSize(pageCategories.getSize());
        categoryResponse.setTotalElements(pageCategories.getTotalElements());
        categoryResponse.setTotalPages(pageCategories.getTotalPages());
        categoryResponse.setLastPage(pageCategories.isLast());

        return categoryResponse;

    }



    @Override
    public CategoryDto updateCategory(Category category, long categoryId) {
        Category savedCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        category.setCategoryId(categoryId);

        savedCategory = categoryRepository.save(category);

        return modelMapper.map(savedCategory, CategoryDto.class);
    }

    @Override

    //removes all products associated with the category before deleting the category itself
    public String deleteCategory(Long categoryId) {
        //find a category by its ID
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        // Retrieves the list of products associated with the category
        List<Product> products = category.getProducts();

       // Iterates over each product in the list to delete products with its id
        products.forEach(product -> {
            productService.deleteProduct(product.getProductId());
        });

        //deletes the category from repository
        categoryRepository.delete(category);

        return "Category with categoryId: " + categoryId + " deleted successfully !!!";
    }

}

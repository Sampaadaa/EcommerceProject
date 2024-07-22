package com.example.Ecommerce.services;

import com.example.Ecommerce.Repository.CartItemRepository;
import com.example.Ecommerce.Repository.CartRepository;
import com.example.Ecommerce.Repository.CategoryRepository;
import com.example.Ecommerce.Repository.ProductRepository;
import com.example.Ecommerce.dto.ProductDto;
import com.example.Ecommerce.dto.ProductResponse;
import com.example.Ecommerce.exceptions.APIException;
import com.example.Ecommerce.exceptions.ResourceNotFoundException;
import com.example.Ecommerce.models.Category;
import com.example.Ecommerce.models.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.modelmapper.ModelMapper;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;


@Service
@Transactional


public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;


    @Autowired
    private CategoryRepository categoryRepo;

    @Autowired
    private FileService fileService;



    @Value("${project.image}")
    private String path;


    @Override
    public ProductDto addProduct(Long categoryId, Product product) {
        //  Retrieve the category by ID and handle the case where it doesn't exist
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        //  Check if the product already exists in the category
        boolean isProductNotPresent = true;
        List<Product> products = category.getProducts();

        for (Product value : products) {
            if (value.getProductName().equals(product.getProductName())
                    && value.getDescription().equals(product.getDescription())) {
                isProductNotPresent = false;
                break;
            }
        }

        //  If the product is not already present, add it
        if (isProductNotPresent) {
            product.setImage("default.png"); // Set a default image
            product.setCategory(category); // Associate the product with the category



            // Save the product to the database
            Product savedProduct = productRepository.save(product);

            // Convert the saved product entity to a DTO and return it
            return modelMapper.map(savedProduct, ProductDto.class);
        } else {
            // If the product already exists, throw an exception
            throw new APIException("Product already exists !!!");
        }
    }





    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        // Determine sorting direction based on sortOrder
        Sort sort = sortOrder.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        // Create a Pageable object for pagination
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        // Retrieve products from the repository using pagination
        List<Product> products = productRepository.findAll(pageable).getContent();

        // Map Product entities to ProductDto using ModelMapper
        List<ProductDto> productDtos = products.stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .collect(Collectors.toList());

        // Create a ProductResponse object and populate it with necessary data
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDtos); // Set the list of ProductDto
        productResponse.setPageNumber(pageNumber); // Set the current page number
        productResponse.setPageSize(pageSize); // Set the page size
        productResponse.setTotalElements(productRepository.count()); // Set the total number of products
        productResponse.setTotalPages((int) Math.ceil((double) productRepository.count() / pageSize)); // Calculate total pages
        productResponse.setLast(pageNumber == productResponse.getTotalPages() - 1); // Determine if it's the last page


        return productResponse;
    }



    @Override
    public ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        //  Retrieve the category by ID and handle the case where it doesn't exist
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        //  sorting
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        //   pageable object for pagination
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        //  Find all products using pagination details
        Page<Product> pageProducts = productRepository.findAll(pageDetails);

        //  Get content of the page
        List<Product> products = pageProducts.getContent();

        //  Handle case where no products are found
        if (products.isEmpty()) {
            throw new APIException(category.getCategoryName() + " category doesn't contain any products !!!");
        }

        //  Map products to ProductDTOs
        List<ProductDto> productDTOs = products.stream().map(p -> modelMapper.map(p, ProductDto.class))
                .collect(Collectors.toList());

        //  Create and return the product response
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOs);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());

        return productResponse;
    }




    @Override
    public ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        List<Product> products = productRepository.findByProductNameContaining(keyword, pageable);

        List<ProductDto> productDtos = products.stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .collect(Collectors.toList());

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDtos);
        productResponse.setPageNumber(pageNumber);
        productResponse.setPageSize(pageSize);
        productResponse.setTotalElements(productRepository.countByProductNameContaining(keyword));
        productResponse.setTotalPages((int) Math.ceil((double) productRepository.countByProductNameContaining(keyword) / pageSize));
        productResponse.setLast(pageNumber == productResponse.getTotalPages() - 1);

        return productResponse;
    }

    @Override
    public ProductDto updateProduct(Long productId, Product product) {
        //Retrieve the Product from the Database by its id
        Product productFromDB = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        //check if product exist
        if (productFromDB == null) {
            throw new APIException("Product not found with productId: " + productId);
        }

         //Set Unchanged Fields
        product.setImage(productFromDB.getImage());
        product.setProductId(productId);
        product.setCategory(productFromDB.getCategory());
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDto.class);
    }

    @Override
    public ProductDto updateProductImage(Long productId, MultipartFile image) throws IOException {
        // Retrieve the product from the database
        Product productFromDB = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        // Upload the new image
        String fileName = fileService.uploadImage(path, image);

        // Update the product's image
        productFromDB.setImage(fileName);

        // Save the updated product
        Product updatedProduct = productRepository.save(productFromDB);

        // Map and return the updated product
        return modelMapper.map(updatedProduct, ProductDto.class);
    }




    @Override
    public String deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        productRepository.delete(product);
        return "Product deleted successfully";
    }




}
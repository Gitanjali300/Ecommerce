package com.axontic.ecommerce.service.impl;

import com.axontic.ecommerce.entity.ItemType;
import com.axontic.ecommerce.entity.Product;
import com.axontic.ecommerce.exception.InvalidInputException;
import com.axontic.ecommerce.exception.ProductNotFoundException;
import com.axontic.ecommerce.model.ProductDTO;
import com.axontic.ecommerce.repository.ProductRepository;
import com.axontic.ecommerce.service.ProductService;
import com.axontic.ecommerce.util.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the ProductService interface.
 * Handles operations related to products, including CRUD operations and search functionality.
 */
@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository repository;
    private final ObjectMapper objectMapper;

    public ProductServiceImpl(ProductRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    /**
     * Retrieves all products from the database.
     */
    @Override
    public List<ProductDTO> getAllProducts() {
        logger.info(Constants.FETCHING_ALL_PRODUCTS);
        try {
            List<Product> products = repository.findAll();
            logger.info("Successfully fetched {} products.", products.size());
            return products.stream()
                    .map(product -> objectMapper.convertValue(product, ProductDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error(Constants.ERROR_FETCHING_PRODUCTS, e.getMessage(), e);
            throw new RuntimeException("Error occurred while fetching all products.", e);
        }
    }

    /**
     * Retrieves a product by its ID.
     */
    @Override
    public ProductDTO getProductById(Long id) {
        logger.info("Fetching product with ID: {}", id);
        try {
            Product product = repository.findById(id)
                    .orElseThrow(() -> new ProductNotFoundException(Constants.PRODUCT_NOT_FOUND + id));
            logger.info("Product with ID {} fetched successfully.", id);
            return objectMapper.convertValue(product, ProductDTO.class);
        } catch (ProductNotFoundException e) {
            logger.error("Product with ID {} not found: {}", id, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching product with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error occurred while fetching the product.", e);
        }
    }

    /**
     * Saves a new product to the database.
     */
    @Override
    @Transactional
    public List<ProductDTO> saveProduct(List<ProductDTO> productDTOList) {
        logger.info("Saving new product: {}", productDTOList);
        try {
            List<Product> productList = productDTOList.stream().map(c ->objectMapper.convertValue(c, Product.class)).toList();
            List<Product> savedProduct = repository.saveAll(productList);
            logger.info("Product saved successfully with ID: {}");
            return savedProduct.stream().map(convertedProduct -> objectMapper.convertValue(convertedProduct, ProductDTO.class)).toList();
        } catch (Exception e) {
            logger.error("Error while saving product: {}", e.getMessage(), e);
            throw new RuntimeException("Error occurred while saving the product.", e);
        }
    }

    /**
     * Deletes a product by its ID.
     */
    @Override
    public void deleteProduct(Long id) {
        logger.info("Deleting product with ID: {}", id);
        try {
            if (repository.existsById(id)) {
                repository.deleteById(id);
                logger.info("Product with ID {} deleted successfully.", id);
            } else {
                logger.warn("Product with ID {} not found for deletion.", id);
                throw new ProductNotFoundException(Constants.PRODUCT_NOT_FOUND + id);
            }
        } catch (ProductNotFoundException e) {
            logger.error("Product with ID {} not found for deletion: {}", id, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Error while deleting product with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error occurred while deleting the product.", e);
        }
    }

    /**
     * Updates an existing product by its ID.
     */
    @Override
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        logger.info("Updating product with ID: {}", id);
        try {
            Product existingProduct = repository.findById(id)
                    .orElseThrow(() -> new ProductNotFoundException(Constants.PRODUCT_NOT_FOUND + id));

            objectMapper.updateValue(existingProduct, productDTO);

            Product updatedProduct = repository.save(existingProduct);
            logger.info("Product with ID {} updated successfully.", id);
            return objectMapper.convertValue(updatedProduct, ProductDTO.class);
        } catch (ProductNotFoundException e) {
            logger.error("Product with ID {} not found for update: {}", id, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating product with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error occurred while updating the product.", e);
        }
    }

    /**
     * Searches for products by their name.
     */
    @Override
    public List<ProductDTO> searchProductsByName(String name) {
        logger.info("Searching for products with name containing: {}", name);
        try {
            List<Product> products = repository.findByNameContainingIgnoreCase(name);
            logger.info("Found {} product(s) matching the name '{}'.", products.size(), name);
            return products.stream()
                    .map(product -> objectMapper.convertValue(product, ProductDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error while searching for products with name '{}': {}", name, e.getMessage(), e);
            throw new RuntimeException("Error occurred while searching for products.", e);
        }
    }

    /**
     * Suggests products based on criteria.
     */
    @Override
    public List<ProductDTO> findSuggestedProducts(List<Long> excludedProductIds, List<ItemType> itemTypes) {
        logger.info("Fetching suggested products with exclusions: {} and item types: {}", excludedProductIds, itemTypes);
        try {
            List<Product> products = repository.findSuggestedProducts(excludedProductIds, itemTypes);
            return products.stream()
                    .map(product -> objectMapper.convertValue(product, ProductDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error while fetching suggested products: {}", e.getMessage(), e);
            throw new RuntimeException("Error occurred while fetching suggested products.", e);
        }
    }
}

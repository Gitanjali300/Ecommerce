package com.axontic.ecommerce.controller;

import com.axontic.ecommerce.entity.ItemType;
import com.axontic.ecommerce.exception.ProductNotFoundException;
import com.axontic.ecommerce.model.ProductDTO;
import com.axontic.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller to handle HTTP requests related to Product management.
 * Supports CRUD operations and search functionality.
 */
@RestController
@RequestMapping("/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    /**
     * Create a new product.
     *
     * @param productDTOList the product DTO object to be created
     * @return ResponseEntity containing the created product DTO
     */
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<List<ProductDTO>> createProduct(@Valid @RequestBody List<ProductDTO> productDTOList) {
        logger.info("Received request to create a new product: {}", productDTOList);
       try {
            List<ProductDTO> createdProduct = productService.saveProduct(productDTOList);
            logger.info("Product created successfully with ID: {}");
            return ResponseEntity.ok(createdProduct);
       } catch (Exception e) {
            logger.error("Error while creating product: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * Get a product by its ID.
     *
     * @param id the unique ID of the product
     * @return ResponseEntity containing the requested product DTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id) {
        logger.info("Fetching product with ID: {}", id);
        try {
            ProductDTO productDTO = productService.getProductById(id);
            if (productDTO == null) {
                throw new ProductNotFoundException("Product not found with ID: " + id);
            }
            logger.info("Product fetched successfully: {}", productDTO);
            return ResponseEntity.ok(productDTO);
        } catch (ProductNotFoundException ex) {
            logger.error("Product not found with ID {}: {}", id, ex.getMessage());
            throw ex;
        } catch (Exception e) {
            logger.error("Error while fetching product with ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Retrieve a list of all products.
     *
     * @return ResponseEntity containing the list of product DTOs
     */
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        logger.info("Fetching all products");
        try {
            List<ProductDTO> products = productService.getAllProducts();
            logger.info("Successfully fetched {} products", products.size());
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            logger.error("Error while fetching all products: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Search products by name.
     *
     * @param name the name of the product(s) to search for
     * @return ResponseEntity containing the matching product DTOs
     */
    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam String name) {
        logger.info("Searching for products with name: {}", name);
        try {
            List<ProductDTO> products = productService.searchProductsByName(name);
            if (products.isEmpty()) {
                logger.warn("No products found with name: {}", name);
                throw new ProductNotFoundException("No products found with name: " + name);
            }
            logger.info("Found {} product(s) matching the name '{}'", products.size(), name);
            return ResponseEntity.ok(products);
        } catch (ProductNotFoundException ex) {
            logger.error("No products found with name '{}': {}", name, ex.getMessage());
            throw ex;
        } catch (Exception e) {
            logger.error("Error while searching for products with name '{}': {}", name, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Update a product by its ID.
     *
     * @param id         the unique ID of the product to update
     * @param productDTO the updated product DTO details
     * @return ResponseEntity containing the updated product DTO
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDTO productDTO) {
        logger.info("Updating product with ID: {}", id);
        try {
            ProductDTO updatedProduct = productService.updateProduct(id, productDTO);
            logger.info("Product updated successfully with ID: {}", updatedProduct.getItemNumber());
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            logger.error("Error while updating product with ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Delete a product by its ID.
     *
     * @param id the unique ID of the product to delete
     * @return ResponseEntity indicating deletion status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        logger.info("Deleting product with ID: {}", id);
        try {
            productService.deleteProduct(id);
            logger.info("Product deleted successfully with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error while deleting product with ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Get suggested products based on excluded product IDs and item types.
     *
     * @param excludedProductIds List of product IDs to exclude from suggestions (e.g., products already in cart).
     * @param itemTypes          List of item types (e.g., Tech, Beauty) to filter the suggestions.
     * @return ResponseEntity containing the list of suggested product DTOs.
     */
    @GetMapping("/suggested")
    public ResponseEntity<List<ProductDTO>> getSuggestedProducts(
            @RequestParam List<Long> excludedProductIds,
            @RequestParam List<ItemType> itemTypes) {

        try {
            List<ProductDTO> suggestedProducts = productService.findSuggestedProducts(excludedProductIds, itemTypes);

            if (suggestedProducts.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(suggestedProducts);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(List.of(new ProductDTO(null, "Error", 0.0, null, 0.0)));
        }
    }

}

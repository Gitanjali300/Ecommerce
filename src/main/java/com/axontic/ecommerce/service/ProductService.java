package com.axontic.ecommerce.service;
import com.axontic.ecommerce.entity.ItemType;
import com.axontic.ecommerce.model.ProductDTO;

import java.util.List;

public interface ProductService {

    /**
     * Retrieve all products.
     *
     * @return a list of ProductDTOs
     */
    List<ProductDTO> getAllProducts();

    /**
     * Retrieve a product by its ID.
     *
     * @param id the ID of the product to retrieve
     * @return ResponseEntity containing the ProductDTO
     */
    ProductDTO getProductById(Long id);

    /**
     * Save a new product.
     *
     * @param productDTOList the product DTO to save
     * @return ResponseEntity containing the created ProductDTO
     */
    List<ProductDTO> saveProduct(List<ProductDTO> productDTOList);

    /**
     * Delete a product by its ID.
     *
     * @param id the ID of the product to delete
     * @return ResponseEntity indicating the deletion status
     */
    void deleteProduct(Long id);

    /**
     * Update a product.
     *
     * @param id         the ID of the product to update
     * @param productDTO the updated product details
     * @return ProductDTO containing the updated product details
     */
    ProductDTO updateProduct(Long id, ProductDTO productDTO);

    /**
     * Search for products by name.
     *
     * @param name the name of the product(s) to search for
     * @return ResponseEntity containing a list of matching ProductDTOs
     */
    List<ProductDTO> searchProductsByName(String name);

    List<ProductDTO> findSuggestedProducts(List<Long> excludedProductIds, List<ItemType> itemTypes);

}

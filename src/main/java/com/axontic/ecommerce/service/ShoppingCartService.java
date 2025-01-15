package com.axontic.ecommerce.service;

import com.axontic.ecommerce.model.ShoppingCartDTO;
import com.axontic.ecommerce.response.StatusResponseDTO;


import java.util.List;

/**
 * Interface defining the business logic for Shopping Cart operations.
 */
public interface ShoppingCartService {

    /**
     * Retrieves all shopping carts belonging to a customer.
     *
     * @param customerId the ID of the customer.
     * @return a list of ShoppingCartDTOs.
     */
    List<ShoppingCartDTO> getCustomerCarts(Long customerId);

    /**
     * Adds a product to a shopping cart or updates its quantity if it already exists.
     *
     * @param cartId the ID of the shopping cart.
     * @param productId the ID of the product.
     * @param quantity the quantity to add.
     * @return the updated ShoppingCartDTO.
     */
     StatusResponseDTO addProductToCart(Long customerId, Long cartId, Long productId, int quantity);

    /**
     * Removes a product from a shopping cart or adjusts its quantity.
     *
     * @param cartId the ID of the shopping cart.
     * @param productId the ID of the product to remove.
     * @param quantity the quantity to remove.
     */
    void removeProductFromCart(Long cartId, Long productId, int quantity);

    /**
     * Deletes a shopping cart.
     *
     * @param cartId the ID of the shopping cart to delete.
     */
    void deleteCart(Long cartId);
}

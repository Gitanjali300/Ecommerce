package com.axontic.ecommerce.controller;

import com.axontic.ecommerce.model.ShoppingCartDTO;
import com.axontic.ecommerce.exception.ResourceNotFoundException;
import com.axontic.ecommerce.response.StatusResponseDTO;
import com.axontic.ecommerce.service.ShoppingCartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shopping-cart")
public class ShoppingCartController {

    private static final Logger logger = LoggerFactory.getLogger(ShoppingCartController.class);

    private final ShoppingCartService shoppingCartService;

    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    /**
     * Get all shopping carts for a customer.
     *
     * @param customerId The ID of the customer.
     * @return List of shopping cart DTOs.
     */
    @GetMapping("/{customerId}/carts")
    public ResponseEntity<List<ShoppingCartDTO>> getCustomerCarts(@PathVariable Long customerId) {
        logger.info("Request to fetch all shopping carts for customer ID: {}", customerId);
        try {
            List<ShoppingCartDTO> cartDTOs = shoppingCartService.getCustomerCarts(customerId);
            logger.info("Successfully fetched {} shopping carts for customer ID: {}", cartDTOs.size(), customerId);
            return ResponseEntity.ok(cartDTOs);
        } catch (ResourceNotFoundException ex) {
            logger.error("Error fetching shopping carts for customer ID {}: {}", customerId, ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Unexpected error fetching shopping carts for customer ID {}: {}", customerId, ex.getMessage());
            throw ex;
        }
    }


    /**
     * Add a product to a shopping cart or update its quantity.
     *
     * @param cartId    The ID of the shopping cart.
     * @param productId The ID of the product.
     * @param quantity  The quantity to add or update.
     * @return The updated shopping cart DTO.
     */
    @PostMapping("/add-product")
    public ResponseEntity<StatusResponseDTO> addProductToCart(
            @RequestParam Long customerId,
            @RequestParam(required = false) Long cartId,
            @RequestParam Long productId,
            @RequestParam int quantity) {

        logger.info("Request to add product ID {} with quantity {} for customer ID: {} to cart ID: {}",
                productId, quantity, customerId, cartId);

        try {
            StatusResponseDTO updatedCartDTO = shoppingCartService.addProductToCart(customerId, cartId, productId, quantity);
            logger.info("Product ID {} added/updated successfully for customer ID: {}", productId, customerId);
            return ResponseEntity.ok(updatedCartDTO);
        } catch (ResourceNotFoundException ex) {
            logger.error("Error adding product: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Unexpected error: {}", ex.getMessage());
            throw ex;
        }
    }


    /**
     * Remove a product from a shopping cart.
     *
     * @param customerId    The ID of the shopping cart.
     * @param productId The ID of the product to remove.
     * @param quantity  The quantity to remove.
     * @return Success message.
     */
    @DeleteMapping("/remove-product")
    public ResponseEntity<String> removeProductFromCart(
            @RequestParam Long customerId,
            @RequestParam Long productId,
            @RequestParam int quantity) {
        logger.info("Request to remove product ID {} with quantity {} from shopping cart ID: {}", productId, quantity, customerId);
        try {
            shoppingCartService.removeProductFromCart(customerId, productId, quantity);
            logger.info("Product ID {} removed successfully from shopping cart ID: {}", productId, customerId);
            return ResponseEntity.ok("Product removed successfully.");
        } catch (ResourceNotFoundException ex) {
            logger.error("Error removing product from shopping cart ID {}: {}", customerId, ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Unexpected error removing product from shopping cart ID {}: {}", customerId, ex.getMessage());
            throw ex;
        }
    }

    /**
     * Delete a shopping cart.
     *
     * @param cartId The ID of the shopping cart to delete.
     * @return Success message.
     */
    @DeleteMapping("/{cartId}")
    public ResponseEntity<String> deleteCart(@PathVariable Long cartId) {
        logger.info("Request to delete shopping cart ID: {}", cartId);
        try {
            shoppingCartService.deleteCart(cartId);
            logger.info("Shopping cart ID {} deleted successfully.", cartId);
            return ResponseEntity.ok("Shopping cart deleted successfully.");
        } catch (ResourceNotFoundException ex) {
            logger.error("Error deleting shopping cart ID {}: {}", cartId, ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Unexpected error deleting shopping cart ID {}: {}", cartId, ex.getMessage());
            throw ex;
        }
    }
}

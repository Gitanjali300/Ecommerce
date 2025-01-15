package com.axontic.ecommerce.repository;

import com.axontic.ecommerce.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /**
     * Find a specific product in a shopping cart by cart ID and product ID.
     *
     * @param shoppingCartId the ID of the shopping cart
     * @param productId the ID of the product
     * @return an Optional containing the ShoppingCartProduct entity if found
     */
    //Optional<CartItem> findByShoppingCartIdAndProductId(Long shoppingCartId, Long productId);
    Optional<CartItem> findByShoppingCartShoppingCartIdAndProductItemNumber(Long shoppingCartId, Long productId);
}

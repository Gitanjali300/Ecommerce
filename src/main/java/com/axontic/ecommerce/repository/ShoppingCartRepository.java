package com.axontic.ecommerce.repository;

import com.axontic.ecommerce.entity.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    List<ShoppingCart> findByCustomerCustomerId(Long customerId);
    @Query("SELECT c FROM ShoppingCart c LEFT JOIN FETCH c.cartItems WHERE c.id = :id")
    Optional<ShoppingCart> findByIdWithProducts(@Param("id") Long id);
}

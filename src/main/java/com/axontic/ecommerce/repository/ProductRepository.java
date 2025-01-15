package com.axontic.ecommerce.repository;

import com.axontic.ecommerce.entity.ItemType;
import com.axontic.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContainingIgnoreCase(String name);

   @Query("SELECT p FROM Product p WHERE p.id NOT IN :excludedProductIds " +
            "AND p.itemType IN :itemTypes ORDER BY p.rating DESC")
    List<Product> findSuggestedProducts(@Param("excludedProductIds") List<Long> excludedProductIds,
                                        @Param("itemTypes") List<ItemType> itemTypes);

}


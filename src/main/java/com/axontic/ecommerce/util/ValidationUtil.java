package com.axontic.ecommerce.util;

import com.axontic.ecommerce.entity.Customer;
import com.axontic.ecommerce.entity.Product;

public class ValidationUtil {
    public static boolean isValidCustomer(Customer customer) {
        return customer != null && customer.getEmail() != null && customer.getEmail().contains("@");
    }

    public static boolean isValidProduct(Product product) {
        return product != null && product.getName() != null && product.getPrice() != null && product.getItemNumber() != null;
    }
}
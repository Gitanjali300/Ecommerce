package com.axontic.ecommerce.util;

public final class Constants {

    private Constants() {
        throw new UnsupportedOperationException("Cannot instantiate Constants class");
    }

    // Customer-related messages
    public static final String CUSTOMER_NOT_FOUND = "Customer not found with ID: ";
    public static final String UNABLE_TO_FETCH_CUSTOMERS = "Unable to fetch customers";
    public static final String UNABLE_TO_SAVE_CUSTOMER = "Unable to save customer";
    public static final String UNABLE_TO_DELETE_CUSTOMER = "Unable to delete customer";

    //Product-related messages

        // Error messages
        public static final String PRODUCT_NOT_FOUND = "Product not found with ID: ";
        public static final String ERROR_FETCHING_PRODUCTS = "Error occurred while fetching all products";
        public static final String ERROR_FETCHING_PRODUCT = "Error occurred while fetching product with ID: ";
        public static final String ERROR_SAVING_PRODUCT = "Error occurred while saving product";
        public static final String ERROR_DELETING_PRODUCT = "Error occurred while deleting product with ID: ";
        public static final String ERROR_UPDATING_PRODUCT = "Error occurred while updating product with ID: ";
        public static final String ERROR_SEARCHING_PRODUCTS = "Error occurred while searching products with name: ";

        // Log messages
        public static final String FETCHING_ALL_PRODUCTS = "Fetching all products";
        public static final String FETCHING_PRODUCT = "Fetching product with ID: ";
        public static final String SAVING_PRODUCT = "Saving product: ";
        public static final String DELETING_PRODUCT = "Attempting to delete product with ID: ";
        public static final String UPDATING_PRODUCT = "Updating product with ID: ";
        public static final String SEARCHING_PRODUCTS = "Searching for products with name: ";


    }



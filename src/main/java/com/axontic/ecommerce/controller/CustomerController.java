package com.axontic.ecommerce.controller;

import com.axontic.ecommerce.model.CustomerDTO;
import com.axontic.ecommerce.service.CustomerService;
import com.axontic.ecommerce.service.ShoppingCartService;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping(value = "/api/customers", produces = MediaType.APPLICATION_JSON_VALUE)
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
    private final CustomerService service;
    private final ShoppingCartService shoppingCartService;

    public CustomerController(CustomerService service, ShoppingCartService shoppingCartService) {
        this.service = service;
        this.shoppingCartService = shoppingCartService;
    }
    /**
     * Get all customers
     * @return List of customers
     */
    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        logger.info("Received request to fetch all customers.");
        try {
            List<CustomerDTO> customers = service.getAllCustomers();
            logger.info("Successfully fetched {} customers.", customers.size());
            return ResponseEntity.ok(customers);
        } catch (Exception ex) {
            logger.error("Error occurred while fetching customers: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Get a customer by ID
     * @param id Customer ID
     * @return Customer details
     * Customers	to	view	all	their	shopping	carts	and	their	contents
     */
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long id) {
        logger.info("Received request to fetch customer with ID: {}", id);
        try {
            CustomerDTO customer = service.getCustomerById(id);
            logger.info("Successfully fetched customer: {}", customer);
            return ResponseEntity.ok(customer);
        } catch (Exception ex) {
            logger.error("Error occurred while fetching customer with ID {}: {}", id, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Create a new customer
     * @param customer Valid customer data
     * @return Created customer
     */
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<CustomerDTO> createCustomer(@Valid @RequestBody CustomerDTO customer) {
        logger.info("Received request to create a new customer: {}", customer);
        try {
            CustomerDTO createdCustomer = service.saveCustomer(customer);
            logger.info("Successfully created customer with ID: {}", createdCustomer.getCustomerId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
        } catch (Exception ex) {
            logger.error("Error occurred while creating customer: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * Update an existing customer by ID
     * @param id Customer ID
     * @param updatedCustomer Updated customer data
     * @return Updated customer details
     */
    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerDTO updatedCustomer) {
        logger.info("Received request to update customer with ID: {}", id);
        try {
            CustomerDTO customer = service.updateCustomer(id, updatedCustomer);
            logger.info("Successfully updated customer with ID: {}", id);
            return ResponseEntity.ok(customer);
        } catch (Exception ex) {
            logger.error("Error occurred while updating customer with ID {}: {}", id, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

        /**
         * Delete a customer by ID
         * @param id Customer ID
         * @return HTTP 204 if successful
         */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        logger.info("Received request to delete customer with ID: {}", id);
        try {
            service.deleteCustomer(id);
            logger.info("Successfully deleted customer with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            logger.error("Error occurred while deleting customer with ID {}: {}", id, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}

package com.axontic.ecommerce.service.impl;

import com.axontic.ecommerce.entity.Customer;
import com.axontic.ecommerce.exception.InvalidInputException;
import com.axontic.ecommerce.exception.ResourceNotFoundException;

import com.axontic.ecommerce.model.CustomerDTO;
import com.axontic.ecommerce.repository.CustomerRepository;
import com.axontic.ecommerce.service.CustomerService;
import com.axontic.ecommerce.util.Constants;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Service
public class CustomerServiceImpl implements CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerRepository repository;
    private final ObjectMapper objectMapper;

    public CustomerServiceImpl(CustomerRepository repository,ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDTO> getAllCustomers() {
        logger.info("Fetching all customers from the database.");
        try {
            List<Customer> customers = repository.findAll();
            logger.info("Successfully fetched {} customers.", customers.size());

            return customers.stream()
                    .map(customer -> objectMapper.convertValue(customer, CustomerDTO.class))
                    .toList();
        } catch (Exception ex) {
            logger.error("Error occurred while fetching customers: {}", ex.getMessage(), ex);
            throw new RuntimeException(Constants.UNABLE_TO_FETCH_CUSTOMERS, ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDTO getCustomerById(Long id) {
        logger.info("Fetching customer with ID: {} from the database.", id);
        try {
            Customer customer = repository.findById(id).orElseThrow(() -> {
                logger.warn("Customer with ID {} not found.", id);
                return new ResourceNotFoundException(Constants.CUSTOMER_NOT_FOUND + id);
            });
            CustomerDTO customerDTO = objectMapper.convertValue(customer, CustomerDTO.class);
            logger.info("Successfully fetched customer with ID: {}", id);
            return customerDTO;
        } catch (ResourceNotFoundException ex) {
            logger.error("Error: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Unexpected error while fetching customer with ID {}: {}", id, ex.getMessage(), ex);
            throw new RuntimeException(Constants.UNABLE_TO_FETCH_CUSTOMERS, ex);
        }
    }


    @Override
    @Transactional
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        logger.info("Saving customer to the database: {}", customerDTO);
        try {
            Customer customer = objectMapper.convertValue(customerDTO, Customer.class);
            Customer savedCustomer = repository.save(customer);
            logger.info("Successfully saved customer with ID: {}", savedCustomer.getCustomerId());
            return objectMapper.convertValue(savedCustomer, CustomerDTO.class);
        } catch (Exception ex) {
            logger.error("Error occurred while saving customer: {}", ex.getMessage(), ex);
            throw new InvalidInputException(Constants.UNABLE_TO_SAVE_CUSTOMER, ex);
        }
    }


    @Override
    @Transactional
    public CustomerDTO updateCustomer(Long id, CustomerDTO updatedCustomerDTO) {
        logger.info("Updating customer with ID: {}", id);
        try {
            // Fetch the existing customer from the database
            Customer existingCustomer = repository.findById(id).orElseThrow(() -> {
                logger.warn("Customer with ID {} not found for update.", id);
                return new ResourceNotFoundException(Constants.CUSTOMER_NOT_FOUND + id);
            });

            // Merge fields from updatedCustomerDTO into existingCustomer
            objectMapper.updateValue(existingCustomer, updatedCustomerDTO);

            // Save the updated customer
            Customer savedCustomer = repository.save(existingCustomer);
            logger.info("Successfully updated customer with ID: {}", savedCustomer.getCustomerId());

            return objectMapper.convertValue(savedCustomer, CustomerDTO.class);
        } catch (ResourceNotFoundException ex) {
            logger.error("Error: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Unexpected error while updating customer with ID {}: {}", id, ex.getMessage(), ex);
            throw new RuntimeException(Constants.UNABLE_TO_SAVE_CUSTOMER, ex);
        }
    }


    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        logger.info("Deleting customer with ID: {}", id);
        try {
            Customer existingCustomer = repository.findById(id).orElseThrow(() -> {
                logger.warn("Customer with ID {} not found for deletion.", id);
                return new ResourceNotFoundException(Constants.CUSTOMER_NOT_FOUND + id);
            });
            repository.deleteById(id);
            logger.info("Successfully deleted customer with ID: {}", id);

        } catch (ResourceNotFoundException ex) {
            logger.error("Error: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Unexpected error while deleting customer with ID {}: {}", id, ex.getMessage(), ex);
            throw new RuntimeException(Constants.UNABLE_TO_DELETE_CUSTOMER, ex);
        }
    }



}
package com.axontic.ecommerce.service;

import com.axontic.ecommerce.model.CustomerDTO;
import java.util.List;

public interface CustomerService {
    List<CustomerDTO> getAllCustomers();

    CustomerDTO getCustomerById(Long id);

    CustomerDTO saveCustomer(CustomerDTO customer);

    CustomerDTO updateCustomer(Long id, CustomerDTO updatedCustomer);

    void deleteCustomer(Long id);

}

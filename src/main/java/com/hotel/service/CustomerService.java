package com.hotel.service;

import com.hotel.model.Customer;
import com.hotel.repository.CustomerRepository;

import java.util.List;
import java.util.Optional;

public class CustomerService {
    private final CustomerRepository customerRepository;

    // Constructor that accepts a CustomerRepository instance
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository; // Use the provided repository instance
    }

    // Add a customer
    public void create(Customer customer) {
        customerRepository.save(customer);
    }

    // Retrieve a customer by ID
    public Optional<Customer> getById(int customerId) {
        return customerRepository.findById(customerId);
    }
    public List<Customer> getAll() {
        return customerRepository.findAll();
    }

//    // Update a customer
//    public void update(Customer customer) {
//        customerRepository.update(customer);
//    }

//    // Delete a customer
//    public void delete(int customerId) {
//        customerRepository.delete(customerId);
//    }
}

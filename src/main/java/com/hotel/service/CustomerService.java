package com.hotel.service;

import com.hotel.model.Customer;
import com.hotel.repository.CustomerRepository;

import java.util.Optional;

public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService() {
        this.customerRepository = new CustomerRepository();
    }

    // Ajouter un client
    public void create(Customer customer) {
        customerRepository.save(customer);
    }

    // Récupérer un client par ID
    public Optional<Customer> getById(int customerId) {
        return customerRepository.findById(customerId);
    }

    // Mettre à jour un client
    public void update(Customer customer) {
        customerRepository.update(customer);
    }

    // Supprimer un client
    public void delete(int customerId) {
        customerRepository.delete(customerId);
    }
}

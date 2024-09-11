package com.hotel.service;

import com.hotel.model.Customer;
import com.hotel.repository.CustomerRepository;

import java.util.Optional;

public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService() {
        this.customerRepository = CustomerRepository.getInstance(); // Utiliser le pattern Singleton
    }

    // Méthode d'authentification pour vérifier l'existence d'un client par email
    public Optional<Customer> authenticate(String email) {
        // Cherche le client dans la base de données
        return customerRepository.findByEmail(email);
    }
}

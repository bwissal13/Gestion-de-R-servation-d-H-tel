package com.hotel.repository;

import com.hotel.config.DatabaseConfig;
import com.hotel.model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerRepository {
    private final DatabaseConfig dbConfig;


    public CustomerRepository(DatabaseConfig dbConfig) {
        this.dbConfig = dbConfig;
    }
    public List<Customer> findAll() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers";
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Customer customer = new Customer(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone")
                );
                customers.add(customer);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    // Add a customer to the database
    public void save(Customer customer) {
        String query = "INSERT INTO customers (id, name, email, phone_number) VALUES (?, ?, ?, ?)";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, customer.getId());
            stmt.setString(2, customer.getName());
            stmt.setString(3, customer.getEmail());
            stmt.setString(4, customer.getPhoneNumber());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error while inserting customer: " + e.getMessage());
        }
    }

    // Retrieve a customer by their ID
    public Optional<Customer> findById(int customerId) {
        String query = "SELECT * FROM customers WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Customer customer = new Customer(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone_number")
                );
                return Optional.of(customer);
            }
        } catch (SQLException e) {
            System.err.println("Error while retrieving customer: " + e.getMessage());
        }
        return Optional.empty();
    }

    // Update a customer
//    public void update(Customer customer) {
//        String query = "UPDATE customers SET name = ?, email = ?, phone_number = ? WHERE id = ?";
//
//        try (Connection conn = dbConfig.getConnection(); // Use dbConfig to obtain the connection
//             PreparedStatement stmt = conn.prepareStatement(query)) {
//            stmt.setString(1, customer.getName());
//            stmt.setString(2, customer.getEmail());
//            stmt.setString(3, customer.getPhoneNumber());
//            stmt.setInt(4, customer.getId());
//            stmt.executeUpdate();
//        } catch (SQLException e) {
//            System.err.println("Error while updating customer: " + e.getMessage());
//        }
//    }
//
//    // Delete a customer
//    public void delete(int customerId) {
//        String query = "DELETE FROM customers WHERE id = ?";
//
//        try (Connection conn = dbConfig.getConnection(); // Use dbConfig to obtain the connection
//             PreparedStatement stmt = conn.prepareStatement(query)) {
//            stmt.setInt(1, customerId);
//            stmt.executeUpdate();
//        } catch (SQLException e) {
//            System.err.println("Error while deleting customer: " + e.getMessage());
//        }
//    }
}

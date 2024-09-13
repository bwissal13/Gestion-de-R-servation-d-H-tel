package com.hotel.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {
    private static final Properties properties = new Properties();
    private static DatabaseConfig instance;
    private static final Object lock = new Object();

    // Initialisation statique
    static {
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.out.println("Désolé, le fichier application.properties est introuvable.");
            } else {
                properties.load(input);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // Constructeur privé pour éviter l'instanciation depuis l'extérieur
    private DatabaseConfig() {
        // Initialisation ou configuration si nécessaire
    }

    // Méthode pour obtenir l'unique instance
    public static DatabaseConfig getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new DatabaseConfig();
                }
            }
        }
        return instance;
    }

    public String getUrl() {
        return properties.getProperty("db.url");
    }

    public String getUsername() {
        return properties.getProperty("db.username");
    }

    public String getPassword() {
        return properties.getProperty("db.password");
    }

    // Méthode pour obtenir une connexion à la base de données
    public Connection getConnection() throws SQLException {
        String url = getUrl();
        String username = getUsername();
        String password = getPassword();

        // Retourne une connexion JDBC en utilisant DriverManager
        return DriverManager.getConnection(url, username, password);
    }
}

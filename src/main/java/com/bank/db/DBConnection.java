package com.bank.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL      = System.getenv().getOrDefault("DB_URL", "jdbc:mysql://localhost:3306/bank_system");
    private static final String USERNAME = System.getenv().getOrDefault("DB_USER", "root");
    private static final String PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "your_password");

    public static Connection getConnection() throws SQLException {
        String fullUrl = URL
                + "?useSSL=false"
                + "&serverTimezone=UTC"
                + "&allowPublicKeyRetrieval=true";

        return DriverManager.getConnection(fullUrl, USERNAME, PASSWORD);
    }
}


package com.pluralsight;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainApplication {

    public static void main(String[] args) {
        String dbUrl = "jdbc:mysql://localhost:3306/northwind";
        String username = "root"; // e.g., "root" or a specific user for northwind
        String password = "KahootKing24"; // Your MySQL password

        // The SQL query to select all products.
        // We'll select ProductID and ProductName for this example.
        String query = "SELECT ProductID, ProductName FROM Products ORDER BY ProductName";

        System.out.println("Connecting to Northwind database...");

        // Using try-with-resources to ensure database resources are closed automatically
        try {
            // 1. Load the MySQL JDBC driver (optional for modern JDBC if JAR is in classpath,
            // but good practice to include, especially for clarity and older systems).
            // For MySQL Connector/J 8.0 and later, the driver class name is com.mysql.cj.jdbc.Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 2. Establish the connection to the database
            try (Connection connection = DriverManager.getConnection(dbUrl, username, password);
                 // 3. Create a statement object to execute SQL
                 Statement statement = connection.createStatement();
                 // 4. Execute the query and get the result set
                 ResultSet resultSet = statement.executeQuery(query)) {

                System.out.println("Successfully connected to the database.");
                System.out.println("Fetching product names...");
                System.out.println("----------------------------------------");
                System.out.println("Products from Northwind Traders:");
                System.out.println("----------------------------------------");

                int productCount = 0;
                // 5. Process the result set
                while (resultSet.next()) {
                    // Retrieve data by column name (safer) or column index (faster but less readable)
                    // int productId = resultSet.getInt("ProductID");
                    String productName = resultSet.getString("ProductName");

                    // Display the product name
                    System.out.println(productName);
                    productCount++;
                }

                System.out.println("----------------------------------------");
                if (productCount == 0) {
                    System.out.println("No products found or table is empty.");
                } else {
                    System.out.println("Total products displayed: " + productCount);
                }

            } // The Connection, Statement, and ResultSet are automatically closed here
            // due to try-with-resources

        } catch (ClassNotFoundException e) {
            // This error occurs if the JDBC driver JAR is not on the classpath
            // or if the driver class name is incorrect.
            System.err.println("Error: MySQL JDBC Driver not found.");
            System.err.println("Please ensure the MySQL Connector/J dependency is correctly added to your pom.xml and loaded by Maven.");
            e.printStackTrace();
        } catch (SQLException e) {
            // This error can occur for various reasons:
            // - Database server is not running or not reachable.
            // - Incorrect database URL, username, or password.
            // - Insufficient user permissions.
            // - SQL syntax errors in the query.
            // - Network issues.
            System.err.println("Database Error: Could not connect to or query the database.");
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
            System.err.println("Check your database connection details, server status, and query.");
            e.printStackTrace();
        }
    } 
}

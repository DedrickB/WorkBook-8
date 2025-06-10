package com.pluralsight;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;


public class MainApplication {

    public static void main(String[] args) {
        String dbUrl = "jdbc:mysql://localhost:3306/northwind";
        String username = "root"; // e.g., "root"
        String password = "KahootKing24"; // Your MySQL password

        // The SQL query to select required product details
        String query = "SELECT ProductID, ProductName, UnitPrice, UnitsInStock FROM Products ORDER BY ProductID";

        Scanner scanner = new Scanner(System.in);
        int displayOption = 0;

        // Get display option from user
        while (true) {
            System.out.println("\nSelect display format for products:");
            System.out.println("1: Stacked Information (Product ID, Name, Price, Stock on separate lines)");
            System.out.println("2: Rows of Information (Tabular format)");
            System.out.print("Enter your choice (1 or 2): ");
            if (scanner.hasNextInt()) {
                displayOption = scanner.nextInt();
                if (displayOption == 1 || displayOption == 2) {
                    break;
                } else {
                    System.out.println("Invalid choice. Please enter 1 or 2.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number (1 or 2).");
                scanner.next(); // Consume the invalid input
            }
        }
        scanner.nextLine(); // Consume newline left-over

        System.out.println("\nConnecting to Northwind database...");

        try {
            // 1. Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 2. Establish the connection and execute query using try-with-resources
            try (Connection connection = DriverManager.getConnection(dbUrl, username, password);
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {

                System.out.println("Successfully connected to the database.");
                System.out.println("Fetching product details...");
                System.out.println("----------------------------------------");

                if (displayOption == 2) { // Print header for tabular format
                    System.out.printf("%-10s %-35s %-10s %-10s%n", "ID", "Name", "Price", "Stock");
                    System.out.printf("%-10s %-35s %-10s %-10s%n", "----", "-----------------------------------", "----------", "----------");
                }

                int productCount = 0;
                // 3. Process the result set
                while (resultSet.next()) {
                    int productId = resultSet.getInt("ProductID");
                    String productName = resultSet.getString("ProductName");
                    double unitPrice = resultSet.getDouble("UnitPrice");
                    int unitsInStock = resultSet.getInt("UnitsInStock");
                    productCount++;

                    if (displayOption == 1) { // Stacked Information
                        System.out.println("Product Id: " + productId);
                        System.out.println("Name: " + productName);
                        System.out.printf("Price: %.2f%n", unitPrice); // Format price to 2 decimal places
                        System.out.println("Stock: " + unitsInStock);
                        System.out.println("------------------");
                    } else { // displayOption == 2, Rows of Information
                        System.out.printf("%-10d %-35s %-10.2f %-10d%n",
                                productId,
                                productName,
                                unitPrice,
                                unitsInStock);
                    }
                }

                System.out.println("----------------------------------------");
                if (productCount == 0) {
                    System.out.println("No products found or table is empty.");
                } else {
                    System.out.println("Total products displayed: " + productCount);
                }

            } // Connection, Statement, and ResultSet are automatically closed here

        } catch (ClassNotFoundException e) {
            System.err.println("Error: MySQL JDBC Driver not found.");
            System.err.println("Please ensure the MySQL Connector/J dependency is correctly added to your pom.xml and loaded by Maven.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database Error: Could not connect to or query the database.");
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close(); // Close the scanner
        }
    }
}
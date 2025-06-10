package com.pluralsight;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class MainApplication {

    // --- Configuration ---
    private static final String DB_URL = "jdbc:mysql://localhost:3306/northwind";
    private static final String DB_USERNAME = "root"; // e.g., "root"
    private static final String DB_PASSWORD = "KahootKing24"; // Your MySQL password
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Try to load the driver. No 'throws' on main, so handle ClassNotFoundException here.
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("CRITICAL ERROR: MySQL JDBC Driver not found. The application cannot continue.");
            e.printStackTrace(); // Log the full error for debugging.
            return; // Exit if driver is not found
        }

        boolean running = true;
        while (running) {
            System.out.println("\nWhat do you want to do?");
            System.out.println("1) Display all products");
            System.out.println("2) Display all customers");
            System.out.println("0) Exit");
            System.out.print("Select an option: ");

            String choiceStr = scanner.nextLine().trim();
            int choice = -1;

            try {
                choice = Integer.parseInt(choiceStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    displayAllProducts();
                    break;
                case 2:
                    displayAllCustomers();
                    break;
                case 0:
                    running = false;
                    System.out.println("Exiting application. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close(); // Close scanner when loop ends
    }

    private static void displayAllProducts() {
        String query = "SELECT ProductID, ProductName, UnitPrice, UnitsInStock FROM Products ORDER BY ProductID";
        System.out.println("\nFetching all products...");

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // 1. Get a connection
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            System.out.println("Connected to database for products.");

            // 2. Create a statement
            statement = connection.createStatement();

            // 3. Execute the query
            resultSet = statement.executeQuery(query);
            System.out.println("Product List:");
            System.out.println("----------------------------------------");
            System.out.printf("%-10s %-35s %-10s %-10s%n", "ID", "Name", "Price", "Stock");
            System.out.printf("%-10s %-35s %-10s %-10s%n", "----", "-----------------------------------", "----------", "----------");

            int productCount = 0;
            // 4. Process the results
            while (resultSet.next()) {
                int productId = resultSet.getInt("ProductID");
                String productName = resultSet.getString("ProductName");
                double unitPrice = resultSet.getDouble("UnitPrice");
                int unitsInStock = resultSet.getInt("UnitsInStock");
                productCount++;

                System.out.printf("%-10d %-35s %-10.2f %-10d%n",
                        productId, productName, unitPrice, unitsInStock);
            }

            System.out.println("----------------------------------------");
            if (productCount == 0) {
                System.out.println("No products found.");
            } else {
                System.out.println("Total products displayed: " + productCount);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching products:");
            printSqlExceptionDetails(e);
        } finally {
            // 5. Close the resources in reverse order of creation
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                printSqlExceptionDetails(e, "closing product ResultSet");
            }
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                printSqlExceptionDetails(e, "closing product Statement");
            }
            try {
                if (connection != null) {
                    connection.close();
                    System.out.println("Disconnected from database (products).");
                }
            } catch (SQLException e) {
                printSqlExceptionDetails(e, "closing product Connection");
            }
        }
    }

    private static void displayAllCustomers() {
        // Query to select customer details, ordered by Country
        String query = "SELECT ContactName, CompanyName, City, Country, Phone FROM Customers ORDER BY Country, CompanyName";
        System.out.println("\nFetching all customers...");

        Connection connection = null; // Declare outside try to be accessible in finally
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // 1. Get a connection
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            System.out.println("Connected to database for customers.");

            // 2. Create a statement
            statement = connection.createStatement();

            // 3. Execute the query
            resultSet = statement.executeQuery(query);
            System.out.println("Customer List (Ordered by Country):");
            System.out.println("-----------------------------------------------------------------------------------------------------------");
            System.out.printf("%-30s | %-35s | %-20s | %-20s | %-20s%n",
                    "Contact Name", "Company Name", "City", "Country", "Phone");
            System.out.printf("%-30s | %-35s | %-20s | %-20s | %-20s%n",
                    "------------------------------", "-----------------------------------", "--------------------", "--------------------", "--------------------");

            int customerCount = 0;
            // 4. Process the results
            while (resultSet.next()) {
                String contactName = resultSet.getString("ContactName");
                String companyName = resultSet.getString("CompanyName");
                String city = resultSet.getString("City");
                String country = resultSet.getString("Country");
                String phone = resultSet.getString("Phone");
                customerCount++;

                System.out.printf("%-30s | %-35s | %-20s | %-20s | %-20s%n",
                        contactName, companyName, city, country, phone);
            }

            System.out.println("-----------------------------------------------------------------------------------------------------------");
            if (customerCount == 0) {
                System.out.println("No customers found.");
            } else {
                System.out.println("Total customers displayed: " + customerCount);
            }

        } catch (SQLException e) {
            // Handle SQL-related errors
            System.err.println("Error fetching customers:");
            printSqlExceptionDetails(e);
        } finally {
            // 5. Close the resources in the finally block to ensure they are always closed
            //    Close in reverse order of creation.
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                printSqlExceptionDetails(e, "closing customer ResultSet");
            }
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                printSqlExceptionDetails(e, "closing customer Statement");
            }
            try {
                if (connection != null) {
                    connection.close(); // Close the connection
                    System.out.println("Disconnected from database (customers).");
                }
            } catch (SQLException e) {
                printSqlExceptionDetails(e, "closing customer Connection");
            }
        }
    }

    // Overloaded helper method for printing SQLException details
    private static void printSqlExceptionDetails(SQLException e) {
        printSqlExceptionDetails(e, "an unknown operation");
    }

    private static void printSqlExceptionDetails(SQLException e, String context) {
        System.err.println("Database Error during " + context + ":");
        System.err.println("  SQLState: " + e.getSQLState());
        System.err.println("  Error Code: " + e.getErrorCode());
        System.err.println("  Message: " + e.getMessage());
        // e.printStackTrace(); // Uncomment for full stack trace during development
    }
}
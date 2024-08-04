/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.banking;

/**
 *
 * @author Ashu
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLConnection {

    // JDBC URL, username, and password of MySQL server
    private static final String URL = "jdbc:mysql://localhost:3306";
    private static final String USER = "root";
    private static final String PASSWORD = ""; 
    

    public static Connection getConnection() throws SQLException {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found. Include it in your library path.");
            e.printStackTrace();
        }
        // Establish the connection
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Method to create the database and tables
    public static void createDatabaseAndTables() {

        try (Connection connection = MySQLConnection.getConnection();
             Statement statement = connection.createStatement()){
            // Create Database
            
            String createDatabase = "CREATE DATABASE IF NOT EXISTS bankingApplication";
            statement.executeUpdate(createDatabase);

            // Use the newly created database
            String useDatabase = "USE bankingApplication";
            statement.executeUpdate(useDatabase);

            // Create Users table with additional columns
            String createUsersTable = "CREATE TABLE IF NOT EXISTS Users (" +
                    "user_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(255) UNIQUE NOT NULL, " +
                    "password_hash VARCHAR(255) NOT NULL, " +
                    "father_name VARCHAR(255) NOT NULL, " +
                    "mother_name VARCHAR(255) NOT NULL, " +
                    "dob DATE NOT NULL, " +
                    "gender ENUM('male', 'female', 'other') NOT NULL, " +
                    "aadhar_number VARCHAR(12) UNIQUE NOT NULL, " +
                    "email VARCHAR(255) UNIQUE NOT NULL, " +
                    "phone_number VARCHAR(20) UNIQUE NOT NULL, " +
                    "country VARCHAR(100) NOT NULL, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP)";
            statement.executeUpdate(createUsersTable);

            // Create Accounts table with additional columns
            String createAccountsTable = "CREATE TABLE IF NOT EXISTS Accounts (" +
                    "account_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "user_id INT NOT NULL, " +
                    "account_type ENUM('savings', 'checking') NOT NULL, " +
                    "account_number VARCHAR(20) UNIQUE NOT NULL, " +
                    "IFSC_code VARCHAR(11) NOT NULL, " +
                    "balance DECIMAL(15, 2) NOT NULL, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (user_id) REFERENCES Users(user_id))";
            statement.executeUpdate(createAccountsTable);

            // Create Transactions table with additional columns for transfers
            String createTransactionsTable = "CREATE TABLE IF NOT EXISTS Transactions (" +
                    "transaction_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "account_id INT NOT NULL, " +
                    "from_account_id INT, " +
                    "to_account_id INT, " +
                    "transaction_type ENUM('deposit', 'withdrawal', 'transfer') NOT NULL, " +
                    "amount DECIMAL(15, 2) NOT NULL, " +
                    "balance_after DECIMAL(15, 2) NOT NULL, " +
                    "description VARCHAR(255), " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (account_id) REFERENCES Accounts(account_id), " +
                    "FOREIGN KEY (from_account_id) REFERENCES Accounts(account_id), " +
                    "FOREIGN KEY (to_account_id) REFERENCES Accounts(account_id))";
            statement.executeUpdate(createTransactionsTable);

        } catch (SQLException e) {
            System.out.println("SQL exception occurred. Check output console.");
            e.printStackTrace();
        }
    }
}
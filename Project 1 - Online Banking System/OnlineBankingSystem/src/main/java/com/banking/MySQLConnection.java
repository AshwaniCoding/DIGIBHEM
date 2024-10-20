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
    private static final String URL1 = "jdbc:mysql://localhost:3306/" + ConfigReader.getConfigValue("database.name");
    private static final String USER = "root";
    private static final String PASSWORD = ""; 
    

    public static Connection getConnection() throws SQLException {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // Establish the connection
        return DriverManager.getConnection(URL1, USER, PASSWORD);
    }
    
    public static Connection getConnectionForDatabase() throws SQLException {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // Establish the connection
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    

    // Method to create the database and tables
    public static void createDatabaseAndTables() {

        try (Connection connection = MySQLConnection.getConnectionForDatabase();
             Statement statement = connection.createStatement()){
            // Create Database
            
            String createDatabase = "CREATE DATABASE IF NOT EXISTS " + ConfigReader.getConfigValue("database.name");
            statement.executeUpdate(createDatabase);

            // Use the newly created database
            String useDatabase = "USE " + ConfigReader.getConfigValue("database.name");
            statement.executeUpdate(useDatabase);

            // Create Users table with additional columns
            String createUsersTable = "CREATE TABLE IF NOT EXISTS Users (" +
                    "user_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "full_name VARCHAR(255) NOT NULL, " +
                    "father_name VARCHAR(255) NOT NULL, " +
                    "mother_name VARCHAR(255) NOT NULL, " +
                    "aadhar VARCHAR(12) UNIQUE NOT NULL, " +
                    "mobile VARCHAR(20) UNIQUE NOT NULL, " +
                    "email VARCHAR(255) UNIQUE NOT NULL, " +
                    "username VARCHAR(255) UNIQUE NOT NULL, " +
                    "dob DATE NOT NULL, " +
                    "gender ENUM('male', 'female', 'other') NOT NULL, " +
                    "country VARCHAR(100) NOT NULL, " +
                    "pin VARCHAR(6) NOT NULL, " +
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
                    "balance_after_sender DECIMAL(15, 2), " +
                    "balance_after_receiver DECIMAL(15, 2), " +
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
        
        createTrigger();
    }
    
    //function to create a trigger which automatically takes as a row when new user created
    public static void createTrigger() {
    // Trigger for creating an account after a new user is inserted
    String triggerSQLAccount = "CREATE TRIGGER IF NOT EXISTS after_user_insert\n"
            + "AFTER INSERT ON Users\n"
            + "FOR EACH ROW\n"
            + "BEGIN\n"
            + "    DECLARE account_number VARCHAR(20);\n"
            + "    SET account_number = CONCAT('ACC', NEW.user_id, LPAD(FLOOR(RAND() * 1000000), 6, '0'));\n"
            + "    INSERT INTO Accounts (user_id, account_number, account_type, IFSC_code, balance)\n"
            + "    VALUES (NEW.user_id, account_number, 'Savings', 'OBSA000044', 0.00);\n"
            + "END";


    try (Connection connection = MySQLConnection.getConnection(); Statement statement = connection.createStatement()) {
        // Execute both trigger creation SQLs
        statement.execute(triggerSQLAccount);
        System.out.println("Triggers created successfully");
    } catch (SQLException e) {
        e.printStackTrace();
    }
    
}

}

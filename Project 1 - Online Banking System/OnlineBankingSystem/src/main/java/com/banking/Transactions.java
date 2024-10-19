/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.banking;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class Transactions {

    // TransactionDetails class to hold transaction data
    public static class TransactionDetails {
        private String date;
        private String name;
        private String transactionType; // CR for Credit, DR for Debit
        private float amount;
        private float balanceAfter;

        public TransactionDetails(String date, String name, String transactionType, float amount, float balanceAfter) {
            this.date = date;
            this.name = name;
            this.transactionType = transactionType;
            this.amount = amount;
            this.balanceAfter = balanceAfter;
        }

        public String getDate() {
            return date;
        }

        public String getName() {
            return name;
        }

        public String getTransactionType() {
            return transactionType;
        }

        public float getAmount() {
            return amount;
        }

        public float getBalanceAfter() {
            return balanceAfter;
        }
    }

    // Method to fetch transactions for a given username
    public List<TransactionDetails> getTransactionsByUsername(String username) {
        List<TransactionDetails> transactionList = new ArrayList<>();

        // SQL query to get transactions by username
        String query = "SELECT DATE_FORMAT(t.created_at, '%d/%m/%Y') AS transaction_date, " +
                       "COALESCE(u.full_name, 'Added') AS full_name, " +
                       "t.transaction_type, " +
                       "t.amount, " +
                       "t.balance_after_sender " +
                       "FROM Transactions t " +
                       "JOIN Accounts a ON t.account_id = a.account_id " +
                       "LEFT JOIN Users u ON t.to_account_id = u.user_id " +
                       "WHERE a.user_id = (SELECT user_id FROM Users WHERE username = ?)";
        
        try (Connection connection = MySQLConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username); // Bind the username to the query
            ResultSet rs = preparedStatement.executeQuery();

            // Iterate through the result set and populate the list
            while (rs.next()) {
                String date = rs.getString("transaction_date"); 
                String name = rs.getString("full_name");
                String transactionType = rs.getString("transaction_type").equals("deposit") ? "CR" : "DR";
                float amount = rs.getFloat("amount");
                float balanceAfter = rs.getFloat("balance_after_sender");

                // Create a TransactionDetails object and add it to the list
                transactionList.add(new TransactionDetails(date, name, transactionType, amount, balanceAfter));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactionList; 
    }
    
}


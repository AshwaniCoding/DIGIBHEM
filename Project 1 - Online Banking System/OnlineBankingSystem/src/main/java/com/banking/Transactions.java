/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.banking;

import static com.banking.HomePage.loginUserName;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class Transactions {
    
    DefaultTableModel tableModel;

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

    public List<TransactionDetails> getAllTransactions(String username) {
        List<TransactionDetails> transactionList = new ArrayList<>();

        // SQL query to get received money transactions
        String queryReceiveMoney = "SELECT " +
                "    t.created_at AS transaction_date, " +
                "    (SELECT u.full_name FROM users u WHERE u.user_id = (SELECT a.user_id FROM accounts a WHERE a.account_id = t.from_account_id)) AS full_name, " +
                "    t.transaction_type, " +
                "    t.amount, " +
                "    t.balance_after_receiver AS balance " +
                "FROM " +
                "    transactions t " +
                "JOIN " +
                "    accounts a ON t.to_account_id = a.account_id " +
                "JOIN " +
                "    users u ON a.user_id = u.user_id " +
                "WHERE " +
                "    u.username = ? " + 
                "    AND EXISTS (SELECT 1 FROM users u2 WHERE u2.user_id = (SELECT a2.user_id FROM accounts a2 WHERE a2.account_id = t.from_account_id) AND u2.full_name IS NOT NULL);";

        // SQL query to get transferred/added money transactions
        String queryTransferOrAddMoney = "SELECT " +
                "    t.created_at AS transaction_date, " +
                "    CASE " +
                "        WHEN t.account_id = t.from_account_id AND t.account_id != t.to_account_id AND t.from_account_id != t.to_account_id " +
                "            THEN (SELECT u.full_name FROM users u WHERE u.user_id = (SELECT a.user_id FROM accounts a WHERE a.account_id = t.to_account_id)) " +
                "        WHEN t.account_id != t.to_account_id AND t.account_id != t.from_account_id AND t.from_account_id != t.to_account_id " +
                "            THEN (SELECT u.full_name FROM users u WHERE u.user_id = (SELECT a.user_id FROM accounts a WHERE a.account_id = t.from_account_id)) " +
                "        WHEN t.account_id = t.to_account_id AND t.from_account_id IS NULL " +
                "            THEN 'Added Money' " +
                "    END AS full_name, " +
                "    t.transaction_type, " +
                "    t.amount, " +
                "    CASE " +
                "        WHEN t.account_id = t.from_account_id AND t.account_id != t.to_account_id THEN t.balance_after_sender " +
                "        WHEN t.account_id = t.to_account_id AND t.account_id != t.from_account_id THEN t.balance_after_receiver " +
                "        ELSE t.balance_after_sender " +
                "    END AS balance " +
                "FROM " +
                "    transactions t " +
                "JOIN " +
                "    accounts a ON t.account_id = a.account_id " +
                "JOIN " +
                "    users u ON a.user_id = u.user_id " +
                "WHERE " +
                "    u.username = ?";  

        try (Connection connection = MySQLConnection.getConnection()) {
            // First query: Receiving money
            try (PreparedStatement preparedStatementReceive = connection.prepareStatement(queryReceiveMoney)) {
                preparedStatementReceive.setString(1, username);
                ResultSet rsReceive = preparedStatementReceive.executeQuery();

                while (rsReceive.next()) {
                    String date = rsReceive.getString("transaction_date");
                    String name = rsReceive.getString("full_name");
                    String transactionType = "CR"; // Receiving money is always credit
                    float amount = rsReceive.getFloat("amount");
                    float balanceAfter = rsReceive.getFloat("balance");

                    transactionList.add(new TransactionDetails(date, name, transactionType, amount, balanceAfter));
                }
            }

            // Second query: Transferring/Adding money
            try (PreparedStatement preparedStatementTransfer = connection.prepareStatement(queryTransferOrAddMoney)) {
                preparedStatementTransfer.setString(1, username);
                ResultSet rsTransfer = preparedStatementTransfer.executeQuery();

                while (rsTransfer.next()) {
                    String date = rsTransfer.getString("transaction_date");
                    String name = rsTransfer.getString("full_name");
                    String transactionType = rsTransfer.getString("transaction_type").equals("deposit") ? "CR" : "DR";
                    float amount = rsTransfer.getFloat("amount");
                    float balanceAfter = rsTransfer.getFloat("balance");

                    transactionList.add(new TransactionDetails(date, name, transactionType, amount, balanceAfter));
                }
            }

            // Sort the final transaction list by transaction_date
            transactionList.sort(Comparator.comparing(TransactionDetails::getDate));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactionList;
    }
    
    public void transactionData(){
    
        tableModel = (DefaultTableModel) HomePage.transactionsTable.getModel();
        tableModel.setRowCount(0);  

        // Fetch the transactions for the given username
        List<Transactions.TransactionDetails> transactionList = getAllTransactions(loginUserName);

        // Date format patterns
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy"); 
        
        // Populate the table with transaction data
        for (int i = transactionList.size() - 1; i >= 0; i--) {
            String formattedDate = transactionList.get(i).getDate(); 
            try {
                Date date = inputFormat.parse(transactionList.get(i).getDate()); 
                formattedDate = outputFormat.format(date); 
            } catch (ParseException e) {
                e.printStackTrace(); 
            }

            // Add the formatted date and other details to the table
            tableModel.addRow(new Object[]{
                formattedDate, 
                transactionList.get(i).getName(),
                transactionList.get(i).getTransactionType(),
                transactionList.get(i).getAmount(),
                transactionList.get(i).getBalanceAfter()
            });
        }
    }
    
    public void getTwotransactionData(){

        // Fetch the transactions for the given username
        List<Transactions.TransactionDetails> transactionList = getAllTransactions(loginUserName);
        
        int size = transactionList.size();

        HomePage.homeTransactionName1.setText(transactionList.get(size-1).getName());
        if(transactionList.get(size-1).getTransactionType().equals("CR")){        
            HomePage.homeTransactionAmount1.setText("+ ₹" + String.valueOf(transactionList.get(size-1).getAmount()));
        }else{
            HomePage.homeTransactionAmount1.setText("- ₹" + String.valueOf(transactionList.get(size-1).getAmount()));
        }
            
        HomePage.homeTransactionName2.setText(transactionList.get(size-2).getName());
        if(transactionList.get(size-2).getTransactionType().equals("CR")){        
            HomePage.homeTransactionAmount2.setText("+ ₹" + String.valueOf(transactionList.get(size-2).getAmount()));
        }else{
            HomePage.homeTransactionAmount2.setText("- ₹" + String.valueOf(transactionList.get(size-2).getAmount()));
        }
    }
    
}


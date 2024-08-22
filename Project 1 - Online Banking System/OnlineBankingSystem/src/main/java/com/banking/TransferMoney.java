/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.banking;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author Ashu
 */
public class TransferMoney {
    
    public void removeTransferLabel() {
        HomePage.tmAcNo.setText("");
        HomePage.tmCfAcNo.setText("");
        HomePage.tmIfsc.setText("");
        HomePage.tmFullName.setText("");
        HomePage.tmAmount.setText("");
        HomePage.tmDesc.setText("");
    }

    public boolean transferMoney(int fromAccountId, String toAccountNumber, String confirmToAccountNumber, String ifscCode, String fullName, double amount, String description) {
        if (!toAccountNumber.equals(confirmToAccountNumber)) {
            JOptionPane.showMessageDialog(null, "The account numbers do not match.");
            return false;
        }

        String deductMoneyQuery = "UPDATE Accounts SET balance = balance - ? WHERE account_id = ?";
        String addMoneyQuery = "UPDATE Accounts SET balance = balance + ? WHERE account_number = ?";
        String insertTransactionQuery = "INSERT INTO Transactions (account_id, from_account_id, to_account_id, transaction_type, amount, balance_after_sender, balance_after_receiver, description) VALUES ( ?, ?, ?, 'transfer', ?, ?, ?, ?)";

        try (Connection connection = MySQLConnection.getConnection()) {
            connection.setAutoCommit(false);  // Start transaction

            double senderBalance;

            // Verify sender's account
            String verifySenderQuery = "SELECT account_number, balance FROM Accounts WHERE account_id = ?";
            try (PreparedStatement senderStmt = connection.prepareStatement(verifySenderQuery)) {
                senderStmt.setInt(1, fromAccountId);
                ResultSet senderRs = senderStmt.executeQuery();

                if (senderRs.next()) {
                    String fromAccountNumber = senderRs.getString("account_number");
                    senderBalance = senderRs.getDouble("balance");

                    // Deduct money from the sender's account
                    if (senderBalance >= amount) {
                        try (PreparedStatement deductStmt = connection.prepareStatement(deductMoneyQuery)) {
                            deductStmt.setDouble(1, amount);
                            deductStmt.setInt(2, fromAccountId);
                            int rowsAffected = deductStmt.executeUpdate();
                            if (rowsAffected == 0) {
                                throw new SQLException("Failed to deduct money from the sender's account.");
                            }
                        }

                        int toAccountId = -1;

                        // Verify receiver's account details
                        String verifyReceiverQuery = "SELECT a.account_id, a.balance, b.full_name, a.IFSC_code FROM Accounts a join Users b on a.user_id = b.user_id WHERE account_number = ?";
                        try (PreparedStatement receiverStmt = connection.prepareStatement(verifyReceiverQuery)) {
                            receiverStmt.setString(1, toAccountNumber);
                            ResultSet receiverRs = receiverStmt.executeQuery();

                            if (receiverRs.next()) {
                                toAccountId = receiverRs.getInt("account_id");
                                double receiverBalance = receiverRs.getDouble("balance");
                                String receiverFullName = receiverRs.getString("full_name");
                                String receiverIFSCCode = receiverRs.getString("IFSC_code");

                                // Validate receiver's full name and IFSC code
                                if (!receiverFullName.equalsIgnoreCase(fullName) || !receiverIFSCCode.equals(ifscCode)) {
                                    JOptionPane.showMessageDialog(null, "Receiver's full name or IFSC code does not match.");
                                    return false;
                                }

                                // Add money to the receiver's account
                                try (PreparedStatement addStmt = connection.prepareStatement(addMoneyQuery)) {
                                    addStmt.setDouble(1, amount);
                                    addStmt.setString(2, toAccountNumber);
                                    int rowsAffected = addStmt.executeUpdate();
                                    if (rowsAffected == 0) {
                                        throw new SQLException("Failed to add money to the receiver's account.");
                                    }
                                }

                                // Update the sender's balance after the transfer
                                senderBalance -= amount;

                                // Log the transaction for the sender and receiver
                                try (PreparedStatement transactionStmt = connection.prepareStatement(insertTransactionQuery)) {
                                    transactionStmt.setInt(1, fromAccountId);
                                    transactionStmt.setInt(2, fromAccountId);
                                    transactionStmt.setInt(3, toAccountId);
                                    transactionStmt.setDouble(4, amount);
                                    transactionStmt.setDouble(5, senderBalance);
                                    transactionStmt.setDouble(6, receiverBalance + amount);  // Updated receiver balance
                                    transactionStmt.setString(7, description);
                                    transactionStmt.executeUpdate();
                                }

                            } else {
                                JOptionPane.showMessageDialog(null, "Receiver's account not found.");
                                return false;
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "insufficient Balance!");
                        return false;
                    }

                } else {
                    JOptionPane.showMessageDialog(null, "Sender's account not found.");
                    return false;
                }
            }

            connection.commit();  // Commit transaction
            JOptionPane.showMessageDialog(null, "Transfer successful");
            removeTransferLabel();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Transfer failed: " + e.getMessage());
            try {
                Connection connection = MySQLConnection.getConnection();
                connection.rollback();  // Rollback transaction on failure
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            return false;
        } finally {
            try {
                Connection connection = MySQLConnection.getConnection();
                connection.setAutoCommit(true);  // Restore auto-commit mode
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
